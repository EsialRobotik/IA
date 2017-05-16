package fr.esialrobotik.data.table;

import com.google.gson.JsonObject;

/**
 * Created by icule on 28/03/17.
 */
public class Point {
    public enum DIRECTION{

        NO(false), N(true), NE(false), E(true), SE(false), S(true), SO(false), O(true), NULL(false);


        public final boolean isPureCardinalPoint;

        DIRECTION(boolean isCardinalPoint){
            this.isPureCardinalPoint = isCardinalPoint;
        }
    }

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

    public DIRECTION getDirectionToGoTo(Point p){
        if(this.x == p.x){
            if(this.y == p.y)
                return DIRECTION.NULL;
            else if(this.y < p.y)
                return DIRECTION.S;
            else
                return DIRECTION.N;
        }else if(this.x < p.x){
            if(this.y == p.y)
                return DIRECTION.E;
            else if(this.y > p.y)
                return DIRECTION.SE;
            else
                return DIRECTION.NE;
        }else{
            if(this.y == p.y)
                return DIRECTION.O;
            else if(this.y > p.y)
                return DIRECTION.SO;
            else
                return DIRECTION.NO;
        }
    }

    public String toString(){
        return "("+x+";"+y+")";
    }
}
