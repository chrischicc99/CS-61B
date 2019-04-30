package graph;

import java.util.Objects;

/**
 * A vertex has distance information w/ _gScore and _hScore.
 *
 * @author Chris Chi
 */
class DistanceVertex extends Vertex implements Comparable<DistanceVertex> {
    /**
     * The distance between current vertex and _source.
     */
    private double _gScore;
    /**
     * The estimated distance between current vertex and _dest.
     */
    private double _hScore;
    /**
     * The vertex's id before this vertex.
     */
    private int _from;

    /**
     * Construct a new Distance Vertex.
     * @param vertexId the vertex id.
     * @param gScore the gScore.
     * @param hScore the hScore.
     */
    DistanceVertex(int vertexId, double gScore, double hScore) {
        super(vertexId);
        this._gScore = gScore;
        this._hScore = hScore;
        this._from = 0;
    }

    /**
     * @return current gScore;
     */
    double getGScore() {
        return _gScore;
    }

    /**
     * @param gScore the new gScore;
     */
    void setGScore(double gScore) {
        this._gScore = gScore;
    }

    /**
     * @param hScore the new estimated score;
     */
    void setHScore(double hScore) {
        this._hScore = hScore;
    }

    @Override
    public int compareTo(DistanceVertex o) {
        return Double.compare(this._gScore + this._hScore,
                o._gScore + o._hScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceVertex that = (DistanceVertex) o;
        return _vertexId == that._vertexId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_vertexId);
    }

    /**
     * @return return vertex's id before this vertex.
     */
    public int getFrom() {
        return _from;
    }

    /**
     * @param from set _from id.
     */
    public void setFrom(int from) {
        this._from = from;
    }
}
