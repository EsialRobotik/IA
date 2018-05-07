package fr.esialrobotik.detection;


import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.detection.DetectionInterface;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.MovementManager;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.Table;
import javafx.geometry.Pos;
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

    private volatile boolean interrupted = false;

    private Position posFrontLeft;
    private Position posFront;
    private Position posFrontRight;
    private Position posBack;

    @Inject
    public UltraSoundManager(DetectionInterface detectionInterface, Table table, MovementManager movementManager) {
        detection = new boolean[4];
        LoggerFactory.init(Level.TRACE);
        logger = LoggerFactory.getLogger(UltraSoundManager.class);

        this.movementManager = movementManager;
        this.detectionInterface = detectionInterface;
        this.table = table;

        this.posFrontLeft = new Position(50, 130, Math.PI/6);
        this.posFront = new Position(100, -60, Math.PI/12);
        this.posFrontRight = new Position(100, -130, -Math.PI/6);
        this.posBack = new Position(-100, 0, Math.PI);
    }

    private static Position getObstaclePosition(Position posRobot, Position posDetector, long distance) {
        double xObstacleRelativeToRobot, yObstacleRelativeToRobot;
        int xObstacleRelativeToTable, yObstacleRelativeToTable;

        xObstacleRelativeToRobot = posDetector.getX() + distance * Math.cos(posDetector.getTheta());
        yObstacleRelativeToRobot = posDetector.getY() + distance * Math.sin(posDetector.getTheta());

        xObstacleRelativeToTable = (int) (posRobot.getX()
                + xObstacleRelativeToRobot * Math.cos(posRobot.getTheta())
                + yObstacleRelativeToRobot * Math.sin(posRobot.getTheta()) );

        yObstacleRelativeToTable = (int) (posRobot.getY()
                - xObstacleRelativeToRobot * Math.sin(posRobot.getTheta())
                + yObstacleRelativeToRobot * Math.cos(posRobot.getTheta()) );

        return new Position(xObstacleRelativeToTable, yObstacleRelativeToTable);
    }

    public void start() {
        thread = new Thread(() -> {
            while(!interrupted){
                boolean[] tempDetection = new boolean[4];
                final long[] pull = detectionInterface.ultraSoundDetection();
                Position position = movementManager.getPosition();
//                int x, y;

                //First one is front left
                if(pull[0] < threshold) {
                    Position pos = getObstaclePosition(position, posFrontLeft, pull[0]);
//                        x = (int) (position.getX() + 50 + Math.cos(position.getTheta() + Math.PI/6) * pull[0]);
//                        y = (int) (position.getY() + 130 + Math.sin(position.getTheta() + Math.PI/6) * pull[0]);
                    logger.debug("Avant gauche : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                       tempDetection[0] = true;
                    }
                }

                //frnt middle
                logger.debug("Avant milieu : " + pull[1]);
                if(pull[1] < threshold) {
                    Position pos = getObstaclePosition(position, posFront, pull[1]);
//                        x = (int) (position.getX() + 100 + Math.cos(position.getTheta() + Math.PI/12) * pull[1]);
//                        y = (int) (position.getY() - 60 + Math.sin(position.getTheta() + Math.PI/12) * pull[1]);
                    logger.debug("Avant milieu : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[1] = true;
                    }
                }

                //frnt right
                logger.debug("Avant droit : " + pull[2]);
                if(pull[2] < threshold) {
                    Position pos = getObstaclePosition(position, posFrontRight, pull[2]);
//                        x = (int) (position.getX() + 100 + Math.cos(position.getTheta() - Math.PI/6) * pull[2]);
//                        y = (int) (position.getY() - 130 + Math.sin(position.getTheta() - Math.PI/6) * pull[2]);
                    logger.debug("Avant droit : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[2] = true;
                    }
                }

                //back middle
                logger.debug("Arriere : " + pull[3]);
                if(pull[3] < threshold) {
                    Position pos = getObstaclePosition(position, posBack, pull[3]);
//                        x = (int) (position.getX() - 100 - Math.cos(position.getTheta()) * pull[1]);
//                        y = (int) (position.getY() - Math.sin(position.getTheta()) * pull[1]);
                    logger.debug("Arriere : " + pos.getX() + "," + pos.getY());
                    if(!table.isAreaForbiddenSafe(pos.getX() / 10, pos.getY() / 10)) {
                        tempDetection[3] = true;
                    }
                }
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
        return detection[0] || detection[1] || detection[3] || detection[4];
    }

    public boolean[] getDetectionResult() {
        return this.detection;
    }
}
