package fr.esialrobotik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.miscallenious.DomotikClient;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.configuration.ConfigurationModule;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;
import java.util.Random;

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
        if (args.length > 0) {
            if (args.length > 1) {
                System.out.println("L'IA n'attends qu'un seul argument pour démarrer");
            } else {
                switch (args[0]) {
                    default:
                    case "help":
                        System.out.println("Utilisation :");
                        System.out.println("  - help : Affiche ce menu");
                        System.out.println("  - TRACE : Active les logs en mode TRACE");
                        System.out.println("  - INFO : Active les logs en mode INFO");
                        System.out.println("  - DEBUG : Active les logs en mode DEBUG");
                        System.out.println("  - ERROR : Active les logs en mode ERROR");
                        System.out.println("Par defaut, le niveau de log affiché est TRACE");
                        return;
                    case "TRACE":
                        LoggerFactory.init(Level.TRACE);
                        break;
                    case "INFO":
                        LoggerFactory.init(Level.INFO);
                        break;
                    case "DEBUG":
                        LoggerFactory.init(Level.DEBUG);
                        break;
                    case "ERROR":
                        LoggerFactory.init(Level.ERROR);
                        break;
                }
            }
        } else {
            LoggerFactory.init(Level.TRACE);
        }
        new Main();

//        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
//        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
//        configurationManager.loadConfiguration("config.json");

        // Loading the core
//        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));

//        DetectionManager detectionManager = coreInjector.getInstance(DetectionManager.class);
//        detectionManager.initAPI();
//        detectionManager.startDetection();
//        while (true){
//            System.out.println("Yolo");
//            Thread.sleep(1000);
//        }
    }
}
