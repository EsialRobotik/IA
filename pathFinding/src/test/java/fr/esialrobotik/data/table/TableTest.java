package fr.esialrobotik.data.table;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.esialrobotik.data.table.shape.*;
import fr.esialrobotik.data.table.shape.Polygon;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import java.awt.*;

import static org.junit.Assert.*;

/**
 * Created by icule on 28/03/17.
 */
public class TableTest {
    //This example is extract from the esialrobotik wiki documentation
    private String loadingExample = "{\n" +
            "  \"longueur\": 3000,\n" +
            "  \"largeur\": 2000,\n" +
            "  \"couleurDepartXPositif\": \"Rouge\",\n" +
            "  \"couleurDepartXNegatif\": \"Bleu\",\n" +
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

    public class TableModuleTest extends AbstractModule{
        protected void configure() {
            bind(Gson.class);
            bind(Table.class);
        }
    }

    @Before
    public void setUp(){
        Injector injector = Guice.createInjector(new TableModuleTest());
        table = injector.getInstance(Table.class);
    }

    @Test
    public void testLoading(){
        table.loadFromString(loadingExample);
        assertEquals(3000, table.getLength());
        assertEquals(2000, table.getWidth());
        assertEquals(TableColor.RED, table.getPositiveStartColor());
        assertEquals(TableColor.BLUE, table.getNegativeStartColor());

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

        table.printTable();

        table.computeForbiddenArea(67);

        table.printTable();
    }
}