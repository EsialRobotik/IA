package fr.esialrobotik.detection;

import esialrobotik.ia.utils.rplidar.*;

import javax.inject.Inject;

/**
 * This class is the entry point of the lidar
 *
 * Created by icule on 22/05/17.
 */
public class LidarReceiver implements RpLidarListener {
    @Inject
    public LidarReceiver() {
    }


    @Override
    public void handleMeasurement(RpLidarMeasurement rpLidarMeasurement) {

    }

    @Override
    public void handleDeviceHealth(RpLidarHeath rpLidarHeath) {

    }

    @Override
    public void handleDeviceInfo(RpLidarDeviceInfo rpLidarDeviceInfo) {

    }
}
