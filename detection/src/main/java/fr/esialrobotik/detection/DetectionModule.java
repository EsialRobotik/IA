package fr.esialrobotik.detection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import esialrobotik.ia.detection.DetectionAPIModule;
import esialrobotik.ia.detection.DetectionModuleConfiguration;

/**
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
