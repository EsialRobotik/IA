package fr.esialrobotik.data.table.shape;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.esialrobotik.data.table.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icule on 29/03/17.
 */
public class Polygon extends Shape {
    private List<Point> vertexList;

    public Polygon(JsonObject jsonObject){
        vertexList = new ArrayList<Point>();
        JsonArray vertexArray = jsonObject.getAsJsonArray("points");
        for(JsonElement jsonElement : vertexArray){
            vertexList.add(new Point(jsonElement.getAsJsonObject()));
        }
    }

    public List<Point> getVertexList(){
        return this.vertexList;
    }
}
