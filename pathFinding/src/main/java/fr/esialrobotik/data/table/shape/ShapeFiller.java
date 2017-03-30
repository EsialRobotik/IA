package fr.esialrobotik.data.table.shape;

/**
 * Created by icule on 30/03/17.
 */
public class ShapeFiller {
    private final boolean board[][];

    public ShapeFiller(boolean[][] board){
        this.board = board;
    }

    public void fillBoard(){
        //We need to create a buffer to simplify the process
        boolean[][] buffer = new boolean[board.length][board[0].length];

        for(int i = 0; i < board.length; ++i) {
            for(int j = 0; j < board[0].length; ++j) {
                if(board[i][j]){
                    continue;
                }
                if(isInsideYAxisProjection(i, j) && isInsideXAxisProjection(i, j)){
                    buffer[i][j] = true;
                }
                else {
                    buffer[i][j] = false;
                }
            }
        }

        for(int i = 0; i < board.length; ++i) {
            for(int j = 0; j < board[0].length; ++j) {
                if(buffer[i][j]) {
                    board[i][j] = true;
                }
            }
        }
    }

    private boolean isInsideXAxisProjection(int x, int y){
        int before = 0;
        int after = 0;
        boolean inBorder = false;
        //We count the amount of time we encounter an edge on the x axis of the point, left and right sense.
        for(int i = 0; i < board.length; ++i) {
            boolean temp = board[i][y];
            if(temp) {
                if(!inBorder) {
                    if( i < x) {
                        ++before;
                    }
                    else {
                        ++after;
                    }
                }
                inBorder = true;
            }
            else {
                inBorder = false;
            }
        }
        return (after % 2) != 0 && (before % 2) != 0;
    }

    private boolean isInsideYAxisProjection(int x, int y){
        int before = 0;
        int after = 0;
        boolean inBorder = false;
        //We count the amount of time we encounter an edge on the x axis of the point, left and right sense.
        for(int i = 0; i < board[0].length; ++i) {
            boolean temp = board[x][i];
            if(temp) {
                if(!inBorder) {
                    if( i < y) {
                        ++before;
                    }
                    else {
                        ++after;
                    }
                }
                inBorder = true;
            }
            else {
                inBorder = false;
            }
        }
        return (after % 2) != 0 && (before % 2) != 0;
    }
}
