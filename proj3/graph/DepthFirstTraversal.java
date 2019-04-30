package graph;

/* See restrictions in Graph.java. */

import java.util.LinkedList;

/**
 * Implements a depth-first traversal of a graph.  Generally, the
 * client will extend this class, overriding the visit and
 * postVisit methods, as desired (by default, they do nothing).
 *
 * @author Chris Chi
 */
public class DepthFirstTraversal extends Traversal {

    /**
     * A depth-first Traversal of G.
     */
    protected DepthFirstTraversal(Graph G) {
        super(G, new LinkedList<>());
    }

    @Override
    protected boolean visit(int v) {
        return super.visit(v);
    }

    @Override
    protected boolean postVisit(int v) {
        return super.postVisit(v);
    }

    @Override
    protected boolean processSuccessor(int u, int v) {
        traverse(v);
        return false;
    }
}
