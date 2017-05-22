package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.TableColor;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.pathFinding.PathFinding;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Collections;

/**
 * Created by Guillaume on 17/05/2017.
 */
public class MasterLoop {
  private MovementManager movementManager;
  private DetectionManager detectionManager;
  private ActionCollection actionCollection;
  private PathFinding pathFinding;
  private ColorDetector colorDetector;
  private Chrono chrono;
  private Tirette tirette;

  private boolean interrupted;

  private ActionSupervisor actionSupervisor;
  private ActionDescriptor currentAction;
  private Step currentStep;

  private Logger logger;


  @Inject
  public MasterLoop(MovementManager movementManager,
                    DetectionManager detectionManager,
                    ActionCollection actionCollection,
                    ActionSupervisor actionSupervisor,
                    PathFinding pathFinding,
                    ColorDetector colorDetector,
                    Chrono chrono,
                    Tirette tirette) {
    this.movementManager = movementManager;
    this.detectionManager = detectionManager;
    this.actionCollection = actionCollection;
    this.pathFinding = pathFinding;
    this.colorDetector = colorDetector;
    this.chrono = chrono;
    this.tirette = tirette;
    this.actionSupervisor = actionSupervisor;

    this.interrupted = true;
    this.logger = LoggerFactory.getLogger(MasterLoop.class);
  }


  //NOTE robot is at starting point before reaching this
  public boolean mainLoop() {
    logger.info("Begin of main loop");
    //When we arrived here everything is set up so we just need to launch the first path finding,
    // and wait for the beginning of the match
    boolean everythingDone = false;
    boolean astarLaunch = false;
    boolean somethingDetected = false;
    boolean movingForward = false;

    // FIRST COMPUTATION HERE
    // 1/ We pull the first action to do
    currentAction = actionCollection.getNextActionToPerform();
    currentStep = currentAction.getNextStep(); //Should not be null

    logger.info("Fetch of first acction");
    //The first action is always a move straight or a path finding issue
    // 2/ We launch the Astar (to spare time) if we have a non always.
    if(currentStep.getActionType() == Step.Type.DEPLACEMENT) {
      logger.info("First action is a deplacement with astar, let's start computation");
      launchAstar(positionToPoint(currentStep.getEndPosition()));
      while(!pathFinding.isComputationEnded()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
          //We got interrupted, things are bad
        }
      }
      // 3/ We send command to the asserv manager
      movementManager.executeMovement(pathFinding.getLastComputedPath());
    }
    else {//Straight line
      logger.info("First deplacement is a straight line");
      movementManager.executeMovement(Collections.singletonList(positionToPoint(currentStep.getEndPosition())));
    }

    logger.info("Trajectory load, let's wait for tirette");
    // 4/ We wait for the beginning of the match
    tirette.waitForTirette(false);

    // 5/ start of the timer start the main loop
    logger.info("Tirette pull, begin of the match");
    chrono.startMatch(this);
    movementManager.resumeAsserv();


