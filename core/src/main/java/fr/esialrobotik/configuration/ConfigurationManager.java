package fr.esialrobotik.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import esialrobotik.ia.actions.ActionModuleConfiguration;
import esialrobotik.ia.asserv.AsservAPIConfiguration;
import esialrobotik.ia.detection.DetectionModuleConfiguration;
import fr.esialrobotik.data.table.TableColor;
import fr.esialrobotik.pathFinding.PathFindingConfiguration;
import fr.esialrobotik.pathFinding.PathFindingModule;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class is responsible for the loading of the robot
 * Created by icule on 20/05/17.
 */
public class ConfigurationManager {
    private int colorGpio;
    private int tiretteGpio;
    private String tableDescriptionFilePath;
    private String commandFile;

    private AsservAPIConfiguration asservAPIConfiguration;
    private DetectionModuleConfiguration detectionConfiguration;
    private PathFindingConfiguration pathFindingConfiguration;
    private ActionModuleConfiguration actionModuleConfiguration;

    @Inject
    public ConfigurationManager(AsservAPIConfiguration asservAPIConfiguration,
                                DetectionModuleConfiguration detectionConfiguration,
                                PathFindingConfiguration pathFindingConfiguration,
                                ActionModuleConfiguration actionModuleConfiguration) {
        this.asservAPIConfiguration = asservAPIConfiguration;
        this.detectionConfiguration = detectionConfiguration;
        this.pathFindingConfiguration = pathFindingConfiguration;
        this.actionModuleConfiguration = actionModuleConfiguration;
    }

    public void loadConfiguration(String path) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonObject configRootNode = parser.parse(new FileReader(path)).getAsJsonObject();

        this.asservAPIConfiguration.loadConfig(configRootNode.get("asserv").getAsJsonObject());
        this.detectionConfiguration.loadConfiguration(configRootNode.get("detection").getAsJsonObject());
        this.actionModuleConfiguration.loadConfig(configRootNode.get("action").getAsJsonObject());
        this.pathFindingConfiguration.loadConfig(configRootNode);

        this.colorGpio = configRootNode.get("gpioColorSelector").getAsInt();
        this.tiretteGpio = configRootNode.get("gpioTirette").getAsInt();
        this.commandFile = configRootNode.get("commandFile").getAsString();
    }

    public int getColorGpio() {
        return this.colorGpio;
    }

    public int getTiretteGpio() {
        return this.tiretteGpio;
    }

    public String getTableDescriptionFilePath() {
        return this.tableDescriptionFilePath;
    }

    public AsservAPIConfiguration getAsservAPIConfiguration() {
        return this.asservAPIConfiguration;
    }

    public DetectionModuleConfiguration getDetectionConfiguration() {
        return detectionConfiguration;
    }

    public ActionModuleConfiguration getActionModuleConfiguration() {
        return actionModuleConfiguration;
    }

    public String getCommandFile() {
        return commandFile;
    }

    public PathFindingConfiguration getPathFindingConfiguration() {
        return this.pathFindingConfiguration;
    }
}
