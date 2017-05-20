package fr.esialrobotik.pathFinding;

import com.google.gson.JsonObject;

import javax.inject.Inject;

/**
 * Created by icule on 20/05/17.
 */
public class PathFindingConfiguration {
    private String tableConfigurationPath;

    @Inject
    public PathFindingConfiguration() {
    }

    public void loadConfig(JsonObject object) {
        this.tableConfigurationPath = object.get("tablePath").getAsString();
    }

    public String getTableConfigurationPath() {
        return this.tableConfigurationPath;
    }
}
