package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.utils.lcd.LCD;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.TableColor;
import fr.esialrobotik.detection.DetectionManager;
import fr.esialrobotik.pathFinding.PathFinding;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Created by Guillaume on 17/05/2017.
 */
public class MasterLoop {
    private MovementManager movementManager;
    private DetectionManager detectionManager;
    private ActionCollection actionCollection;
    private PathFinding pathFinding;
    private ColorDetector colorDetector;
    private Chrono chrono;
    private Tirette tirette;
    private LCD lcdDisplay;

    private volatile boolean interrupted;

    private ActionSupervisor actionSupervisor;
    private ActionDescriptor currentAction;
    private Step currentStep;

    private Logger logger;


    @Inject
    public MasterLoop(MovementManager movementManager,
                      DetectionManager detectionManager,
                      ActionCollection actionCollection,
                      ActionSupervisor actionSupervisor,
                      PathFinding pathFinding,
                      ColorDetector colorDetector,
                      Chrono chrono,
                      Tirette tirette,
                      LCD lcdDisplay) {
        this.movementManager = movementManager;
        this.detectionManager = detectionManager;
        this.actionCollection = actionCollection;
        this.pathFinding = pathFinding;
        this.colorDetector = colorDetector;
        this.chrono = chrono;
        this.tirette = tirette;
        this.lcdDisplay = lcdDisplay;
        this.actionSupervisor = actionSupervisor;

        this.interrupted = false; // Chiotte de bordel de saloperie d'enflure de connerie !
        this.logger = LoggerFactory.getLogger(MasterLoop.class);
    }


    //NOTE robot is at starting point before reaching this
    public boolean mainLoop() {
        logger.info("Begin of main loop");
        //When we arrived here everything is set up so we just need to launch the first path finding,
        // and wait for the beginning of the match
        boolean everythingDone = false;
        boolean astarLaunch = false;
        boolean somethingDetected = false;
        boolean movingForward = false;

        movementManager.setYPositive(colorDetector.isYPositive());

        // FIRST COMPUTATION HERE
        // 1/ We pull the first action to do
        currentAction = actionCollection.getNextActionToPerform();
        currentStep = currentAction.getNextStep(); //Should not be null

        logger.info("Fetch of first acction");
        //The first action is always a move straight or a path finding issue
        // 2/ We launch the Astar (to spare time) if we have a non always.
        if (currentStep.getActionType() == Step.Type.DEPLACEMENT && currentStep.getSubType() == Step.SubType.GOTO) {
            logger.info("First action is a deplacement with astar, let's start computation");
            launchAstar(positionToPoint(currentStep.getEndPosition()));
            while (!pathFinding.isComputationEnded()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //We got interrupted, things are bad
                }
            }
            // 3/ We send command to the asserv manager
            movementManager.executeMovement(pathFinding.getLastComputedPath());
        } else if (currentStep.getSubType() == Step.SubType.GO) {//Straight line
            logger.info("First deplacement is a straight line");
            movementManager.executeStepDeplacement(currentStep);
        }

        logger.info("Trajectory load, let's wait for tirette");
        // 4/ We wait for the beginning of the match
        tirette.waitForTirette(false);

        // 5/ start of the timer start the main loop
        logger.info("Tirette pull, begin of the match");
        chrono.startMatch(this);
        movementManager.setMatchStarted(true);
        movementManager.resumeAsserv();

