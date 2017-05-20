package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.pathFinding.PathFinding;

import javax.inject.Inject;

/**
 * Created by Guillaume on 17/05/2017.
 */
public class MasterLoop implements Runnable {
  private AsservInterface asservInterface;
  private DetectionManager detectionManager;
  private ActionCollection actionCollection;
  private ActionDescriptor currentAction;
  private PathFinding pathFinding;

  private boolean interrupted;

  @Inject
  public MasterLoop(AsservInterface asservInterface, DetectionManager detectionManager, ActionCollection actionCollection, PathFinding pathFinding) {
    this.asservInterface = asservInterface;
    this.detectionManager = detectionManager;
    this.actionCollection = actionCollection;
    this.pathFinding = pathFinding;

    this.interrupted = true;
  }



  public void run() {
    //When we arrived here everything is set up so we just need to launch the first path finding,
    // and wait for the beginning of the match

    // FIRST COMPUTATION HERE
    // 1/ We pull the first action to do
    currentAction = actionCollection.getNextActionToPerform();
    Step step = currentAction.getNextStep(); //Should not be null

    // 2/ We launch the Astar (to spare time)

    while(!pathFinding.isComputationEnded()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        //We got interrupted, things are bad
      }
    }
    // 3/ We send command to the asserv manager

    // 4/ We wait for the beginning of the match

    // 5/ start of the timer start the main loop

    while(!interrupted) {
      // 1/ we check if we detect something
      int detected = this.detectionManager.getEmergencyDetectionMap();
      if(detected != 0) {
        //We detect something, we get the movement direction and we check if we detect it in the right side
        AsservInterface.MovementDirection direction = this.asservInterface.getMovementDirection();

        if(direction == AsservInterface.MovementDirection.FORWARD
                && (detected & 0x7) != 0) {
          //We detect something. That's horrible

        }
        else if (direction == AsservInterface.MovementDirection.BACKWARD
                && (detected & 0x8) != 0) {
          // something is sneaking on us, grab the rocket launcher
        }
      }

      // 2/ if the timer ended


      // 3/ Check if the current task Status
      //   a if an action was being executed and have finished => let's start the new one
      //   b A path was being calculate, let's check if the computation is terminated => let's send it to asserv

    }
  }
}
