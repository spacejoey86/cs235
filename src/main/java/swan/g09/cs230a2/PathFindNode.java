package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * This class acts as a node in the A* pathfinding algorithm used by the Frog class.
 * Stores the values used for calculating path cost as well as the parent node and position.
 *
 * @author Barnaby Morley-Smith
 */
public class PathFindNode {
    /**
     * The h (estimated cost) value of the node.
     */
    private double hVal;

    /**
     * The g (actual cost) value of the node.
     */
    private int gVal;

    /**
     * The parent node for this node (the previous in the path).
     */
    private PathFindNode parent;

    /**
     * The position this node is associated with.
     */
    private final Point2D pos;

    /**
     * Default constructor for PathFindNode.
     * Defaults hVal and gVal to 0.
     * @param position The position of this node.
     */
    PathFindNode(Point2D position) {
        pos = position;
        hVal = 0.0;
        gVal = 0;
    }

    /**
     * Get the associated position.
     * @return The position associated with this node.
     */
    public Point2D getPos() {
        return pos;
    }

    /**
     * Set the h value of this node.
     * @param h The new h value.
     */
    public void setH(double h) {
        hVal = h;
    }

    /**
     * Get the g value of this node.
     * @return The g value of this node (actual cost).
     */
    public int getG() {
        return gVal;
    }

    /**
     * Set the g value of this node.
     * @param g The new g value.
     */
    public void setG(int g) {
        gVal = g;
    }

    /**
     * Get the f value of this node.
     * @return The f value of this node (actual cost + estimated cost).
     */
    public double getF() {
        return gVal + hVal;
    }

    /**
     * Get the parent of this node.
     * @return This node's parent.
     */
    public PathFindNode getParent() {
        return parent;
    }

    /**
     * Set the parent of this node.
     * @param newParent The new parent node.
     */
    public void setParent(PathFindNode newParent) {
        parent = newParent;
    }
}
