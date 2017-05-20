package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.pathFinding.PathFinding;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Guillaume on 17/05/2017.
 */
public class MasterLoop {
  private MovementManager movementManager;
  private DetectionManager detectionManager;
  private ActionCollection actionCollection;
  private ActionDescriptor currentAction;
  private PathFinding pathFinding;
  private ColorDetector colorDetector;
  private Chrono chrono;

  private boolean interrupted;

  @Inject
  public MasterLoop(MovementManager movementManager,
                    DetectionManager detectionManager,
                    ActionCollection actionCollection,
                    PathFinding pathFinding,
                    ColorDetector colorDetector,
                    Chrono chrono) {
    this.movementManager = movementManager;
    this.detectionManager = detectionManager;
    this.actionCollection = actionCollection;
    this.pathFinding = pathFinding;
    this.colorDetector = colorDetector;
    this.chrono = chrono;

    this.interrupted = true;
  }


  //NOTE robot is at starting point before reaching this
  public void mainLoop() {
    //When we arrived here everything is set up so we just need to launch the first path finding,
    // and wait for the beginning of the match

    // FIRST COMPUTATION HERE
    // 1/ We pull the first action to do
    currentAction = actionCollection.getNextActionToPerform();
    Step step = currentAction.getNextStep(); //Should not be null

    // 2/ We launch the Astar (to spare time)
    launchAstar(positionToPoint(step.getEndPosition()));
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

    // 5/ start of the timer start the main loop
    chrono.startMatch(this);
    movementManager.resumeAsserv();


    while(!interrupted) {
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

        // 2/ Check if the current task Status
        //   a if an action was being executed and have finished => let's start the new one
        //   b A path was being calculate, let's check if the computation is terminated => let's send it to asserv
      }
    }
  }

  //Function to be call to set up the robot and lead him to starting point
  public void init() {
    // Calage bordure

    // Wait tirette remise
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
