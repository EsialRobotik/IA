package fr.esialrobotik.detection;


import esialrobotik.ia.detection.DetectionInterface;
import esialrobotik.ia.utils.log.LoggerFactory;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Created by icule on 12/05/17.
 */
public class UltraSoundManager {
    private DetectionInterface detectionInterface;
    private Logger logger;
    private int threshold;

    private Thread thread;
    private int detection;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface) {
        detection = 0;
        logger = LoggerFactory.getLogger(UltraSoundManager.class);
    }

    public void start() {
        thread = new Thread(new Runnable() {
            public void run() {
                while(!thread.isInterrupted()){
                    int tempDetection = 0;
                    int mask = 1;
                    final long[] pull = detectionInterface.ultraSoundDetection();
                    for(long value : pull) {
                        if (value < threshold) {
                            tempDetection |= mask;
                        }
                        mask <<= mask;
                    }
                    detection = tempDetection;
                    logger.info("Ultra sound detection result " + detection);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
        try{
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean hasBeenDetected() {
        return detection != 0;
    }

    public int getDetectionResult() {
        return this.detection;
    }
}
