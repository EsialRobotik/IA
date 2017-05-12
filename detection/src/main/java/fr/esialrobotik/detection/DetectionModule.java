package fr.esialrobotik.detection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import esialrobotik.ia.detection.DetectionAPIModule;

/**
 * Created by icule on 12/05/17.
 */
public class DetectionModule extends AbstractModule {
    protected void configure() {
        install(new DetectionAPIModule());
        bind(LidarManager.class).in(Singleton.class);
        bind(UltraSoundManager.class).in(Singleton.class);
    }
}
