package fr.esialrobotik;

import esialrobotik.ia.actions.ActionExecutor;
import esialrobotik.ia.actions.ActionInterface;
import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.pathFinding.PathFinding;

import javax.inject.Inject;

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

  private ActionInterface actionInterface;
  private ActionDescriptor currentAction;
  private ActionExecutor currentActionExecutor;
  private Step currentStep;


  @Inject
  public MasterLoop(MovementManager movementManager,
                    DetectionManager detectionManager,
                    ActionCollection actionCollection,
                    ActionInterface actionInterface,
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
    this.actionInterface = actionInterface;

    this.interrupted = true;
  }


  //NOTE robot is at starting point before reaching this
  public boolean mainLoop() {
    //When we arrived here everything is set up so we just need to launch the first path finding,
    // and wait for the beginning of the match
    boolean everythingDone = false;
    boolean astarLaunch = false;

    // FIRST COMPUTATION HERE
    // 1/ We pull the first action to do
    currentAction = actionCollection.getNextActionToPerform();
    currentStep = currentAction.getNextStep(); //Should not be null
    currentActionExecutor = null;

    // 2/ We launch the Astar (to spare time)
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

    // 4/ We wait for the beginning of the match
    tirette.waitForTirette(false);

    // 5/ start of the timer start the main loop
    chrono.startMatch(this);
    movementManager.resumeAsserv();


    while(!interrupted && !everythingDone) {
      // 1/ we check if we detect something
      int detected = this.detectionManager.getEmergencyDetectionMap();
      if(detected != 0) {
        //We detect something, we get the movement direction and we check if we detect it in the right side
        AsservInterface.MovementDirection direction = this.movementManager.getMovementDirection();

        if(direction == AsservInterface.MovementDirection.FORWARD
                && (detected & 0x7) != 0) {
          //We detect something. That's horrible

        }
        else if (direction == AsservInterface.MovementDirection.BACKWARD
                && (detected & 0x8) != 0) {
          // something is sneaking on us, grab the rocket launcher
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
        currentActionExecutor = null;
        currentStep = null;
        //Time to fetch the next one
        if(currentAction.hasNextStep()) {
          currentStep = currentAction.getNextStep();
        }
        else { //Previous action has ended, time to fetch a new one
          currentAction = actionCollection.getNextActionToPerform();
          if(currentAction == null) {//Nothing more to do. #sadness
            everythingDone = true;
          }
          else {
            currentStep = currentAction.getNextStep();
          }
        }
        //TODO add other type
        if(currentStep.getActionType() == Step.Type.MANIPULATION) {
          currentActionExecutor = actionInterface.getActionExecutor(currentStep.getActionId());
          currentActionExecutor.execute();
        }
        else if(currentStep.getActionType() == Step.Type.DEPLACEMENT){
          // We need to launch the astar
          launchAstar(positionToPoint(currentStep.getEndPosition()));
          astarLaunch = true;
        }
      }
    }

    return !interrupted;
  }

  //This function could be simplified but at least it keeps things readeable
  private boolean currentStepEnded() {
    //TODO add other action type
    if(currentStep.getActionType() == Step.Type.DEPLACEMENT && movementManager.isLastOrderedMovementEnded()) {
      return true;
    }
    else if(currentActionExecutor != null //A bit defensive but who cares
            && currentStep.getActionType() == Step.Type.MANIPULATION && currentActionExecutor.finished()) {
      return true;
    }
    return false;
  }

  //Function to be call to set up the robot and lead him to starting point
  public void init() {
    // Calage bordure

    // Wait tirette remise
    tirette.waitForTirette(true);
  }

  public void matchEnd() {
    //Stop the asserv here

    interrupted = true;

    //Launch the funny action if needed
  }

  //Start the computation of the path.
  private void launchAstar(Point destination) {
    pathFinding.computePath(positionToPoint(movementManager.getPosition()), destination);
  }

  private Point positionToPoint(Position p) {
    return new Point(p.getX(), p.getY());
  }
}
