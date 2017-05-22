package fr.esialrobotik.pathFinding;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.Table;
import fr.esialrobotik.data.table.astar.Astar;

import java.io.IOException;

/**
 * Created by icule on 20/05/17.
 */
public class PathFindingModule extends AbstractModule{
    private PathFindingConfiguration pathFindingConfiguration;

    public PathFindingModule(PathFindingConfiguration pathFindingConfiguration) {
        this.pathFindingConfiguration = pathFindingConfiguration;
    }


    protected void configure() {
        Table table = null;
        try {
            table = new Table(pathFindingConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        bind(Table.class).toInstance(table);
        bind(PathFindingConfiguration.class).toInstance(pathFindingConfiguration);
        bind(PathFinding.class).in(Singleton.class);
        bind(Astar.class).in(Singleton.class);
    }
}
