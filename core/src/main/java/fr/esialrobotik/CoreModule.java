package fr.esialrobotik;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import esialrobotik.ia.actions.ActionModule;
import esialrobotik.ia.asserv.AsservModule;
import esialrobotik.ia.utils.lcd.DummyLCD;
import esialrobotik.ia.utils.lcd.LCD;
import esialrobotik.ia.utils.lcd.raspberry.LCD_I2C;
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
        install(new ActionModule(configurationManager.getActionModuleConfiguration()));

        bind(ConfigurationManager.class).toInstance(configurationManager);
        bind(String.class).annotatedWith(Names.named("commandFile")).toInstance(configurationManager.getCommandFile());

        bind(MasterLoop.class).in(Singleton.class);
        bind(ColorDetector.class).in(Singleton.class);
        bind(Chrono.class).in(Singleton.class);
        bind(MovementManager.class).in(Singleton.class);
        bind(Tirette.class).in(Singleton.class);
        bind(LCD.class).to(LCD_I2C.class).in(Singleton.class);
//        bind(LCD.class).to(DummyLCD.class).in(Singleton.class);
        //bind(Tirette.class).to(DummyTirette.class).in(Singleton.class);
        bind(ActionSupervisor.class).in(Singleton.class);

    }

}
