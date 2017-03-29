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

    public void drawShapeEdges(boolean[][] board) {
        //We draw the shape edges segment by segment
        for(int i =0; i < vertexList.size() - 1 ; ++i) {
            drawSegment(board, vertexList.get(i), vertexList.get(i+1));
        }
        //Only the last segment is left
        drawSegment(board, vertexList.get(0), vertexList.get(vertexList.size() - 1));
    }

    private void drawSegment(boolean[][] board, final Point a, final Point b) {
        board[a.getX()/10][a.getY()/10] = true;
        board[b.getX()/10][b.getY()/10] = true;
        //We divide the segment in a 1k point.
        double deltaX = (b.getX() - a.getX()) / 10000.;
        double deltaY = (b.getY() - a.getY()) / 10000.;

        double x = a.getX() / 10.;
        double y = a.getY() / 10.;
        for(int i = 0; i < 1000; ++i) {
            board[(int)x][(int)y] = true;
            x += deltaX;
            y += deltaY;
        }
    }
}
