package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.shape.Circle;
import fr.esialrobotik.data.table.shape.Shape;
import fr.esialrobotik.data.table.shape.ShapeFactory;
import fr.esialrobotik.pathFinding.PathFindingConfiguration;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icule on 28/03/17.
 */
@Singleton
public class Table {
    private int xSize;
    private int rectifiedXSize;
    private int ySize;
    private int rectifiedYSize;
    private TableColor positiveStart;
    private TableColor negativeStart;

    private boolean[][] forbiddenArea;

    private List<Shape> shapeList;

    public Table(){

    }

    @Inject
    public Table(PathFindingConfiguration pathFindingConfiguration) throws IOException {
        this.loadFromSaveFile(pathFindingConfiguration.getTableConfigurationPath());
    }

    public void loadJsonFromFile(String filePath) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(new FileReader(filePath)).getAsJsonObject());
    }

    public void loadJsonFromString(String json){
        JsonParser parser = new JsonParser();
        loadConfig(parser.parse(json).getAsJsonObject());
    }

    private void loadConfig(JsonObject rootElement){
        shapeList = new ArrayList<Shape>();
        xSize = rootElement.get("tailleX").getAsInt();
        rectifiedXSize = xSize / 10;
        ySize = rootElement.get("tailleY").getAsInt();
        rectifiedYSize = ySize / 10;
        positiveStart = TableColor.getTableColorFromConfigName(rootElement.get("couleurDepartYPositif").getAsString());
        negativeStart = TableColor.getTableColorFromConfigName(rootElement.get("couleurDepartYNegatif").getAsString());

        for(JsonElement jsonElement : rootElement.getAsJsonArray("zonesInterdites")){
            shapeList.add(ShapeFactory.getShape(jsonElement.getAsJsonObject()));
        }
    }

    public int getxSize(){
        return xSize;
    }

    public int getySize(){
        return ySize;
    }

    public int getRectifiedXSize() {
        return rectifiedXSize;
    }

    public int getRectifiedYSize() {
        return rectifiedYSize;
    }

    public TableColor getPositiveStartColor(){
        return positiveStart;
    }

    public TableColor getNegativeStartColor(){
        return negativeStart;
    }

    public List<Shape> getShapeList(){
        return shapeList;
    }

    public void drawTable() {
        this.forbiddenArea = new boolean[rectifiedXSize][rectifiedYSize];
        for(int i = 0; i < rectifiedXSize; ++i) {
            for(int j = 0; j < rectifiedYSize; ++j) {
                this.forbiddenArea[i][j] = false;
            }
        }

        for(Shape shape : shapeList) {
            boolean[][] temp = shape.drawShapeEdges(rectifiedXSize, rectifiedYSize);

            for(int i = 0; i < rectifiedXSize; ++i) {
                for(int j = 0; j < rectifiedYSize; ++j) {
                    if(temp[i + rectifiedXSize][j + rectifiedYSize]){
                        this.forbiddenArea[i][j] = true;
                    }
                }
            }
        }
    }

    private void setValue(boolean[][] board, int x, int y, boolean val) {
        if(x >= 0 && y >= 0 && x <= board.length && y <= board[0].length) {
            board[x][y] = val;
        }
    }

    public void computeForbiddenArea(int margin) {
        int rectifiedMargin = (int)Math.ceil(margin / 10.);

        int boardLength = forbiddenArea.length;
        int boardWidth = forbiddenArea[0].length;


        boolean[][] buffer = new boolean[boardLength + 2 * (rectifiedMargin + 1)][boardWidth + 2 * (rectifiedMargin + 1)];
        for(int i = 0; i < buffer.length; ++i) {
            for(int j = 0; j < buffer[0].length; ++j) {
                buffer[i][j] = false;
            }
        }

        Circle circle = new Circle(rectifiedMargin * 10, rectifiedMargin * 10, rectifiedMargin * 10);
        int bufferSize = (rectifiedMargin + 1) * 2;
        boolean[][] shapeBuffer = circle.drawShapeEdges((rectifiedMargin + 1) * 2, (rectifiedMargin + 1) * 2);

        for(int i = 0; i < boardLength; ++i) {
            for(int j = 0; j < boardWidth; ++j) {
                if(forbiddenArea[i][j]) {
                    for(int i1 = bufferSize; i1 < bufferSize * 2; ++i1) {
                        for(int j1 = bufferSize; j1 < bufferSize * 2; ++j1) {
                            if(shapeBuffer[i1][j1]){
                                setValue(buffer, i + 1 + i1 - bufferSize, j + 1 + j1 - bufferSize, true);
                                setValue(buffer, i + 2 + i1 - bufferSize, j + 1 + j1 - bufferSize, true);
                                setValue(buffer, i + 1 + i1 - bufferSize, j + 2 + j1 - bufferSize, true);
                                setValue(buffer, i + 2 + i1 - bufferSize, j + 2 + j1 - bufferSize, true);
                            }
                        }
                    }
                }
            }
        }

        for(int i1 = rectifiedMargin + 1; i1 < forbiddenArea.length + rectifiedMargin + 1; ++i1) {
            for(int j1 = rectifiedMargin + 1; j1 < forbiddenArea[0].length + rectifiedMargin + 1; ++j1) {
                if(buffer[i1][j1]){
                    forbiddenArea[i1 - rectifiedMargin -  1][j1 - rectifiedMargin - 1] = true;
                }
            }
        }

        //Now we disable table side we draw directly in the main buffer
        for(int i = 0; i < rectifiedMargin; ++i) {
            for(int j = 0; j < rectifiedYSize; ++j) {
                forbiddenArea[i][j] = true;
            }
        }

        for(int i = rectifiedXSize - rectifiedMargin; i < rectifiedXSize; ++i) {
            for(int j = 0; j < rectifiedYSize; ++j) {
                forbiddenArea[i][j] = true;
            }
        }

        for(int i = 0; i < rectifiedXSize; ++i) {
            for(int j = 0; j < rectifiedMargin; ++j) {
                forbiddenArea[i][j] = true;
            }

            for(int j = rectifiedYSize - rectifiedMargin; j < rectifiedYSize; ++j) {
                forbiddenArea[i][j] = true;
            }
        }

    }

    public void printTable() {
        for(int i = 0; i < forbiddenArea.length; ++i) {
            for(int j = 0; j < forbiddenArea[0].length; ++j){
                System.out.print(forbiddenArea[i][j]?"x":"o");
            }
            System.out.print("\n");
        }
    }

    public void printBuffer(boolean[][] buffer) {
        for(int i = 0; i < buffer.length; ++i) {
            for(int j = 0; j < buffer[0].length; ++j){
                System.out.print(buffer[i][j]?"x":"o");
            }
            System.out.print("\n");
        }
    }

    public String toString() {
        String res = "";
        for(int i = 0; i < forbiddenArea.length; ++i) {
            for(int j = 0; j < forbiddenArea[0].length; ++j){
                res += forbiddenArea[i][j]?"x":"o";
            }
            res += "\n";
        }
        return res;
    }

    public void saveToFile(String filename) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            bw.write(this.getxSize() + " " + this.getySize() + "\n");
            bw.write(this.toString());
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void loadFromSaveFile(String filename) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            line = br.readLine();
            String[] temp = line.split(" ");
            this.xSize = Integer.parseInt(temp[0]);
            this.rectifiedXSize = xSize / 10;
            this.ySize = Integer.parseInt(temp[1]);
            this.rectifiedYSize = ySize / 10;

            this.forbiddenArea = new boolean[rectifiedXSize][rectifiedYSize];
            int acc = 0;
            while ((line = br.readLine()) != null) {
                for(int j = 0; j < forbiddenArea[0].length ; ++j) {
                    if(line.charAt(j) == 'x') {
                        forbiddenArea[acc][j] = true;
                    }
                    else {
                        forbiddenArea[acc][j] = false;
                    }
                }
                ++acc;
            }
        }
        finally {
            if(br != null) {
                br.close();
            }
        }
    }

    public static class TableModule extends AbstractModule {
        protected void configure() {
            bind(Gson.class);
            bind(Table.class);
        }
    }

    public boolean isAreaForbidden(int x, int y) {
        return forbiddenArea[x][y];
    }

    public boolean isAreaForbiddenSafe(int x, int y) {
        if(x < 0 || y < 0 || x >= rectifiedXSize || y >= rectifiedYSize) {
            return false;
        }
        return forbiddenArea[x][y];
    }

    public static void main(String[] args) throws IOException {
//        File f = new File("l");
//        System.out.println(f.getAbsoluteFile());
//        Injector injector = Guice.createInjector(new TableModule());
//        Table table = injector.getInstance(Table.class);
//        table.loadJsonFromFile(args[0]);
//
//        table.drawTable();
//        //TODO get the dimension by the robot file
//        table.computeForbiddenArea(Integer.parseInt(args[1]));
//        table.saveToFile("test.tbl");
//
//        Table saved = injector.getInstance(Table.class);
//        saved.loadFromSaveFile("test.tbl");

        Table table = new Table();
        table.loadJsonFromFile("pathFinding/table.json");

        table.drawTable();
//        table.computeForbiddenArea(182);
        table.computeForbiddenArea(180);

        File f = new File("table.tbl");

        table.saveToFile(f.getName());
        System.out.println("Generation of the table succesfull.");
    }
}
