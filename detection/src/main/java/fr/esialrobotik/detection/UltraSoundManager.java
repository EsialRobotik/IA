package fr.esialrobotik.detection;


import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.detection.DetectionInterface;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.MovementManager;
import fr.esialrobotik.data.table.Table;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;

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

    private volatile boolean interrupted = false;

    private Position posFrontLeft;
    private Position posFront;
    private Position posFrontRight;
    private Position posBack;

    private HashMap<String, Integer> thresholdMap;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface, Table table, MovementManager movementManager) {
        detection = new boolean[4];
        LoggerFactory.init(Level.TRACE);
        logger = LoggerFactory.getLogger(UltraSoundManager.class);

        this.movementManager = movementManager;
        this.detectionInterface = detectionInterface;
        this.table = table;

        this.posFrontLeft = new Position(125, 105, 0);
        this.posFront = new Position(125, -0, 0);
        this.posFrontRight = new Position(125, -105, 0);
        this.posBack = new Position(-125, 0, Math.PI);

        this.thresholdMap = new HashMap<>();
        this.thresholdMap.put("FrontLeft", 150);
        this.thresholdMap.put("Front", 300);
        this.thresholdMap.put("FrontRight", 200);
        this.thresholdMap.put("Back", 300);
    }

    private static Position getObstaclePosition(Position posRobot, Position posDetector, long distance) {
        double xObstacleRelativeToRobot, yObstacleRelativeToRobot;
        int xObstacleRelativeToTable, yObstacleRelativeToTable;

        xObstacleRelativeToRobot = posDetector.getX() + distance * Math.cos(posDetector.getTheta());
        yObstacleRelativeToRobot = posDetector.getY() + distance * Math.sin(posDetector.getTheta());

        xObstacleRelativeToTable = (int) (posRobot.getX()
                + xObstacleRelativeToRobot * Math.cos(posRobot.getTheta())
                - yObstacleRelativeToRobot * Math.sin(posRobot.getTheta()) );

        yObstacleRelativeToTable = (int) (posRobot.getY()
                + xObstacleRelativeToRobot * Math.sin(posRobot.getTheta())
                + yObstacleRelativeToRobot * Math.cos(posRobot.getTheta()) );

        return new Position(xObstacleRelativeToTable, yObstacleRelativeToTable);
    }

    public void start() {
        thread = new Thread(() -> {
            while(!interrupted){
                boolean[] tempDetection = new boolean[4];
                final long[] pull = detectionInterface.ultraSoundDetection();
                Position position = movementManager.getPosition();

                //First one is front left
                if(pull[0] < thresholdMap.get("FrontLeft")) {
                    Position pos = getObstaclePosition(position, posFrontLeft, pull[0]);
                    logger.debug("Ultrasound Avant gauche : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                       tempDetection[0] = true;
                        logger.debug("Ultrasound Avant gauche : STOP");
                    } else {
                        logger.debug("Ultrasound Avant gauche : IGNORER");
                    }
                }

                //front middle
                if(pull[1] < thresholdMap.get("Front")) {
                    Position pos = getObstaclePosition(position, posFront, pull[1]);
                    logger.debug("Ultrasound Avant milieu : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[1] = true;
                        logger.debug("Ultrasound Avant milieu : STOP");
                    } else {
                        logger.debug("Ultrasound Avant milieu : IGNORER");
                    }
                }

                //front right
                if(pull[2] < thresholdMap.get("FrontRight")) {
                    Position pos = getObstaclePosition(position, posFrontRight, pull[2]);
                    logger.debug("Ultrasound Avant droit : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[2] = true;
                        logger.debug("Ultrasound Avant droit : STOP");
                    } else {
                        logger.debug("Ultrasound Avant droit : IGNORER");
                    }
                }

                //back middle
                if(pull[3] < thresholdMap.get("Back")) {
                    Position pos = getObstaclePosition(position, posBack, pull[3]);
                    logger.debug("Ultrasound Arriere : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[3] = true;
                        logger.debug("Ultrasound Arriere : STOP");
                    } else {
                        logger.debug("Ultrasound Arriere : IGNORER");
                    }
                }
                detection = tempDetection;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void startDebug() {
        thread = new Thread(() -> {
            while(!interrupted){
                boolean[] tempDetection = new boolean[4];
                final long[] pull = detectionInterface.ultraSoundDetection();
                Position position = movementManager.getPosition();

                //First one is front left
                Position pos = getObstaclePosition(position, posFrontLeft, pull[0]);
                System.out.println("Ultrasound Avant gauche : " + pull[0] + " = " + pos.getX() + "," + pos.getY());

                //front middle
                pos = getObstaclePosition(position, posFront, pull[1]);
                System.out.println("Ultrasound Avant milieu : " + pull[1] + " = "  + pos.getX() + "," + pos.getY());

                //front right
                pos = getObstaclePosition(position, posFrontRight, pull[2]);
                System.out.println("Ultrasound Avant droit : " + pull[2] + " = "  + pos.getX() + "," + pos.getY());

                //back middle
                pos = getObstaclePosition(position, posBack, pull[3]);
                System.out.println("Ultrasound Arriere : " + pull[3] + " = "  + pos.getX() + "," + pos.getY());

                detection = tempDetection;
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        interrupted = true;
    }

    public boolean hasBeenDetected() {
        return detection[0] || detection[1] || detection[2] || detection[3];
    }

    public boolean[] getDetectionResult() {
        return this.detection;
    }
}
