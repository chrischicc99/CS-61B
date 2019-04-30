package graph;

/* See restrictions in Graph.java. */

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A partial implementation of Graph containing elements common to
 * directed and undirected graphs.
 *
 * @author Chris Chi
 */
abstract class GraphObj extends Graph {
    /**
     * The ID position in edge triple.
     */
    protected static final int ID_POS = 0;
    /**
     * The from position in edge triple.
     */
    protected static final int FROM_POS = 1;
    /**
     * The to position in edge triple.
     */
    protected static final int TO_POS = 2;

    /**
     * the vertices.
     */
    protected List<Integer> vertices;

    /**
     * list of [id, from, to].
     */
    protected List<int[]> edges;

    /**
     * A new, empty Graph.
     */
    GraphObj() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    @Override
    public int vertexSize() {
        return this.vertices.size();
    }

    @Override
    public int maxVertex() {
        return this.vertices.stream()
            .mapToInt(v -> v)
            .max().orElse(0);
    }

    /**
     * @return the max edge id to define new edge.
     */
    protected int maxEdge() {
        return this.edges.stream()
            .mapToInt(v -> v[ID_POS])
            .max().orElse(0);
    }

    @Override
    public int edgeSize() {
        return (int) this.edges.stream()
            .map(e -> e[ID_POS])
            .distinct()
            .count();
    }

    @Override
    public abstract boolean isDirected();

    @Override
    public int outDegree(int v) {
        return (int) edges.stream()
            .filter(e -> e[FROM_POS] == v)
            .count();
    }

    @Override
    public abstract int inDegree(int v);

    @Override
    public boolean contains(int u) {
        return vertices.stream()
            .anyMatch(id -> id == u);
    }

    @Override
    public boolean contains(int u, int v) {
        return edges.stream()
            .anyMatch(e -> e[FROM_POS] == u && e[TO_POS] == v);
    }

    @Override
    public int add() {
        int vertexId = maxVertex() + 1;
        vertices.add(vertexId);

        return vertexId;
    }

    @Override
    public int add(int u, int v) {
        int edgeId = maxEdge() + 1;
        edges.add(new int[] {edgeId, u, v});

        return edgeId;
    }

    @Override
    public void remove(int v) {
        vertices.removeIf(id -> id == v);
    }

    @Override
    public void remove(int u, int v) {
        edges.removeIf(e -> e[FROM_POS] == u && e[TO_POS] == v);
    }

    @Override
    public Iteration<Integer> vertices() {
        return Iteration.iteration(vertices);
    }

    @Override
    public Iteration<Integer> successors(int v) {
        return Iteration.iteration(
            edges.stream()
                .filter(e -> e[FROM_POS] == v)
                .map(e -> e[TO_POS])
                .collect(Collectors.toList())
        );
    }

    @Override
    public abstract Iteration<Integer> predecessors(int v);

    @Override
    public Iteration<int[]> edges() {
        return Iteration.iteration(
            edges.stream()
                .map(e -> new int[] {e[FROM_POS], e[TO_POS]})
                .collect(Collectors.toList())
        );
    }

    @Override
    protected void checkMyVertex(int v) {
        super.checkMyVertex(v);
    }

    @Override
    protected int edgeId(int u, int v) {
        return edges.stream()
            .filter(e -> e[FROM_POS] == u && e[TO_POS] == v)
            .map(e -> e[ID_POS])
            .findAny().orElse(0);
    }

}
