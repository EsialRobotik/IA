package fr.esialrobotik.configuration;

import com.google.gson.JsonParser;
import esialrobotik.ia.asserv.AsservAPIConfiguration;
import fr.esialrobotik.data.table.TableColor;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This class is responsible for the loading of the robot
 * Created by icule on 20/05/17.
 */
public class ConfigurationManager {
    private TableColor color;
    private int colorGpio;

    public ConfigurationManager() {

    }

    public void loadConfiguration(String path) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        parser.parse(new FileReader(path));


    }


    public static void main(String args[]) throws FileNotFoundException {
        ConfigurationManager config = new ConfigurationManager();
        config.loadConfiguration("core/config.json");
    }

    public TableColor getTableColor() {
        return this.color;
    }

    public int getColorGpio() {
        return this.colorGpio;
    }
}
