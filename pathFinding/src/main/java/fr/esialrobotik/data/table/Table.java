package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.shape.Circle;
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

    public void computeForbiddenArea(int margin) {
        int rectifiedMargin = (int)Math.ceil(margin / 10.);

        int boardLength = forbiddenArea.length;
        int boardWidth = forbiddenArea[0].length;


        boolean[][] buffer = new boolean[boardLength + 2 * (rectifiedMargin + 1)][boardWidth + 2 * (rectifiedMargin + 1)];
        for(int i = 0; i < buffer.length; ++i) {
            for(int j = 0; j < buffer[0].length; ++j) {
                buffer[i][j] = false;
            }
        }

        Circle circle = new Circle(rectifiedMargin * 10, rectifiedMargin * 10, rectifiedMargin * 10);
        boolean[][] shapeBuffer = circle.drawShapeEdges((rectifiedMargin + 1) * 2, (rectifiedMargin + 1) * 2);

        for(int i = 0; i < boardLength; ++i) {
            for(int j = 0; j < boardWidth; ++j) {
                if(forbiddenArea[i][j] || i == 0 || j == 0 || i == boardLength -1 || j == boardWidth - 1) {
                    for(int i1 = 0; i1 < shapeBuffer.length; ++i1) {
                        for(int j1 = 0; j1 < shapeBuffer[0].length; ++j1) {
                            if(shapeBuffer[i1][j1]){
                                buffer[i + 1 + i1][j + 1 + j1] = true;
                            }
                        }
                    }
                }
            }
        }

        for(int i1 = rectifiedMargin + 1; i1 < forbiddenArea.length + rectifiedMargin + 1; ++i1) {
            for(int j1 = rectifiedMargin + 1; j1 < forbiddenArea[0].length + rectifiedMargin + 1; ++j1) {
                if(buffer[i1][j1]){
                    forbiddenArea[i1 - rectifiedMargin -  1][j1 - rectifiedMargin - 1] = true;
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
