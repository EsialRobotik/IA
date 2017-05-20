package fr.esialrobotik.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import esialrobotik.ia.asserv.AsservAPIConfiguration;
import esialrobotik.ia.detection.DetectionModuleConfiguration;

/**
 * Created by icule on 20/05/17.
 */
public class ConfigurationModule extends AbstractModule{
    protected void configure() {
        bind(ConfigurationManager.class).in(Singleton.class);
        bind(AsservAPIConfiguration.class).in(Singleton.class);
        bind(DetectionModuleConfiguration.class).in(Singleton.class);
    }
}
