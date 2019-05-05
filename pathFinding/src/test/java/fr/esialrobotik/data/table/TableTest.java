package fr.esialrobotik.data.table;

import fr.esialrobotik.data.table.shape.Circle;
import fr.esialrobotik.data.table.shape.Polygon;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by icule on 28/03/17.
 */
public class TableTest {
    //This example is extract from the esialrobotik wiki documentation
    private String loadingExample = "{\n" +
            "  \"tailleX\": 3000,\n" +
            "  \"tailleY\": 2000,\n" +
            "  \"couleur0\": \"Vert\",\n" +
            "  \"couleur3000\": \"Orange\",\n" +
            "  \"zonesInterdites\" : [\n" +
            "    {\n" +
            "      \"forme\" : \"cercle\",\n" +
            "      \"centre\" :\n" +
            "        {\n" +
            "          \"x\" : 1000,\n" +
            "          \"y\" : 1500\n" +
            "        },\n" +
            "      \"rayon\": 200\n" +
            "    },\n" +
            "    {\n" +
            "      \"forme\" : \"cercle\",\n" +
            "      \"centre\" :\n" +
            "        {\n" +
            "          \"x\" : 300,\n" +
            "          \"y\" : 1500\n" +
            "        },\n" +
            "      \"rayon\": 200\n" +
            "    },\n" +
            "    {\n" +
            "      \"forme\" : \"polygone\",\n" +
            "      \"points\" : [\n" +
            "        {\n" +
            "          \"x\" : 100,\n" +
            "          \"y\" : 100\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 100,\n" +
            "          \"y\" : 200\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 200,\n" +
            "          \"y\" : 100\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"forme\" : \"polygone\",\n" +
            "      \"points\" : [\n" +
            "        {\n" +
            "          \"x\" : 1000,\n" +
            "          \"y\" : 100\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 1000,\n" +
            "          \"y\" : 200\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 1200,\n" +
            "          \"y\" : 300\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 1200,\n" +
            "          \"y\" : 50\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private Table table;
    private Table toLoad;

    @Before
    public void setUp(){
        table = new Table();
        toLoad = new Table();
    }

    @Test
    public void testLoading() throws IOException {
        table.loadJsonFromString(loadingExample);
        assertEquals(3000, table.getxSize());
        assertEquals(2000, table.getySize());
        assertEquals(TableColor.COLOR_0.toString(), table.getColor0Name());
        assertEquals(TableColor.COLOR_3000.toString(), table.getColor3000Name());

        assertTrue(table.getShapeList().get(0) instanceof Circle);
        Circle temp = (Circle)table.getShapeList().get(0);
        assertEquals(1000, temp.getCenter().getX());
        assertEquals(1500, temp.getCenter().getY());
        assertEquals(200, temp.getRadius());

        assertTrue(table.getShapeList().get(1) instanceof Circle);
        temp = (Circle)table.getShapeList().get(1);
        assertEquals(300, temp.getCenter().getX());
        assertEquals(1500, temp.getCenter().getY());
        assertEquals(200, temp.getRadius());

        assertTrue(table.getShapeList().get(2) instanceof Polygon);
        Polygon polygon = (Polygon)table.getShapeList().get(2);
        assertEquals(3, polygon.getVertexList().size());
        assertEquals(100, polygon.getVertexList().get(0).getX());
        assertEquals(100, polygon.getVertexList().get(0).getY());
        assertEquals(100, polygon.getVertexList().get(1).getX());
        assertEquals(200, polygon.getVertexList().get(1).getY());
        assertEquals(200, polygon.getVertexList().get(2).getX());
        assertEquals(100, polygon.getVertexList().get(2).getY());

        table.drawTable();
        table.computeForbiddenArea(67);

        File f = new File("table.tbl");

        table.saveToFile(f.getName());
        toLoad.loadFromSaveFile(f.getName());

        assertEquals(table.toString(), toLoad.toString());

        //f.delete();
    }
}