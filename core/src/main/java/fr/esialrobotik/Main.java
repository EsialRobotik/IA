package fr.esialrobotik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.Divers.DomotikClient;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.configuration.ConfigurationModule;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;

/**
 * The goal of this class if to bootstrap the code, init the robot and launch the match
 * Created by icule on 21/05/17.
 */
public class Main {

    public Main() throws FileNotFoundException, InterruptedException {
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
        if(!res) { //We run out of action, and not of time, let's wait for the end of the match
            while(true) {
                Thread.sleep(1000);
                if(masterLoop.isMatchFinished()) {
                    break;
                }
            }
        }

        //End of the game. Let's wait a few more seconds (for funny action and to be sure) and let's return
        Thread.sleep(9000);
    }


    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        LoggerFactory.init(Level.INFO);
        new Main();

//        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
//        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
//        configurationManager.loadConfiguration("config.json");
//
//        //Loading the core
//        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));
//
//        DetectionManager detectionManager = coreInjector.getInstance(DetectionManager.class);
//        detectionManager.initAPI();
//        detectionManager.startDetection();
//        while (true){
//            System.out.println("Yolo");
//            Thread.sleep(1000);
//        }
    }
}
