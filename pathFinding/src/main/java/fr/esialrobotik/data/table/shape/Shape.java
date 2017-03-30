package fr.esialrobotik.data.table.shape;

/**
 * Created by icule on 28/03/17.
 */
public abstract class Shape {

    //Board is a centimetrique grid off the table. (don't need more)
    public abstract boolean[][] drawShapeEdges(int length, int width);

    protected boolean[][] getEmptyBoard(int length, int width) {
        boolean[][] res = new boolean[length][width];
        for(int i = 0; i < length; ++i) {
            for(int j = 0; j < width; ++j) {
                res[i][j] = false;
            }
        }
        return res;
    }
}
