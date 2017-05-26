package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.data.table.Point;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the asserv. All call to this class must be has fast as possible.
 * Created by icule on 20/05/17.
 */
public class MovementManager {

    private AsservInterface asservInterface;
    private boolean isMatchStarted = false;

    private Logger logger;

    private boolean isYPositive;

    @Inject
    public MovementManager(AsservInterface asservInterface) {
        this.asservInterface = asservInterface;
        this.logger = LoggerFactory.getLogger(MovementManager.class);
    }

    /**
     * Liste de la série de commande GoTo à enchainer
     */
    private List<Point> gotoQueue = new ArrayList<Point>();

    public void haltAsserv(boolean temporary) {
        if (!temporary) {
            gotoQueue.clear();
        } else {
            if (gotoQueue.size() > 0 && gotoQueue.size() - this.asservInterface.getQueueSize() > 0 && this.asservInterface.getQueueSize() > 0) {
                gotoQueue = gotoQueue.subList(gotoQueue.size() - this.asservInterface.getQueueSize(), gotoQueue.size());
            }
        }
        this.asservInterface.emergencyStop();
    }

    /**
     * Resume the asserv. If the asserv was halted definitely it should not be restart
     * @return true if the resume was successful, false otherwise
     */
    public boolean resumeAsserv() {
        logger.info("resumeAsserv, gotoQueue.size() = " + gotoQueue.size());
        this.asservInterface.emergencyReset();
        if (gotoQueue.size() > 0) {
            executeMovement(new ArrayList<>(gotoQueue));
            // On attends un peu pour être certains que l'asserv à reçut au moins une nouvelle commande et est à jour
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public void executeMovement(List<Point> trajectory) {
        //Call for a goto solved by astar
        logger.info("executeMovement = " + trajectory);
        logger.info("isMatchStarted = " + isMatchStarted);
        gotoQueue.clear();
        //trajectory = trajectory.subList(1, trajectory.size()); // GaG parie un café qu'un jour on cherchera pourquoi la première commande disparait
        for (Point point : trajectory) {
            gotoQueue.add(point);
            if (isMatchStarted) {
                this.asservInterface.goTo(new Position(point.x, point.y * (isYPositive ? 1 : -1)));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("executeMovement gotoQueue = " + gotoQueue);
    }

    public void executeStepDeplacement(Step step) {
        //Here we receive a GO or a FACE
        if (step.getSubType() == Step.SubType.FACE) {
            this.asservInterface.face(new Position(step.getEndPosition().getX(), step.getEndPosition().getY() * (isYPositive ? 1 : -1)));
        } else if (step.getSubType() == Step.SubType.GO) {
            this.asservInterface.go(step.getDistance());
        } else if (step.getSubType() == Step.SubType.GOTO) {
            this.asservInterface.goTo(new Position(step.getEndPosition().getX(),step.getEndPosition().getY() * (isYPositive ? 1 : -1)));
        }
    }

    public Position getPosition() {
        Position position = this.asservInterface.getPosition();
        if (!isYPositive) {
            position = new Position(position.getX(), position.getY()*-1);
        }
        return position;
    }

    public boolean isLastOrderedMovementEnded() {
        boolean isFinished = this.asservInterface.getQueueSize() == 0 && this.asservInterface.getAsservStatus() == AsservInterface.AsservStatus.STATUS_IDLE;
        if (isFinished) {
            gotoQueue.clear();
        }
        return isFinished;
    }

    public void setCap(Position position) {
        this.asservInterface.face(position);
    }

    public AsservInterface.MovementDirection getMovementDirection() {
        return this.asservInterface.getMovementDirection();
    }

    /**
     * Lance la callage du robot
     * @param isYPositive true si la couleur est pour le Y positif, false sinon
     */
    public void callage(boolean isYPositive) {
        try {
            this.asservInterface.calage(isYPositive);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setMatchStarted(boolean matchStarted) {
        isMatchStarted = matchStarted;
    }

    public boolean isYPositive() {
        return isYPositive;
    }

    public void setYPositive(boolean YPositive) {
        isYPositive = YPositive;
    }
}
