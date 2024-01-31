package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * This class stores details for a position for the Frog's pathfinding.
 * Stores the f value and the position.
 *
 * @author Barnaby Morley-Smith
 */
public class PathFindDetails {
    /**
     * The f value for this position.
     */
    private final double val;

    /**
     * The position these details are associated with.
     */
    private final Point2D pos;

    /**
     * Default constructor for PathFindDetails.
     * @param value The f value in the A* algorithm.
     * @param position The position these details are associated with.
     */
    PathFindDetails(double value, Point2D position) {
        val = value;
        pos = position;
    }

    /**
     * Get the f value for this position.
     * @return The f value.
     */
    public double getVal() {
        return val;
    }

    /**
     * Get the associated position.
     * @return The position these details are associated with.
     */
    public Point2D getPos() {
        return pos;
    }
}
