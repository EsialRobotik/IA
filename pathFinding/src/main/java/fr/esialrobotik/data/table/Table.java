package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.shape.Shape;
import fr.esialrobotik.data.table.shape.ShapeFactory;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icule on 28/03/17.
 */
@Singleton
public class Table {
    private Gson gson; // Gson object used to deserialize content of configuration
    private int length;
    private int width;
    private TableColor positiveStart;
    private TableColor negativeStart;

    private List<Shape> shapeList;

    @Inject
    public Table(Gson gson){
        this.gson = gson;
    }

    public void loadFromFile(String filePath) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(new FileReader(filePath)).getAsJsonObject());
    }

    public void loadFromString(String json){
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(json).getAsJsonObject());
    }

    private void loadConfig(JsonObject rootElement){
        shapeList = new ArrayList<Shape>();
        length = rootElement.get("longueur").getAsInt();
        width = rootElement.get("largeur").getAsInt();
        positiveStart = TableColor.getTableColorFromConfigName(rootElement.get("couleurDepartXPositif").getAsString());
        negativeStart = TableColor.getTableColorFromConfigName(rootElement.get("couleurDepartXNegatif").getAsString());

        for(JsonElement jsonElement : rootElement.getAsJsonArray("zonesInterdites")){
            shapeList.add(ShapeFactory.getShape(jsonElement.getAsJsonObject()));
        }
    }

    public int getLength(){
        return length;
    }

    public int getWidth(){
        return width;
    }

    public TableColor getPositiveStartColor(){
        return positiveStart;
    }

    public TableColor getNegativeStartColor(){
        return negativeStart;
    }

    public List<Shape> getShapeList(){
        return shapeList;
    }
}
