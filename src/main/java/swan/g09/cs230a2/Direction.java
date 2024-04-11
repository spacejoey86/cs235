package swan.g09.cs230a2;

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
     * Parse a string into a direction.
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
