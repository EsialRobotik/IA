package fr.esialrobotik.detection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Guice;
import com.google.inject.Injector;
import esialrobotik.ia.detection.DetectionModuleConfiguration;

/**
 * Created by icule on 12/05/17.
 */
public class DetectionManagerTest {
    String configure = "{" +
            "  \"type\":\"test\", \n" +
            "  \"gpioList\":[ \n " +
            "    {\n" +
            "       \"in\":1, \n" +
            "       \"out\":2 \n" +
            "    }\n" +
            "  ]\n" +
            "}";

    //@Test
    public void testInstantiation() {
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(configure).getAsJsonObject();

        DetectionModuleConfiguration config = new DetectionModuleConfiguration();
        config.loadConfiguration(object);

        Injector injector = Guice.createInjector(new DetectionModule(config));
        DetectionManager detectionManager = injector.getInstance(DetectionManager.class);
    }

}