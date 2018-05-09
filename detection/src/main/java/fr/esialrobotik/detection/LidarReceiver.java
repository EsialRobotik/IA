package fr.esialrobotik.detection;

import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.utils.log.LoggerFactory;
import esialrobotik.ia.utils.rplidar.RpLidarDeviceInfo;
import esialrobotik.ia.utils.rplidar.RpLidarHeath;
import esialrobotik.ia.utils.rplidar.RpLidarListener;
import esialrobotik.ia.utils.rplidar.RpLidarMeasurement;
import fr.esialrobotik.MovementManager;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.Table;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is the entry point of the lidar
 *
 * Created by icule on 22/05/17.
 */
public class LidarReceiver implements RpLidarListener {
    private Logger logger;
    private DetectionTrame lastCompletedTrame;
    private DetectionTrame currentTrame;
    private MovementManager movementManager;
    private Table table;

    public static class DetectionTrame {
        public List<Point> detectedPoint;
        public int trameId;

        public DetectionTrame(int trameId) {
            this.trameId = trameId;
            this.detectedPoint = new ArrayList<>();
        }
    }

    @Inject
    public LidarReceiver(Table table, MovementManager movementManager) {
        this.logger = LoggerFactory.getLogger(LidarReceiver.class);
        //In case we create the trame -1
        currentTrame = new DetectionTrame(-1);
        lastCompletedTrame = currentTrame;
        this.movementManager = movementManager;
        this.table = table;
    }

    public DetectionTrame getLastCompletedTrame() {
        return this.lastCompletedTrame;
    }


    @Override
    public void handleMeasurement(RpLidarMeasurement rpLidarMeasurement) {
        double deg = (rpLidarMeasurement.angle / 64.0) * Math.PI / 160;
        double r = rpLidarMeasurement.distance / 4.0;
        if(rpLidarMeasurement.start) {
            lastCompletedTrame = currentTrame;
            currentTrame = new DetectionTrame(lastCompletedTrame.trameId + 1);
        }
        if( r < 1000 && r > 1) {
            Position position = movementManager.getPosition();
            //Ok this is a point of interest, so let's check if it's on a forbidden area
            double xFloat = position.getX() + (r * Math.cos(deg + position.getTheta()));
            double yFloat = position.getY() + (r * Math.sin(deg + position.getTheta()));
            if(this.table.isAreaForbiddenSafe((int)xFloat, (int)yFloat)) {
                //Ok that seams interesting so let's store it
                currentTrame.detectedPoint.add(new Point((int)xFloat, (int)yFloat));
            }
        }
    }

    @Override
    public void handleDeviceHealth(RpLidarHeath rpLidarHeath) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo rpLidarDeviceInfo) {

    }
}
