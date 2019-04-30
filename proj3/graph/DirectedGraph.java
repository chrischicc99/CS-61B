package graph;

/* See restrictions in Graph.java. */

import java.util.stream.Collectors;

/**
 * Represents a general unlabeled directed graph whose vertices are denoted by
 * positive integers. Graphs may have self edges.
 *
 * @author Chris Chi
 */
public class DirectedGraph extends GraphObj {

    @Override
    public boolean isDirected() {
        return true;
    }

    @Override
    public int inDegree(int v) {
        return (int) edges.stream()
            .map(e -> e[TO_POS] == v)
            .count();
    }

    @Override
    public Iteration<Integer> predecessors(int v) {
        return Iteration.iteration(
            edges.stream()
                .filter(e -> e[TO_POS] == v)
                .map(e -> e[ID_POS])
                .collect(Collectors.toList())
        );
    }

}
