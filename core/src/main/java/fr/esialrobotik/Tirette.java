package fr.esialrobotik;

import fr.esialrobotik.configuration.ConfigurationManager;

import javax.inject.Inject;

/**
 * Created by icule on 20/05/17.
 */
public class Tirette {
    @Inject
    public Tirette(ConfigurationManager configurationManager) {

    }

    public boolean getTiretteState() {
        return false;
    }

    public void waitForTirette(boolean state) {
        while(true) { //While true masterrace
            //Test value of tirette
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Ohoh wasn't plan
            }
        }
    }
}
