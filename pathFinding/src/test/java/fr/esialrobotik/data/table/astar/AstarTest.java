package fr.esialrobotik.data.table.astar;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import esialrobotik.ia.utils.log.LoggerFactory;
import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.Table;
import fr.esialrobotik.data.table.TableTest;
import org.apache.logging.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by icule on 16/05/17.
 */
public class AstarTest {
    String config = "{\n" +
            "  \"longueur\": 200,\n" +
            "  \"largeur\": 200,\n" +
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