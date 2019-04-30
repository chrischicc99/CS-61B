package graph;

/* See restrictions in Graph.java. */

import java.util.HashMap;
import java.util.Map;

/**
 * A partial implementation of ShortestPaths that contains the weights of
 * the vertices and the predecessor edges.   The client needs to
 * supply only the two-argument getWeight method.
 *
 * @author Chris Chi
 */
public abstract class SimpleShortestPaths extends ShortestPaths {

    /**
     * The shortest paths in G from SOURCE.
     */
    public SimpleShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /**
     * A shortest path in G from SOURCE to DEST.
     */
    public SimpleShortestPaths(Graph G, int source, int dest) {
        super(G, source, dest);
    }

    /**
     * Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     * not in the graph, returns positive infinity.
     */
    @Override
    protected abstract double getWeight(int u, int v);

    @Override
    public double getWeight(int v) {
        return vertexWeight.getOrDefault(v, Double.MAX_VALUE);
    }

    @Override
    protected void setWeight(int v, double w) {
        vertexWeight.put(v, w);
    }

    @Override
    public int getPredecessor(int v) {
        return predecessor.getOrDefault(v, 0);
    }

    @Override
    protected void setPredecessor(int v, int u) {
        predecessor.put(v, u);
    }

    /**
     * the vertex to its weight map.
     */
    private Map<Integer, Double> vertexWeight = new HashMap<>();
    /**
     * the vertex to its predecessor map.
     */
    private Map<Integer, Integer> predecessor = new HashMap<>();


}
