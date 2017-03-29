package fr.esialrobotik.data.table.shape;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.esialrobotik.data.table.Point;

/**
 * Created by icule on 29/03/17.
 */
public class Circle extends Shape{
    private Point center;
    private int radius;

    public Circle(JsonObject jsonObject){
      this.center = new Point(jsonObject.get("centre").getAsJsonObject());
      this.radius = jsonObject.get("rayon").getAsInt();
    }

    public int getRadius(){
        return radius;
    }

    public Point getCenter(){
        return this.center;
    }
}
