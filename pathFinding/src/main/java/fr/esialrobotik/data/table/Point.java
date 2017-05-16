package fr.esialrobotik.data.table;

import com.google.gson.JsonObject;

/**
 * Created by icule on 28/03/17.
 */
public class Point {
    public int x;
    public int y;

    public Point(int x, int y){
        this.x =x;
        this.y = y;
    }

    public Point(JsonObject jsonObject){
        this.x = jsonObject.get("x").getAsInt();
        this.y = jsonObject.get("y").getAsInt();
    }

    public int getX(){
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
