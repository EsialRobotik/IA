package fr.esialrobotik.detection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import esialrobotik.ia.detection.DetectionAPIModule;
import esialrobotik.ia.detection.DetectionModuleConfiguration;
import esialrobotik.ia.detection.lidar.rplidar.raspberry.RpLidar;

/**
 * This module required the installation of the asserv module, and the pathFinding
 * Created by icule on 12/05/17.
 */
public class DetectionModule extends AbstractModule {
    private DetectionModuleConfiguration detectionModuleConfiguration;

    public DetectionModule(DetectionModuleConfiguration detectionModuleConfiguration) {
        this.detectionModuleConfiguration = detectionModuleConfiguration;
    }

    protected void configure() {
        install(new DetectionAPIModule(detectionModuleConfiguration));
        bind(LidarManager.class).in(Singleton.class);
        bind(UltraSoundManager.class).in(Singleton.class);
    }
}
