package fr.esialrobotik;

import fr.esialrobotik.configuration.ConfigurationManager;

import javax.inject.Inject;

/**
 * Created by icule on 21/05/17.
 */
public class DummyTirette extends Tirette {
    @Inject
    public DummyTirette(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public void waitForTirette(boolean state) {
        //Were are good person, we are always ok
    }
}
