package fr.esialrobotik.pathFinding;

import fr.esialrobotik.data.table.Point;
import fr.esialrobotik.data.table.Table;
import fr.esialrobotik.data.table.astar.Astar;
import fr.esialrobotik.data.table.astar.LineSimplificator;

import javax.inject.Inject;
import javax.swing.text.Position;
import java.util.List;
import java.util.Stack;

/**
 * Created by icule on 19/05/17.
 */
public class PathFinding {
    //TODO add an interface to allow to change the path finding algorithm
    private Astar astar;

    private boolean computationStart;
    private boolean computationEnded;
    private List<Point> computedPath;

    @Inject
    public PathFinding(Astar astar) {
        this.astar = astar;
    }

    public void computePath(final Point start, final Point end) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                Stack<Point> path = astar.getChemin(start, end);
                computedPath = LineSimplificator.getSimpleLines(path);
                computationEnded = true;
            }
        });
        computationStart = false;
        computedPath = null;
        t.start();
    }

    public boolean isComputationStart() {
        return this.computationStart;
    }

    public boolean isComputationEnded() {
        return this.computationEnded;
    }

    public List<Point> getLastComputedPath() {
        return this.computedPath;
    }
}
