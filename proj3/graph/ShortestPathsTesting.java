package graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortestPathsTesting {
    @Test
    public void testDirectedShortestPath1() {
        Graph g = new DirectedGraph();
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.vertexSize());

        int v1 = g.add();
        int v2 = g.add();
        int v3 = g.add();
        int v4 = g.add();

        int e1 = g.add(v1, v2);
        int e2 = g.add(v1, v3);
        int e3 = g.add(v3, v2);
        int e4 = g.add(v2, v4);

        Paths paths = new Paths(g, v1, v4);
        paths.setPaths();
        paths.setWeight(v1, v2, 10);
        paths.setWeight(v1, v3, 5);
        paths.setWeight(v3, v2, 6);
        paths.setWeight(v2, v4, 7);

        List<Integer> path = paths.pathTo();
        assertEquals(Arrays.asList(v1, v2, v4), path);
    }

    @Test
    public void testDirectedShortestPath2() {
        Graph g = new DirectedGraph();
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.vertexSize());

        int v1 = g.add();
        int v2 = g.add();
        int v3 = g.add();
        int v4 = g.add();

        int e1 = g.add(v1, v2);
        int e2 = g.add(v1, v3);
        int e3 = g.add(v3, v2);
        int e4 = g.add(v2, v4);

        Paths paths = new Paths(g, v1, v4);
        paths.setPaths();
        paths.setWeight(v1, v2, 10);
        paths.setWeight(v1, v3, 5);
        paths.setWeight(v3, v2, 4);
        paths.setWeight(v2, v4, 7);

        List<Integer> path = paths.pathTo();
        assertEquals(Arrays.asList(v1, v3, v2, v4), path);
    }

    @Test
    public void testUndirectedShortestPath1() {
        Graph g = new UndirectedGraph();
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.vertexSize());

        int v1 = g.add();
        int v2 = g.add();
        int v3 = g.add();
        int v4 = g.add();

        int e1 = g.add(v1, v2);
        int e2 = g.add(v1, v3);
        int e3 = g.add(v3, v2);
        int e4 = g.add(v2, v4);

        Paths paths = new Paths(g, v4, v1);
        paths.setPaths();
        paths.setWeight(v1, v2, 10);
        paths.setWeight(v1, v3, 5);
        paths.setWeight(v3, v2, 4);
        paths.setWeight(v2, v4, 7);

        List<Integer> path = paths.pathTo();
        assertEquals(Arrays.asList(v4, v2, v3, v1), path);
    }

    @Test
    public void testUndirectedShortestPath2() {
        Graph g = new UndirectedGraph();
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.vertexSize());

        int v1 = g.add();
        int v2 = g.add();
        int v3 = g.add();
        int v4 = g.add();

        int e1 = g.add(v1, v2);
        int e2 = g.add(v1, v3);
        int e3 = g.add(v3, v2);
        int e4 = g.add(v2, v4);

        Paths paths = new Paths(g, v4, v1);
        paths.setPaths();
        paths.setWeight(v1, v2, 10);
        paths.setWeight(v1, v3, 5);
        paths.setWeight(v3, v2, 8);
        paths.setWeight(v2, v4, 7);

        List<Integer> path = paths.pathTo();
        assertEquals(Arrays.asList(v4, v2, v1), path);
    }

    static class Paths extends SimpleShortestPaths {

        Paths(Graph G, int source, int dest) {
            super(G, source, dest);
        }

        @Override
        protected double getWeight(int u, int v) {
            int edgeId = _G.edgeId(u, v);
            return edgeWeight.getOrDefault(edgeId, Double.MAX_VALUE);
        }

        protected void setWeight(int u, int v, double w) {
            int edgeId = _G.edgeId(u, v);
            if (edgeId != 0) {
                edgeWeight.put(edgeId, w);
            }
        }

        private Map<Integer, Double> edgeWeight = new HashMap<>();
    }
}
