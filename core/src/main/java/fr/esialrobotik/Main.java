package fr.esialrobotik;

import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.actions.a2018.Actions;
import esialrobotik.ia.actions.a2019.ActionAX12Json;
import esialrobotik.ia.actions.a2019.ActionFileBinder;
import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.asserv.raspberry.Asserv;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.miscallenious.DomotikClient;
import fr.esialrobotik.configuration.ConfigurationManager;
import fr.esialrobotik.configuration.ConfigurationModule;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
        LoggerFactory.shutdown();
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

        // Exécution normal de l'IA
        new Main();

        // Test de la détection
//        Main.testDetection();

        // Test interrupteurs
//        Main.testInterrupteurs();

        // Danse de la coupe off
//        Main.coupeOffDance();
    }

    private static void testDetection() throws FileNotFoundException, InterruptedException {
        // On charge la config
        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
        configurationManager.loadConfiguration("config.json");

        // Loading the core
        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));

        DetectionManager detectionManager = coreInjector.getInstance(DetectionManager.class);
        detectionManager.initAPI();
        detectionManager.startDetectionDebug();
        while (true){
            Thread.sleep(1000);
        }
    }

    private static void testInterrupteurs() throws FileNotFoundException, InterruptedException {
        // On charge la config
        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
        configurationManager.loadConfiguration("config.json");

        // Loading the core
        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));

        ColorDetector colorDetector = coreInjector.getInstance(ColorDetector.class);
        Tirette tirette = coreInjector.getInstance(Tirette.class);
        while (true){
            Thread.sleep(500);
            System.out.println("Couleur0 ? " + colorDetector.isColor0());
            System.out.println("Tirette présente ? " + tirette.getTiretteState());
        }
    }

    private static void coupeOffDance() throws FileNotFoundException {
        Injector configurationInjector = Guice.createInjector(new ConfigurationModule());
        ConfigurationManager configurationManager = configurationInjector.getInstance(ConfigurationManager.class);
        configurationManager.loadConfiguration("config.json");

        // Loading the core
        Injector coreInjector = Guice.createInjector(new CoreModule(configurationManager));
        Asserv asserv = coreInjector.getInstance(Asserv.class);
        ActionFileBinder actions = coreInjector.getInstance(ActionFileBinder.class);
        Tirette tirette = configurationInjector.getInstance(Tirette.class);
        Logger logger = LoggerFactory.getLogger(Main.class);

        tirette.waitForTirette(true);
        tirette.waitForTirette(false);
        actions.getActionExecutor(12).execute();

        //On prépare du random
        Random random = new Random();
        ArrayList<String> randomDance = new ArrayList<>();
        randomDance.add("goto");
        randomDance.add("goto");
        randomDance.add("goto");
        randomDance.add("turn");
        randomDance.add("turn");
        randomDance.add("turn");
        randomDance.add("brasDroit");
        randomDance.add("brasGauche");
        randomDance.add("largageDroit");
        randomDance.add("largageGauche");
        randomDance.add("vol");

        for (int i = 0; i < 100; i++){
            String action = randomDance.get(random.nextInt(randomDance.size()));

            switch (action) {
                case "goto":
                    int x = random.nextInt(5)*100;
                    int y = random.nextInt(5)*100;
                    asserv.goTo(new Position(x,y));
                    Main.waitForAsserv(asserv);
                    break;
                case "turn":
                    int angle = random.nextInt(360);
                    if (random.nextInt(2) == 1) {
                        angle *= -1;
                    }
                    asserv.turn(angle);
                    Main.waitForAsserv(asserv);
                    break;
                case "goAndBack":
                    int go = random.nextInt(10)*10;
                    asserv.go(go);
                    Main.waitForAsserv(asserv);
                    asserv.go(-go);
                    Main.waitForAsserv(asserv);
                    break;
                case "interrupteur":
                    actions.getActionExecutor(13).execute();
                    break;
                case "brasDroit":
                    actions.getActionExecutor(1).execute();
                    actions.getActionExecutor(0).execute();
                    break;
                case "brasGauche":
                    actions.getActionExecutor(3).execute();
                    actions.getActionExecutor(2).execute();
                    break;
                case "largageDroit":
                    actions.getActionExecutor(5).execute();
                    actions.getActionExecutor(7).execute();
                    break;
                case "largageGauche":
                    actions.getActionExecutor(6).execute();
                    actions.getActionExecutor(7).execute();
                    break;
                case "vol":
                    actions.getActionExecutor(1).execute();
                    actions.getActionExecutor(3).execute();
                    actions.getActionExecutor(0).execute();
                    actions.getActionExecutor(2).execute();
                    break;
            }
        }
    }

    private static void waitForAsserv(AsservInterface asservInterface) {
        while (asservInterface.getQueueSize() == 0 && asservInterface.getAsservStatus() == AsservInterface.AsservStatus.STATUS_IDLE) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Asserv OK");
    }
}
