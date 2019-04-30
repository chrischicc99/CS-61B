package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * The shortest paths through an edge-weighted graph.
 * By overrriding methods getWeight, setWeight, getPredecessor, and
 * setPredecessor, the client can determine how to represent the weighting
 * and the search results.  By overriding estimatedDistance, clients
 * can search for paths to specific destinations using A* search.
 *
 * @author Chris Chi
 */
public abstract class ShortestPaths {
    /**
     * The vertexMap from vertex id to its vertex.
     */
    private Map<Integer, DistanceVertex> vertexMap;

    /**
     * The shortest paths in G from SOURCE.
     */
    public ShortestPaths(Graph G, int source) {
        this(G, source, 0);
    }

    /**
     * A shortest path in G from SOURCE to DEST.
     */
    public ShortestPaths(Graph G, int source, int dest) {
        _G = G;
        _source = source;
        _dest = dest;
        vertexMap = new HashMap<>();
    }

    /**
     * Initialize the shortest paths.  Must be called before using
     * getWeight, getPredecessor, and pathTo.
     */
    public void setPaths() {
        for (int v : _G.vertices()) {
            vertexMap.put(v,
                    new DistanceVertex(v, Double.MAX_VALUE, Double.MAX_VALUE)
            );
        }
        vertexMap.get(_source).setGScore(0.0);
        vertexMap.get(_source).setHScore(estimatedDistance(_source));
    }

    /**
     * Returns the starting vertex.
     */
    public int getSource() {
        return _source;
    }

    /**
     * Returns the target vertex, or 0 if there is none.
     */
    public int getDest() {
        return _dest;
    }

    /**
     * Returns the current weight of vertex V in the graph.  If V is
     * not in the graph, returns positive infinity.
     */
    public abstract double getWeight(int v);

    /**
     * Set getWeight(V) to W. Assumes V is in the graph.
     */
    protected abstract void setWeight(int v, double w);

    /**
     * Returns the current predecessor vertex of vertex V in the graph, or 0 if
     * V is not in the graph or has no predecessor.
     */
    public abstract int getPredecessor(int v);

    /**
     * Set getPredecessor(V) to U.
     */
    protected abstract void setPredecessor(int v, int u);

    /**
     * Returns an estimated heuristic weight of the shortest path from vertex
     * V to the destination vertex (if any).  This is assumed to be less
     * than the actual weight, and is 0 by default.
     */
    protected double estimatedDistance(int v) {
        return 0.0;
    }

    /**
     * Returns the current weight of edge (U, V) in the graph.  If (U, V) is
     * not in the graph, returns positive infinity.
     */
    protected abstract double getWeight(int u, int v);

    /**
     * Returns a list of vertices starting at _source and ending
     * at V that represents a shortest path to V.  Invalid if there is a
     * destination vertex other than V.
     */
    public List<Integer> pathTo(int v) {
        if (v == 0) {
            return Collections.emptyList();
        }

        TreeSet<DistanceVertex> openSet = new TreeSet<>();

        openSet.add(new DistanceVertex(
                _source, 0.0, estimatedDistance(_source)
        ));
        while (!openSet.isEmpty()) {
            DistanceVertex current = openSet.pollFirst();
            if (current == null || current.isMarked()) {
                continue;
            }
            int u = current.getVertexId();
            if (u == v) {
                return buildPath(current);
            }

            current.mark();

            for (int w : _G.successors(u)) {
                double newDistance = current.getGScore() + getWeight(u, w);
                DistanceVertex wVertex = vertexMap.get(w);
                if (!wVertex.isMarked() && wVertex.getGScore() > newDistance) {
                    openSet.remove(wVertex);
                    wVertex.setGScore(newDistance);
                    wVertex.setFrom(u);
                    wVertex.setHScore(estimatedDistance(w));
                    openSet.add(wVertex);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Build the path from _source to vertex after find the path.
     * @param vertex the destination vertex.
     * @return the ids from _source to vertex.
     */
    private List<Integer> buildPath(DistanceVertex vertex) {
        List<Integer> path = new ArrayList<>();
        while (vertex != null) {
            path.add(vertex.getVertexId());
            vertex = vertexMap.get(vertex.getFrom());
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns a list of vertices starting at the source and ending at the
     * destination vertex. Invalid if the destination is not specified.
     */
    public List<Integer> pathTo() {
        return pathTo(getDest());
    }

    /**
     * The graph being searched.
     */
    protected final Graph _G;
    /**
     * The starting vertex.
     */
    private final int _source;
    /**
     * The target vertex.
     */
    private final int _dest;

}
