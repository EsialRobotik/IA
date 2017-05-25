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
    private boolean[] detection;

    private MovementManager movementManager;
    private Table table;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface, Table table, MovementManager movementManager) {
        detection = new boolean[4];
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
                    boolean[] tempDetection = new boolean[4];
                    final long[] pull = detectionInterface.ultraSoundDetection();
                    Position position = movementManager.getPosition();
                    int x, y;

                    //First one is front left
                    logger.debug("Avant gauche : " + pull[0]);
                    if(pull[0] < threshold) {
//                        x = (int) (position.getX() + 130 + Math.cos(position.getTheta() + Math.PI/6) * pull[0]);
//                        y = (int) (position.getY() + 140 + Math.sin(position.getTheta() + Math.PI/6) * pull[0]);
//                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
//                           tempDetection[0] = true;
//                        }
                    }

                    //frnt middle
                    logger.debug("Avant milieu : " + pull[1]);
                    if(pull[1] < threshold) {
//                        x = (int) (position.getX() + 130 + Math.cos(position.getTheta()) * pull[1]);
//                        y = (int) (position.getY() + Math.sin(position.getTheta()) * pull[1]);
//                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
//                            tempDetection[1] = true;
//                        }
                    }

                    //frnt right
                    logger.debug("Avant droit : " + pull[2]);
                    if(pull[2] < threshold) {
                        x = (int) (position.getX() + /*130*/100 + Math.cos(position.getTheta()/* - Math.PI/6*/) * pull[2]);
                        y = (int) (position.getY() /*- 140*/ + Math.sin(position.getTheta()/* - Math.PI/6*/) * pull[2]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                            tempDetection[2] = true;
                        }
                    }

                    //back middle
                    logger.debug("Arriere : " + pull[3]);
                    if(pull[3] < threshold) {
                        x = (int) (position.getX() - /*130*/100 - Math.cos(position.getTheta()) * pull[1]);
                        y = (int) (position.getY() - Math.sin(position.getTheta()) * pull[1]);
                        if(!table.isAreaForbiddenSafe(x / 10, y / 10)) {
                            tempDetection[3] = true;
                        }
                    }
                    detection = tempDetection;
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
        return detection[0] || detection[1] || detection[3] || detection[4];
    }

    public boolean[] getDetectionResult() {
        return this.detection;
    }
}
