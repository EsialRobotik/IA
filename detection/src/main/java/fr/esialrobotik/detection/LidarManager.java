package fr.esialrobotik.detection;

import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.detection.lidar.LidarInterface;
import esialrobotik.ia.detection.lidar.rplidar.raspberry.LidarPoint;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.MovementManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by icule on 12/05/17.
 */
public class LidarManager {

    private Thread thread;

    private MovementManager movementManager;

    private LidarInterface lidar;

    private Logger logger;

    @Inject
    public LidarManager(LidarInterface lidar, MovementManager movementManager) {
        this.lidar = lidar;
        this.movementManager = movementManager;

        LoggerFactory.init(Level.TRACE);
        logger = LoggerFactory.getLogger(LidarManager.class);
    }

    public void start() {
        thread = new Thread(() -> {
            int fail = 0;
            while (true) {
                Position position = movementManager.getPosition();
                List<LidarPoint> points = lidar.getMeasures();
                for (LidarPoint point : points) {
                    Position pos = getObstaclePosition(position, point);
                    logger.debug(pos.toString());
                }
                if (points.size() == 0) {
                    fail++; // Quand ça commence à merder, on reset le lidar
                    if (fail == 5) {
                        lidar.stop();
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lidar.init();
                        fail = 0;
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        this.lidar.stop();
    }

    private static Position getObstaclePosition(Position posRobot, LidarPoint lidarPoint) {
        double xObstacleRelativeToRobot, yObstacleRelativeToRobot;
        int xObstacleRelativeToTable, yObstacleRelativeToTable;

        xObstacleRelativeToRobot = lidarPoint.distance * Math.cos(lidarPoint.angle);
        yObstacleRelativeToRobot = lidarPoint.distance * Math.sin(lidarPoint.angle);

        xObstacleRelativeToTable = (int) (posRobot.getX()
                + xObstacleRelativeToRobot * Math.cos(posRobot.getTheta())
                - yObstacleRelativeToRobot * Math.sin(posRobot.getTheta()) );

        yObstacleRelativeToTable = (int) (posRobot.getY()
                + xObstacleRelativeToRobot * Math.sin(posRobot.getTheta())
                + yObstacleRelativeToRobot * Math.cos(posRobot.getTheta()) );

        return new Position(xObstacleRelativeToTable, yObstacleRelativeToTable);
    }
}
