package fr.esialrobotik.data.table.astar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sun.org.apache.xpath.internal.SourceTree;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.Table;
import fr.esialrobotik.data.table.TableTest;
import fr.esialrobotik.pathFinding.PathFindingConfiguration;
import fr.esialrobotik.pathFinding.PathFindingModule;
import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by icule on 16/05/17.
 */
public class AstarTest {
    String config = "{\n" +
            "  \"tailleX\": 200,\n" +
            "  \"tailleY\": 200,\n" +
            "  \"couleurDepartYPositif\": \"Rouge\",\n" +
            "  \"couleurDepartYNegatif\": \"Bleu\",\n" +
            "  \"zonesInterdites\" : [\n" +
            "    {\n" +
            "      \"forme\" : \"polygone\",\n" +
            "      \"points\" : [\n" +
            "        {\n" +
            "          \"x\" : 50,\n" +
            "          \"y\" : 10\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 50,\n" +
            "          \"y\" : 80\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 60,\n" +
            "          \"y\" : 80\n" +
            "        },\n" +
            "        {\n" +
            "          \"x\" : 60,\n" +
            "          \"y\" : 10\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"forme\" : \"cercle\",\n" +
            "      \"centre\" :\n" +
            "        {\n" +
            "          \"x\" : 150,\n" +
            "          \"y\" : 120\n" +
            "        },\n" +
            "      \"rayon\": 20\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private Table table;

    @Test
    public void testAstar() {
        LoggerFactory.init(Level.TRACE);
        table = new Table();
        table.loadJsonFromString(config);
        table.drawTable();
        String ref = table.toString();
        table.computeForbiddenArea(10);
        shapePrinter(ref, table.toString());

        table.printTable();

        Astar astar = new Astar(table);
        astar.updateVoisinageInfo();

        assertFalse(table.isAreaForbiddenSafe(3, 3));
        assertFalse(table.isAreaForbiddenSafe(12, 5));
        Stack<Point> trajectory = astar.getChemin(new Point(3, 3), new Point(9, 5));
        System.out.println(trajectory.size());
        List<Point> simplified = LineSimplificator.getSimpleLines(trajectory);
        System.out.println(simplified.size());


        for(Point p : simplified) {
            System.out.println(p);
            assertFalse(table.isAreaForbidden(p.getX(), p.getY()));
        }

    }

    /*private String configModule = "{" +
            "\"tablePath\": \"table_test.tbl\"" +
            "}";*/
    private String configModule = "{" +
            "\"tablePath\": \"table.tbl\"" +
            "}";

    @Test
    public void testAstarFromInjector() {
        LoggerFactory.init(Level.TRACE);
        /*table = new Table();
        table.loadJsonFromString(config);
        table.drawTable();
        String ref = table.toString();
        table.computeForbiddenArea(10);
        shapePrinter(ref, table.toString());
        table.saveToFile("table_test.tbl");*/

        JsonParser parser = new JsonParser();
        JsonObject rootConfig = parser.parse(configModule).getAsJsonObject();
        PathFindingConfiguration configuration = new PathFindingConfiguration();
        configuration.loadConfig(rootConfig);

        Astar astar = Guice.createInjector(new PathFindingModule(configuration)).getInstance(Astar.class);
        astar.updateVoisinageInfo();

        //assertFalse(table.isAreaForbiddenSafe(3, 3));
        //assertFalse(table.isAreaForbiddenSafe(12, 5));
        //Stack<Point> trajectory = astar.getChemin(new Point(3, 3), new Point(9, 5));
        Stack<Point> trajectory = astar.getChemin(new Point(22, 91), new Point(100, 200));
        System.out.println(trajectory.size());
        List<Point> simplified = LineSimplificator.getSimpleLines(trajectory);
        System.out.println(simplified.size());


        for(Point p : simplified) {
            System.out.println(p);
            //assertFalse(table.isAreaForbidden(p.getX(), p.getY()));
        }

        (new File("table_test.tbl")).delete();

    }

    public void shapePrinter(String without, String with) {
        for(int i = 0; i < without.length(); ++i) {
            if(without.charAt(i) == '\n') {
                System.out.print('\n');
            }
            else {
                if(with.charAt(i) == without.charAt(i)) {
                    System.out.print(without.charAt(i));
                }
                else {
                    System.out.print('y');
                }
            }
        }
    }

}