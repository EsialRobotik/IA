package fr.esialrobotik.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.detection.ultrasound.srf04.raspberry.SRF04;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertEquals;

/**
 * Created by icule on 20/05/17.
 */
public class ConfigurationManagerTest {

    @Test
    public void testLoading() throws FileNotFoundException {
        Injector injector = Guice.createInjector(new ConfigurationModule());
        ConfigurationManager configurationManager = injector.getInstance(ConfigurationManager.class);

        configurationManager.loadConfiguration("config.json");
        assertEquals(5, configurationManager.getColorGpio());
        assertEquals(4, configurationManager.getTiretteGpio());

        //We check one value per sub module
        assertEquals(SRF04.class, configurationManager.getDetectionConfiguration().getUltraSoundClass());
        assertEquals(230400, configurationManager.getAsservAPIConfiguration().getBaud());
    }

}