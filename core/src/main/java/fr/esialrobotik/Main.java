package fr.esialrobotik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.configuration.ConfigurationModule;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;

/**
 * //The goal of this class if to bootstrap the code, init the robot and launch the match
 * Created by icule on 21/05/17.
 */
public class Main {

    public Main() throws FileNotFoundException {
        //Load of the configuration first
        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
        configurationManager.loadConfiguration("config.json");

        //Loading the core
        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));
        MasterLoop masterLoop = coreInjector.getInstance(MasterLoop.class);

        //Init
        masterLoop.init();

        //Launch the main loop
        boolean res = masterLoop.mainLoop();

        //End of the game.
    }


    public static void main(String[] args) throws FileNotFoundException {
        LoggerFactory.init(Level.DEBUG);
        new Main();
    }
}
