package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.shape.Shape;
import fr.esialrobotik.data.table.shape.ShapeFactory;
import fr.esialrobotik.data.table.shape.ShapeFiller;

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

    private boolean[][] forbiddenArea;

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

    public void drawTable() {
        int rectifiedLength = length / 10;
        int rectifiedWidth = width / 10;

        this.forbiddenArea = new boolean[rectifiedLength][rectifiedWidth];
        for(int i = 0; i < forbiddenArea.length; ++i) {
            for(int j = 0; j < forbiddenArea[0].length; ++j) {
                this.forbiddenArea[i][j] = false;
            }
        }
        for(Shape shape : shapeList) {
            boolean[][] temp = shape.drawShapeEdges(rectifiedLength, rectifiedWidth);

            for(int i = 0; i < rectifiedLength; ++i) {
                for(int j = 0; j < rectifiedWidth; ++j) {
                    if(temp[i][j]){
                        this.forbiddenArea[i][j] = true;
                    }
                }
            }
        }
    }

    public void printTable() {
        for(int i = 0; i < forbiddenArea.length; ++i) {
            for(int j = 0; j < forbiddenArea[0].length; ++j){
                System.out.print(forbiddenArea[i][j]?"x":"o");
            }
            System.out.print("\n");
        }
    }
}
