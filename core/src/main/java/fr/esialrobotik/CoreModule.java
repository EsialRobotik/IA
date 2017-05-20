package fr.esialrobotik;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import esialrobotik.ia.asserv.AsservModule;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.detection.DetectionModule;
import fr.esialrobotik.pathFinding.PathFindingModule;

/**
 * Created by icule on 20/05/17.
 */
public class CoreModule extends AbstractModule {
    public ConfigurationManager configurationManager;

    public CoreModule(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }


    protected void configure() {
        install(new DetectionModule(configurationManager.getDetectionConfiguration()));
        install(new AsservModule(configurationManager.getAsservAPIConfiguration()));
        install(new PathFindingModule(configurationManager.getPathFindingConfiguration()));

        bind(ConfigurationManager.class).toInstance(configurationManager);

        bind(MasterLoop.class).in(Singleton.class);
        bind(ColorDetector.class).in(Singleton.class);
        bind(Chrono.class).in(Singleton.class);
        bind(MovementManager.class).in(Singleton.class);
    }
}
