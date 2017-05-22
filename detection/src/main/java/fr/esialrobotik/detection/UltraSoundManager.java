package fr.esialrobotik.detection;


import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.detection.DetectionInterface;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.MovementManager;
import fr.esialrobotik.data.table.Table;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Created by icule on 12/05/17.
 */
public class UltraSoundManager {
    private DetectionInterface detectionInterface;
    private Logger logger;
    private int threshold = 300;

    private Thread thread;
    private int detection;

    private MovementManager movementManager;
    private Table table;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface, Table table, MovementManager movementManager) {
        detection = 0;
        LoggerFactory.init(Level.TRACE);
        logger = LoggerFactory.getLogger(UltraSoundManager.class);

        this.movementManager = movementManager;
        this.detectionInterface = detectionInterface;
        this.table = table;

    }

    public void start() {
        thread = new Thread(new Runnable() {
            public void run() {
                while(!thread.isInterrupted()){
                    int tempDetection = 0;
                    final long[] pull = detectionInterface.ultraSoundDetection();
                    Position position = movementManager.getPosition();
                    int x, y;

                    //First one is front left
                    if(pull[0] < threshold) {
                        x = (int) (position.getX() + 130 + Math.cos(position.getTheta() + Math.PI/6) * pull[0]);
                        y = (int) (position.getY() + 140 + Math.sin(position.getTheta() + Math.PI/6) * pull[0]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                           tempDetection += 1;
                        }
                    }

                    //frnt middle
                    if(pull[1] < threshold) {
                        x = (int) (position.getX() + 130 + Math.cos(position.getTheta()) * pull[1]);
                        y = (int) (position.getY() + Math.sin(position.getTheta()) * pull[1]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                            tempDetection += 2;
                        }
                    }

                    //frnt right
                    if(pull[2] < threshold) {
                        x = (int) (position.getX() + 130 + Math.cos(position.getTheta() - Math.PI/6) * pull[2]);
                        y = (int) (position.getY() - 140 + Math.sin(position.getTheta() - Math.PI/6) * pull[2]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                            tempDetection += 4;
                        }
                    }

                    //back middle
                    if(pull[3] < threshold) {
                        x = (int) (position.getX() - 130 - Math.cos(position.getTheta()) * pull[1]);
                        y = (int) (position.getY() - Math.sin(position.getTheta()) * pull[1]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                            tempDetection += 8;
                        }
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
