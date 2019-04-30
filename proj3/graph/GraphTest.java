package graph;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for the Graph class.
 *
 * @author Chris Chi
 */
public class GraphTest {

    @Test
    public void emptyGraph() {
        DirectedGraph g = new DirectedGraph();
        assertEquals("Initial graph has vertices", 0, g.vertexSize());
        assertEquals("Initial graph has edges", 0, g.edgeSize());
    }

    @Test
    public void testDirectedGraph() {
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

        assertEquals(e1, g.edgeId(v1, v2));
        assertEquals(e2, g.edgeId(v1, v3));
        assertEquals(e3, g.edgeId(v3, v2));
        assertEquals(e4, g.edgeId(v2, v4));

        assertEquals(4, g.vertexSize());
        assertEquals(4, g.edgeSize());

        assertTrue(g.contains(v1));
        assertTrue(g.contains(v2));
        assertTrue(g.contains(v3));
        assertTrue(g.contains(v4));

        assertTrue(g.contains(v1, v2));
        assertTrue(g.contains(v1, v3));
        assertTrue(g.contains(v3, v2));
        assertTrue(g.contains(v2, v4));

        for (Integer successor : g.successors(v1)) {
            assertTrue(Arrays.asList(v2, v3).contains(successor));
        }

        for (Integer successor : g.successors(v4)) {
            fail();
        }

        for (Integer predecessor : g.predecessors(v2)) {
            assertTrue(Arrays.asList(v1, v3).contains(predecessor));
        }

        for (Integer predecessor : g.predecessors(v1)) {
            fail();
        }
    }

    @Test
    public void testUndirectedGraph() {
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

        assertEquals(e1, g.edgeId(v1, v2));
        assertEquals(e1, g.edgeId(v2, v1));
        assertEquals(e2, g.edgeId(v1, v3));
        assertEquals(e2, g.edgeId(v3, v1));
        assertEquals(e3, g.edgeId(v3, v2));
        assertEquals(e3, g.edgeId(v2, v3));
        assertEquals(e4, g.edgeId(v2, v4));
        assertEquals(e4, g.edgeId(v4, v2));

        assertEquals(4, g.vertexSize());
        assertEquals(4, g.edgeSize());

        assertTrue(g.contains(v1));
        assertTrue(g.contains(v2));
        assertTrue(g.contains(v3));
        assertTrue(g.contains(v4));

        assertTrue(g.contains(v1, v2));
        assertTrue(g.contains(v2, v1));
        assertTrue(g.contains(v1, v3));
        assertTrue(g.contains(v3, v1));
        assertTrue(g.contains(v3, v2));
        assertTrue(g.contains(v2, v3));
        assertTrue(g.contains(v2, v4));
        assertTrue(g.contains(v4, v2));

        for (Integer successor : g.successors(v1)) {
            assertTrue(Arrays.asList(v2, v3).contains(successor));
        }

        for (Integer successor : g.successors(v4)) {
            assertEquals(v2, successor.intValue());
        }

        for (Integer predecessor : g.predecessors(v2)) {
            assertTrue(Arrays.asList(v1, v3, v4).contains(predecessor));
        }

        for (Integer predecessor : g.predecessors(v1)) {
            assertTrue(Arrays.asList(v2, v3).contains(predecessor));
        }
    }

}
