package fr.esialrobotik.pathFinding;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import fr.esialrobotik.data.table.Table;
import fr.esialrobotik.data.table.astar.Astar;

/**
 * Created by icule on 20/05/17.
 */
public class PathFindingModule extends AbstractModule{
    private PathFindingConfiguration pathFindingConfiguration;

    public PathFindingModule(PathFindingConfiguration pathFindingConfiguration) {
        this.pathFindingConfiguration = pathFindingConfiguration;
    }


    protected void configure() {
        bind(PathFindingConfiguration.class).toInstance(pathFindingConfiguration);
        bind(PathFinding.class).in(Singleton.class);
        bind(Table.class).in(Singleton.class);
        bind(Astar.class).in(Singleton.class);
    }
}
