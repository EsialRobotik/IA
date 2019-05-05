package fr.esialrobotik;

import esialrobotik.ia.utils.gpio.raspberry.GpioInput;
import fr.esialrobotik.configuration.ConfigurationManager;

import javax.inject.Inject;

/**
 * Created by icule on 20/05/17.
 */
public class Tirette {

    private GpioInput tirette;

    @Inject
    public Tirette(ConfigurationManager configurationManager) {
        this.tirette = new GpioInput(configurationManager.getTiretteGpio(), false);
    }

    /**
     * Statut de la tirette
     * @return true si la tirette est présente, false sinon
     */
    public boolean getTiretteState() {
        return this.tirette.isLow();
    }

    /**
     * Attends un état de la tirette
     * @param state true pour attendre la présence de la tirette, false pour attendre le retrait
     */
    public void waitForTirette(boolean state) {
        while(getTiretteState() != state) { //While true masterrace
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
