package fr.esialrobotik.detection;

import esialrobotik.ia.utils.log.LoggerFactory;
import esialrobotik.ia.utils.rplidar.*;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * This class is the entry point of the lidar
 *
 * Created by icule on 22/05/17.
 */
public class LidarReceiver implements RpLidarListener {
    private Logger logger;
    @Inject
    public LidarReceiver() {
        this.logger = LoggerFactory.getLogger(LidarReceiver.class);
    }


    @Override
    public void handleMeasurement(RpLidarMeasurement rpLidarMeasurement) {
        double deg = rpLidarMeasurement.angle / 64.0;
        double r = rpLidarMeasurement.distance / 4.0;
        if (rpLidarMeasurement.start)
            System.out.println();
        if( r < 1000 && r > 1)
            logger.debug(rpLidarMeasurement.start + " %3d   theta = %6.2f r = %10.2f\n", rpLidarMeasurement.quality, deg, r);
    }

    @Override
    public void handleDeviceHealth(RpLidarHeath rpLidarHeath) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo rpLidarDeviceInfo) {

    }
}
