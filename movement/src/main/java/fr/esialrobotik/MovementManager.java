package fr.esialrobotik;

import esialrobotik.ia.asserv.AsservInterface;
import fr.esialrobotik.data.table.Point;

import javax.inject.Inject;
import javax.swing.text.Position;
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

    public void haltAsserv(boolean temporary) {

    }

    /**
     * Resume the asserv. If the asserv was halted definitely it should not be restart
     * @return true if the resume was successful, false otherwise
     */
    public boolean resumeAsserv() {
        return true;
    }

    public void executeMovement(List<Point> trajectory) {

    }

    public Position getPosition() {
        return null;
    }

    public boolean isLastOrderedMovementEnded() {
        return false;
    }

    public AsservInterface.MovementDirection getMovementDirection() {
        return null;
    }


}
