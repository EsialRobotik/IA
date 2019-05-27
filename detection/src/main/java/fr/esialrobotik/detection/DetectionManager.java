package fr.esialrobotik.detection;

import esialrobotik.ia.detection.DetectionInterface;

import javax.inject.Inject;

/**
 * Created by icule on 12/05/17.
 */
public class DetectionManager {
    private LidarManager lidarManager;
    private UltraSoundManager ultraSoundManager;
    private DetectionInterface detectionInterface;
    
    @Inject
    public DetectionManager(DetectionInterface detectionInterface, LidarManager lidarManager, UltraSoundManager ultraSoundManager) {
        this.lidarManager = lidarManager;
        this.ultraSoundManager = ultraSoundManager;
        this.detectionInterface = detectionInterface;
    }

    public void initAPI() {
        this.detectionInterface.init();
    }

    public void startDetection() {
        this.ultraSoundManager.start();
//        this.lidarManager.start();
    }

    public void stopDetection() {
        this.ultraSoundManager.stop();
//        this.lidarManager.stop();
    }

    public boolean robotDetectInPath() {
        //Process the result from the LIDAR here
        return false;
    }

    public void startDetectionDebug() {
        this.ultraSoundManager.startDebug();
        this.lidarManager.start();
    }

    public boolean emergencyDetection() {
        return this.ultraSoundManager.hasBeenDetected();
    }

    public boolean[] getEmergencyDetectionMap() {
        return this.ultraSoundManager.getDetectionResult();
    }
}
