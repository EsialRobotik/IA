package fr.esialrobotik.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import esialrobotik.ia.asserv.AsservAPIConfiguration;
import esialrobotik.ia.detection.DetectionModuleConfiguration;
import fr.esialrobotik.data.table.TableColor;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class is responsible for the loading of the robot
 * Created by icule on 20/05/17.
 */
public class ConfigurationManager {
    private int colorGpio;
    private int tiretteGpio;

    private AsservAPIConfiguration asservAPIConfiguration;
    private DetectionModuleConfiguration detectionConfiguration;

    @Inject
    public ConfigurationManager(AsservAPIConfiguration asservAPIConfiguration, DetectionModuleConfiguration detectionConfiguration) {
        this.asservAPIConfiguration = asservAPIConfiguration;
        this.detectionConfiguration = detectionConfiguration;
    }

    public void loadConfiguration(String path) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonObject configRootNode = parser.parse(new FileReader(path)).getAsJsonObject();

        this.asservAPIConfiguration.loadConfig(configRootNode.get("asserv").getAsJsonObject());
        this.detectionConfiguration.loadConfiguration(configRootNode.get("detection").getAsJsonObject());

        this.colorGpio = configRootNode.get("gpioColorSelector").getAsInt();
        this.tiretteGpio = configRootNode.get("gpioTirette").getAsInt();

    }

    public int getColorGpio() {
        return this.colorGpio;
    }

    public int getTiretteGpio() {
        return this.tiretteGpio;
    }

    public AsservAPIConfiguration getAsservAPIConfiguration() {
        return this.asservAPIConfiguration;
    }

    public DetectionModuleConfiguration getDetectionConfiguration() {
        return detectionConfiguration;
    }
}
