package fr.esialrobotik.detection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by icule on 12/05/17.
 */
public class DetectionManagerTest {

    @Test
    public void testInstantiation() {
        Injector injector = Guice.createInjector(new DetectionModule());
        DetectionManager detectionManager = injector.getInstance(DetectionManager.class);
    }

}