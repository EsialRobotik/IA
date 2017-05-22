package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import esialrobotik.ia.asserv.Position;
import fr.esialrobotik.data.table.Point;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the asserv. All call to this class must be has fast as possible.
 * Created by icule on 20/05/17.
 */
public class MovementManager {
    private AsservInterface asservInterface;

    @Inject
    public MovementManager(AsservInterface asservInterface) {
        this.asservInterface = asservInterface;
    }

    /**
     * Liste de la série de commande GoTo à enchainer
     */
    private List<Point> gotoQueue = new ArrayList<Point>();

    public void haltAsserv(boolean temporary) {
        if (!temporary) {
            gotoQueue.clear();
        }
        if (gotoQueue.size() > 0 && gotoQueue.size() - this.asservInterface.getQueueSize() > 0 && this.asservInterface.getQueueSize() > 0) {
            gotoQueue = gotoQueue.subList(gotoQueue.size() - this.asservInterface.getQueueSize(), gotoQueue.size() - 1);
        }
        this.asservInterface.emergencyStop();
    }

    /**
     * Resume the asserv. If the asserv was halted definitely it should not be restart
     * @return true if the resume was successful, false otherwise
     */
    public boolean resumeAsserv() {
        this.asservInterface.emergencyReset();
        if (gotoQueue.size() > 0) {
            executeMovement(gotoQueue);
            return true;
        } else {
            return false;
        }
    }

    public void executeMovement(List<Point> trajectory) {
        //Call for a goto solved by astar
        gotoQueue.clear();
        for (Point point : trajectory) {
            gotoQueue.add(point);
            this.asservInterface.goTo(new Position(point.x, point.y));
        }
    }

    public void executeStepDeplacement(Step step) {
        //Here we receive a GO or a FACE
        if (step.getSubType() == Step.SubType.FACE) {
            this.asservInterface.face(step.getEndPosition());
        } else if (step.getSubType() == Step.SubType.GO) {
            this.asservInterface.go(step.getDistance());
        }
    }

    public Position getPosition() {
        return this.asservInterface.getPosition();
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

}
