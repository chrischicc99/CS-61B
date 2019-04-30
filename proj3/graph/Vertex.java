package graph;

/**
 * The vertex information containing _vertexId and marked flag.
 *
 * @author Chris Chi
 */
class Vertex {
    /**
     * The vertex id in Graph.
     */
    protected int _vertexId;
    /**
     * Whether it has been marked.
     */
    protected boolean marked;

    /**
     * The constructor, unmarked by default.
     *
     * @param vertexId _vertexId
     */
    Vertex(int vertexId) {
        this._vertexId = vertexId;
        this.marked = false;
    }

    /**
     * @return current Vertex's vertex id in Graph.
     */
    public int getVertexId() {
        return _vertexId;
    }

    /**
     * Make current vertex marked.
     */
    public void mark() {
        this.marked = true;
    }

    /**
     * @return whether it has been marked.
     */
    public boolean isMarked() {
        return marked;
    }
}
