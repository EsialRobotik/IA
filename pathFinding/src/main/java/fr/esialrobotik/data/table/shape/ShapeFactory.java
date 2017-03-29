package fr.esialrobotik.data.table.shape;

import com.google.gson.JsonObject;

/**
 * Created by icule on 28/03/17.
 */
public class ShapeFactory {
    public static Shape getShape(JsonObject jsonObject){
        String shapeName = jsonObject.get("forme").getAsString();
        if(shapeName.equals("cercle")){
            return new Circle(jsonObject);
        }
        else if(shapeName.equals("polygone")){
            return new Polygon(jsonObject);
        }
        else {
            throw new RuntimeException("Shape " + shapeName + " cannot be load");
        }
    }
}
