package fr.esialrobotik;

import esialrobotik.ia.actions.ActionExecutor;
import esialrobotik.ia.actions.ActionInterface;

import javax.inject.Inject;

/**
 * The purpose of this class is to handle the execution of action
 * Created by icule on 21/05/17.
 */
public class ActionSupervisor {
    private ActionInterface actionInterface;
    private ActionExecutor currentActionExecutor;

    @Inject
    public ActionSupervisor(ActionInterface actionInterface) {
        this.actionInterface = actionInterface;
    }

    public void executeCommand(int id) {
        currentActionExecutor = actionInterface.getActionExecutor(id);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                currentActionExecutor.execute();
            }
        });
    }

    public boolean isLastExecutionFinished() {
        return this.currentActionExecutor.finished();
    }
}
