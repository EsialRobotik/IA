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

    public boolean[][] drawShapeEdges(int length, int width) {
        //We double the buffer to be large
        boolean[][] board = this.getEmptyBoard(length * 3, width * 3);

        //We draw the shape edges segment by segment
        for(int i =0; i < vertexList.size() - 1 ; ++i) {
            drawSegment(board, vertexList.get(i), vertexList.get(i+1), length, width);
        }
        //Only the last segment is left
        drawSegment(board, vertexList.get(0), vertexList.get(vertexList.size() - 1), length, width);

        ShapeFiller shapeFiller = new ShapeFiller(board);
        shapeFiller.fillBoard();

        return board;
    }

    private void drawSegment(boolean[][] board, final Point a, final Point b, int length, int width) {
        board[a.getX()/10 + length][a.getY()/10 + width] = true;
        board[b.getX()/10 + length][b.getY()/10 + width] = true;
        //We divide the segment in a 1k point.
        double deltaX = (b.getX() - a.getX()) / 10000.;
        double deltaY = (b.getY() - a.getY()) / 10000.;

        double x = a.getX() / 10. + length;
        double y = a.getY() / 10. + width;

        for(int i = 0; i < 1000; ++i) {
            board[(int)x][(int)y] = true;
            x += deltaX;
            y += deltaY;
        }
        ShapeFiller shapeFiller = new ShapeFiller(board);
        shapeFiller.fillBoard();
    }
}
