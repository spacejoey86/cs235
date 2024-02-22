package swan.g09.cs230a2;

import javafx.geometry.Point2D;


/**
 * Enum used for handling movement and facing direction.
 * @author Samuel Lomas
 * */
public enum Direction {
    /** The North movement direction. */
    NORTH,
    /** The East movement direction. */
    EAST,
    /** The South movement direction. */
    SOUTH,
    /** The West movement direction. */
    WEST;

    /**
     * Calculates the position after moving in a specific direction.
     *
     * @param position The current position.
     * @return The new position after moving in the specified direction.
     */
    public Point2D calculateNewPosition(Point2D position) {
        double newX = position.getX();
        double newY = position.getY();

        switch (this) {
            case NORTH -> newY--;
            case EAST -> newX++;
            case SOUTH -> newY++;
            case WEST -> newX--;
            default -> {
            }
        }

        return new Point2D(newX, newY);
    }
};
