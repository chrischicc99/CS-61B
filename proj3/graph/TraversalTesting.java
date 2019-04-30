package graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TraversalTesting {
    @Test
    public void testBreadthFirstTraversal() {
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

        Queue<Integer> visitOrder = new LinkedList<>(
                Arrays.asList(v1, v2, v3, v4)
        );

        Traversal traversal = new BreadthFirstTraversal(g) {
            @Override
            protected boolean visit(int v) {
                assertEquals(visitOrder.poll().intValue(), v);
                return true;
            }
        };
        traversal.traverse(v1);
    }

    @Test
    public void testDepthFirstTraversal() {
        Graph g = new DirectedGraph();
        assertEquals(0, g.edgeSize());
        assertEquals(0, g.vertexSize());

        int v1 = g.add();
        int v2 = g.add();
        int v3 = g.add();
        int v4 = g.add();
        int v5 = g.add();

        int e1 = g.add(v1, v2);
        int e2 = g.add(v1, v3);
        int e3 = g.add(v3, v2);
        int e4 = g.add(v2, v4);
        int e5 = g.add(v3, v5);

        Queue<Integer> visitOrder = new LinkedList<>(
                Arrays.asList(v1, v2, v4, v3, v5)
        );

        Traversal traversal = new DepthFirstTraversal(g) {
            @Override
            protected boolean visit(int v) {
                assertEquals(visitOrder.poll().intValue(), v);
                return true;
            }
        };
        traversal.traverse(v1);
    }
}
