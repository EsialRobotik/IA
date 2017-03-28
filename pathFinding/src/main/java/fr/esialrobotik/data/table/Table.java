package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by icule on 28/03/17.
 */
public class Table {
    private Gson gson; // Gson object used to deserialize content of configuration

    @Inject
    public Table(Gson gson){
        this.gson = gson;
    }

    public void loadFromFile(String filePath) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(new FileReader(filePath)));
    }

    public void loadFromString(String json){
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(json));
    }

    private void loadConfig(JsonElement rootElement){

    }
}