        logger.debug("while " + !interrupted);
        while (!interrupted) {
            if (!somethingDetected) {
                // 1/ we check if we detect something
                boolean[] detected = this.detectionManager.getEmergencyDetectionMap();
                if (detected[0] || detected[1] || detected[2] || detected[3]) {
                    //We detect something, we get the movement direction and we check if we detect it in the right side
                    AsservInterface.MovementDirection direction = this.movementManager.getMovementDirection();

                    if (direction.equals(AsservInterface.MovementDirection.FORWARD)
                            && (detected[0] || detected[1] || detected[2])) {
                        logger.debug("C'est devant, faut s'arrêter");
                        //We detect something. That's horrible
                        movementManager.haltAsserv(true);
                        movingForward = true;
                        somethingDetected = true;

                    } else if (direction.equals(AsservInterface.MovementDirection.BACKWARD)
                            && detected[3]) {
                        logger.debug("C'est derrière, faut s'arrêter");
                        // something is sneaking on us, grab the rocket launcher
                        movementManager.haltAsserv(true);
                        movingForward = false;
                        somethingDetected = true;
                    }
                }

                // 2/ Check if the current step Status
                if (astarLaunch) { //We are computing a path let's check if it's ok now
                    logger.debug("astarLaunch");
                    if (pathFinding.isComputationEnded()) {
                        movementManager.executeMovement(pathFinding.getLastComputedPath());
                        astarLaunch = false;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (currentStepEnded()) { //There is few chance we end the deplacement that soon so don't check
                    logger.debug("currentStepEnded");
                    currentStep = null;
                    //Time to fetch the next one
                    if (currentAction.hasNextStep()) {
                        currentStep = currentAction.getNextStep();
                        if ((currentStep != null && currentStep.isyPositiveExclusive() && !movementManager.isYPositive())
                                || (currentStep != null && currentStep.isyNegativeExclusive() && movementManager.isYPositive())) {
                            currentStep = currentAction.getNextStep(); // TODO FIXME, ne pas mettre en prod
                        }
                        logger.debug("Suite de l'action, step = " + currentStep.getDesc());
                    } else { //Previous action has ended, time to fetch a new one
                        currentAction = actionCollection.getNextActionToPerform();
                        if (currentAction == null) {//Nothing more to do. #sadness
                            logger.debug("Plus rien à faire :'(");
                            break;
                        } else {
                            currentStep = currentAction.getNextStep();
                            if ((currentStep != null && currentStep.isyPositiveExclusive() && !movementManager.isYPositive())
                                    || (currentStep != null && currentStep.isyNegativeExclusive() && movementManager.isYPositive())) {
                                currentStep = currentAction.getNextStep(); // TODO FIXME, ne pas mettre en prod
                            }
                            logger.debug("Nouvelle action = " + currentAction.getDesc());
                            logger.debug("Nouvelle step = " + currentStep.getDesc());
                        }
                    }
                    //Switch... switch... switch, yeah I heard about htem once, but never met :P
                    if (currentStep.getActionType() == Step.Type.MANIPULATION) {
                        logger.debug("Manip");
                        actionSupervisor.executeCommand(currentStep.getActionId());
                    } else if (currentStep.getActionType() == Step.Type.DEPLACEMENT) {
                        logger.debug("Déplacement");
                        if (currentStep.getSubType() == Step.SubType.GOTO) {
                            // We need to launch the astar
                            launchAstar(positionToPoint(currentStep.getEndPosition()));
                            astarLaunch = true;
                        } else {
                            movementManager.executeStepDeplacement(currentStep);
                        }

                    }
                }
            } else { //We detect something last loop. let's check if we still see it, either let's resume the move
                //If we want to put smart code, it's here
                boolean[] detected = this.detectionManager.getEmergencyDetectionMap();
                if (movingForward && !detected[0] && !detected[1] && !detected[2]) {
                    movementManager.resumeAsserv();
                    somethingDetected = false;
                } else if (!movingForward && !detected[3]) {
                    movementManager.resumeAsserv();
                    somethingDetected = false;
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        return !interrupted;
    }

    //This function could be simplified but at least it keeps things readeable
    private boolean currentStepEnded() {
        Step.Type type = currentStep.getActionType();
        if ((type == Step.Type.DEPLACEMENT)
                && movementManager.isLastOrderedMovementEnded()) {
            return true;
        } else if (type == Step.Type.MANIPULATION && actionSupervisor.isLastExecutionFinished()) {
            return true;
        }
        return false;
    }

    //Function to be call to set up the robot and lead him to starting point
    public void init() {
        logger.info("Init mainLoop");

        // Calage bordure
        lcdDisplay.println(colorDetector.getSelectedColor() == TableColor.BLUE ? "Bleu" : "Jaune");
        logger.info("Attente mise en place tirette pour init callage");
        lcdDisplay.println("tirette callage");
        tirette.waitForTirette(true);
        logger.info("Attente retrait tirette pour init callage");
        lcdDisplay.println(colorDetector.getSelectedColor() == TableColor.BLUE ? "Bleu" : "Jaune");
        lcdDisplay.println("tirette callage");
        tirette.waitForTirette(false);
        logger.info("Start calage bordure");
        lcdDisplay.println("Lancement callage bordure");
        movementManager.callage(colorDetector.getSelectedColor() == TableColor.BLUE); // TODO l'ia ne devrait pas connaitre les couleurs mais seulement le sens du Y

        // Wait tirette remise
        lcdDisplay.println("Attente remise tirette");
        logger.info("Init ended, wait for tirette");
        tirette.waitForTirette(true);
        logger.info("Tirette inserted. End of initialization.");
        detectionManager.initAPI();
        detectionManager.startDetection();
        lcdDisplay.println("LET'S ROCK !");
    }

    public void matchEnd() {
        logger.info("End of the match");
        lcdDisplay.println("End of match");
        //Stop the asserv here
        logger.info("Shutting done asserv");
        movementManager.haltAsserv(false);

        //Don't forget action
        logger.info("Shutting done detection");
        detectionManager.stopDetection();

        interrupted = true;
        //Launch the funny action if needed
    }

    //Start the computation of the path.
    private void launchAstar(Point destination) {
        pathFinding.computePath(positionToPoint(movementManager.getPosition()), destination);
    }

    public boolean isMatchFinished() {
        return this.interrupted;
    }

    private Point positionToPoint(Position p) {
        return new Point(p.getX(), p.getY());
    }
}
