package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import fr.esialrobotik.detection.DetectionManager;

/**
 * Created by Guillaume on 17/05/2017.
 */
public class MasterLoop implements Runnable {
  private AsservInterface asservInterface;
  private DetectionManager detectionManager;

  private boolean interrupted;

  public MasterLoop(AsservInterface asservInterface, DetectionManager detectionManager) {
    this.asservInterface = asservInterface;
    this.detectionManager = detectionManager;

    this.interrupted = true;
  }



  public void run() {
    //When we arrived here everything is set up so we just need to launch the first path finding,
    // and wait for the beginning of the match

    // FIRST COMPUTATION HERE

    while(!interrupted) {
      //First we check if we detect something
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
    }
  }
}