    while(!interrupted) {
      if(!somethingDetected) {
        // 1/ we check if we detect something
        int detected = this.detectionManager.getEmergencyDetectionMap();
        if(detected != 0) {
          //We detect something, we get the movement direction and we check if we detect it in the right side
          AsservInterface.MovementDirection direction = this.movementManager.getMovementDirection();

          if(direction == AsservInterface.MovementDirection.FORWARD
                  && (detected & 0x7) != 0) {
            //We detect something. That's horrible
            movementManager.haltAsserv(true);
            movingForward = true;

          }
          else if (direction == AsservInterface.MovementDirection.BACKWARD
                  && (detected & 0x8) != 0) {
            // something is sneaking on us, grab the rocket launcher
            movementManager.haltAsserv(true);
            movingForward = false;
          }
        }

        // 2/ Check if the current step Status
        if(astarLaunch) { //We are computing a path let's check if it's ok now
          if(pathFinding.isComputationEnded()) {
            movementManager.executeMovement(pathFinding.getLastComputedPath());
            astarLaunch = false;
          }
        }
        else if(currentStepEnded()) { //There is few chance we end the deplacement that soon so don't check
          currentStep = null;
          //Time to fetch the next one
          if(currentAction.hasNextStep()) {
            currentStep = currentAction.getNextStep();
          }
          else { //Previous action has ended, time to fetch a new one
            currentAction = actionCollection.getNextActionToPerform();
            if(currentAction == null) {//Nothing more to do. #sadness
              break;
            }
            else {
              currentStep = currentAction.getNextStep();
            }
          }
          //Switch... switch... switch, yeah I heard about htem once, but never met :P
          if(currentStep.getActionType() == Step.Type.MANIPULATION) {
            actionSupervisor.executeCommand(currentStep.getActionId());
          }
          else if(currentStep.getActionType() == Step.Type.DEPLACEMENT){
            // We need to launch the astar
            launchAstar(positionToPoint(currentStep.getEndPosition()));
            astarLaunch = true;
          }
          else if(currentStep.getActionType() == Step.Type.ROTATION) {
            movementManager.setCap(currentStep.getEndPosition());
          }
          else if(currentStep.getActionType() == Step.Type.DEPLACEMENT_ALWAYS) {//Forward until death
            movementManager.executeMovement(Collections.singletonList(positionToPoint(currentStep.getEndPosition())));
          }
        }
      }
      else { //We detect something last loop. let's check if we still see it, either let's resume the move
        //If we want to put smart code, it's here
        int detected = this.detectionManager.getEmergencyDetectionMap();
        if(movingForward && ((detected & 0x7) == 0)) {
          movementManager.resumeAsserv();
          somethingDetected = false;
        }
        else if(!movingForward && ((detected & 0x8) == 0)) {
          movementManager.resumeAsserv();
          somethingDetected = false;
        }
      }
    }


    return !interrupted;
  }

  //This function could be simplified but at least it keeps things readeable
  private boolean currentStepEnded() {
    Step.Type type = currentStep.getActionType();
    if((type == Step.Type.DEPLACEMENT || type == Step.Type.DEPLACEMENT_ALWAYS || type == Step.Type.ROTATION)
            && movementManager.isLastOrderedMovementEnded()) {
      return true;
    }
    else if(type == Step.Type.MANIPULATION && actionSupervisor.isLastExecutionFinished()) {
      return true;
    }
    return false;
  }

  //Function to be call to set up the robot and lead him to starting point
  public void init() {
    logger.info("Init mainLoop");

    // Calage bordure
    // TODO Ecrire "Tirette pour callage bordure + couleur" sur le lcd
    logger.info("Attente mise en place tirette pour init callage");
    tirette.waitForTirette(true);
    logger.info("Attente retrait tirette pour init callage");
    tirette.waitForTirette(false);
    logger.info("Start calage bordure");
    // TODO Ecrire "Lanceent callage bordure" sur le lcd
    movementManager.callage(colorDetector.getSelectedColor() == TableColor.BLUE); // TODO l'ia ne devrait pas connaitre les couleurs mais seulement le sens du Y

    // Wait tirette remise
    // TODO Ecritre "Attente remise tirette" sur le LCD
    logger.info("Init ended, wait for tirette");
    tirette.waitForTirette(true);
    logger.info("Tirette inserted. End of initialization.");
    // TODO Ecrire "Attente retrait tirette pour lancement match" sur le LCD
  }

  public void matchEnd() {
    //Stop the asserv here
    movementManager.haltAsserv(false);

    //Don't forget action

    interrupted = true;
    //Launch the funny action if needed
  }

  //Start the computation of the path.
  private void launchAstar(Point destination) {
    pathFinding.computePath(positionToPoint(movementManager.getPosition()), destination);
  }

  public boolean isMatchFinished() {
    return this.interrupted;
  }

  private Point positionToPoint(Position p) {
    return new Point(p.getX(), p.getY());
  }
}
