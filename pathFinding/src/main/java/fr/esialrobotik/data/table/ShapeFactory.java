package fr.esialrobotik.data.table;

import com.google.gson.JsonObject;

/**
 * Created by icule on 28/03/17.
 */
public class ShapeFactory {
    public static Shape getShape(JsonObject jsonObject){
        String shapeName = jsonObject.get("forme").getAsString();
        if(shapeName.equals("cercle")){
            //TODO
        }
        else if(shapeName.equals("polygone")){
            //TODO
        }
        else {
            throw new RuntimeException("Shape " + shapeName + " cannot be load");
        }
        return null;
    }
}
