package fr.esialrobotik;

import fr.esialrobotik.configuration.ConfigurationManager;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icule on 20/05/17.
 */
public class Chrono {
    private Timer timer;
    private int matchDuration;

    @Inject
    public Chrono(ConfigurationManager configurationManager) {
        timer = new Timer();
    }

    //We should find a way to do other wise, but, well
    public void startMatch(final MasterLoop masterLoop) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                masterLoop.matchEnd();
            }
        }, matchDuration * 1000); //Delay in milliseconds
    }
}
