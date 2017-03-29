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

    public void drawShapeEdges(final boolean[][] board) {
        //We divide the circle in 1k part and compute point each time.
        // We work with the original millimetric table and will convert in cm when drawing
        final double radSplit = Math.PI / 1000;
        double currentValue = 0;
        while(currentValue < (Math.PI* 2)) {
            final int x = (int)(center.getX() + radius * Math.cos(currentValue)) / 10;
            final int y = (int)(center.getY() + radius * Math.sin(currentValue)) / 10;
            board[x][y] = true;
            currentValue += radSplit;
        }
    }
}
