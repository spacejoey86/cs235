package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Enum used for handling movement and facing direction.
 *
 * @author Samuel Lomas
 *
 */
public enum Direction {
    /**
     * The North movement direction.
     */
    NORTH,
    /**
     * The East movement direction.
     */
    EAST,
    /**
     * The South movement direction.
     */
    SOUTH,
    /**
     * The West movement direction.
     */
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
            case NORTH ->
                newY--;
            case EAST ->
                newX++;
            case SOUTH ->
                newY++;
            case WEST ->
                newX--;
            default -> {
            }
        }

        return new Point2D(newX, newY);
    }

    /**
     * Allow the actors facing direction to be flipped when it should be
     * bounced.
     *
     * @return The new direction.
     */
    public Direction flipDirection() {
        return switch (this) {
            case NORTH ->
                Direction.SOUTH;
            case EAST ->
                Direction.WEST;
            case SOUTH ->
                Direction.NORTH;
            case WEST ->
                Direction.EAST;
        };
    }

    /**
     * Parse a string into a direction.
     *
     * @param inputString A capital letter N, S, E or W
     * @return the corresponding direction
     */
    public static Direction parseString(String inputString) {
        switch (inputString) {
            case "N" -> {
                return Direction.NORTH;
            }
            case "E" -> {
                return Direction.EAST;
            }
            case "S" -> {

                return Direction.SOUTH;
            }
            case "W" -> {

                return Direction.WEST;
            }
            default -> {
                throw new IllegalArgumentException("Tried to parse invalid direction");
            }
        }
    }
};
