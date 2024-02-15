package swan.g09.cs230a2;


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
     * flip of the direction
     * @return the flipped direction
     */
    private Direction flip;

    static {
        NORTH.flip = SOUTH;
        SOUTH.flip = NORTH;
        EAST.flip = WEST;
        WEST.flip = EAST;
    }

    /**
     * gets the opposite direction of the current direction
     * @return the opposite direction
     */
    public Direction flip() {
        return flip;
    }
};
