package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Ice is a tile that player may walk on.
 * Player may not give move input on Ice, but can move once landing on a non-ice block.
 * @author Caragh Waite
 * @version 0.2
 */
public class Ice extends ActionTile {
    /**
     * How long the player should be kept on an ice tile before being moved.
     */
    private static final int MOVE_INTERVAL = 2;

    /**
     * The type of ice block.
     */
    private final IceType iceType;

    /**
     * Sets the actor on top of the ice.
     */
    private Actor actorOnTop;

    /**
     * The number of times the ice has been ticked.
     */
    private int currentTick = 0;

    /**
     * The tick the actor walked onto this tile.
     */
    private int actorWalkedOnTick = 0;

    /**
     * Default constructor for class Ice.
     * The position of the Ice tile.
     * @param position the position of the ice block.
     * @param type the type of ice block.
     */
    public Ice(Point2D position, IceType type) {
        super(TileType.ICE, type.path, position);
        iceType = type;
    }

    /**
     * Enum used to determine the type of ice block the actor is moving over.
     */
    public enum IceType {
        /**
         * Normal ice tile.
         */
        NORMAL,

        /**
         * Bottom Left corner ice block.
         */
        BOTTOM_LEFT,

        /**
         * Bottom Right corner ice block.
         */
        BOTTOM_RIGHT,

        /**
         * Top Left corner ice block.
         */
        TOP_LEFT,

        /**
         * Top Right corner ice tile.
         */
        TOP_RIGHT;

        /**
         * the path of the ice type
         * @return the path
         */
        private String path;

        static {
            NORMAL.path = "sprites/Ice.png";
            BOTTOM_LEFT.path = "sprites/Ice_BottomLeft.png";
            BOTTOM_RIGHT.path = "sprites/Ice_BottomRight.png";
            TOP_LEFT.path = "sprites/Ice_TopLeft.png";
            TOP_RIGHT.path = "sprites/Ice_TopRight.png";
        }
    }

    /**
     * Moves the actor at each tick.
     * Changes the direction of the actors movements if a corner is encountered.
     */
    public void tick() {
        currentTick++;

        // If there is an actor on top of the ice, move it
        if (actorOnTop != null) {
            int adjustedMoveInterval = MOVE_INTERVAL;

            // If the actor moved east or right from another tile, wait another tick to account for update order.
            Direction actorDir = actorOnTop.getFacingDir();
            if (actorDir == Direction.EAST || actorDir == Direction.SOUTH) {
                adjustedMoveInterval++;
            }

            // Don't move the actor if it hasn't been long enough yet
            if (currentTick - actorWalkedOnTick < adjustedMoveInterval) {
                return;
            }

            // Determine direction based on the ice type and actor direction
            Direction newDirection = determineNextDirection(actorDir);

            // Only move if it's okay to do so
            if (actorOnTop.checkMove(newDirection)) {
                actorOnTop.setFacingDir(newDirection);
                actorOnTop.move(newDirection);
                return;
            }

            // Bounce them
            Direction bounceDirection = flipDirection(actorDir);
            // Only move if it's okay to do so
            if (actorOnTop.checkMove(bounceDirection)) {
                actorOnTop.setFacingDir(bounceDirection);
                actorOnTop.move(bounceDirection);
            }
        }
    }

    /**
     * Allow the actors to be turned when moving on an ice corner tile.
     * @param dir The original direction.
     * @return The new direction.
     */
    public Direction determineNextDirection(Direction dir) {
        return switch (iceType) {
            case NORMAL -> dir;
            case TOP_LEFT ->
                    switch (dir) {
                        case WEST -> Direction.SOUTH;
                        case NORTH -> Direction.EAST;
                        default -> dir;
                    };
            case TOP_RIGHT ->
                    switch (dir) {
                        case EAST -> Direction.SOUTH;
                        case NORTH -> Direction.WEST;
                        default -> dir;
                    };
            case BOTTOM_LEFT ->
                    switch (dir) {
                        case WEST -> Direction.NORTH;
                        case SOUTH -> Direction.EAST;
                        default -> dir;
                    };
            case BOTTOM_RIGHT ->
                    switch (dir) {
                        case EAST -> Direction.NORTH;
                        case SOUTH -> Direction.WEST;
                        default -> dir;
                    };
        };
    }

    /**
     * Allow the actors facing direction to be flipped when it should be bounced.
     * @param dir The original direction.
     * @return The new direction.
     */
    public Direction flipDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.SOUTH;
            case EAST -> Direction.WEST;
            case SOUTH -> Direction.NORTH;
            case WEST -> Direction.EAST;
        };
    }

    /**
     * Handle ice being walked on by player.
     * Traps the actor to prevent them from moving themselves.
     * @param a The actor that has stepped on the tile
     */
    @Override
    public void walkedOn(Actor a) {
        //Trap actor to prevent them from moving by themselves.
        if (a.getType() == TileType.PLAYER || a.getType() == TileType.BLOCK) {
            a.setTrapped(true);
            actorOnTop = a;
            actorWalkedOnTick = currentTick;
        }

    }

    /**
     * Un-set the actor's trapped state so that it can move again.
     * @param a The actor that has stepped off the tile.
     */
    @Override
    public void walkedOff(Actor a) {
        //Un-trap the actor so they can move again.
        a.setTrapped(false);

        //Clear actorOnTop if storing the actor that walked off the ice tile.
        if (actorOnTop == a) {
            actorOnTop = null;
        }
    }

    /**
     * Checks if this tile can be moved onto from a specified direction.
     * @param dir The direction the actor is trying to move in.
     * @return Whether this tile can be moved onto.
     */
    public boolean canBeMovedOntoFrom(Direction dir) {
        return switch (iceType) {
            case NORMAL -> true;
            case TOP_LEFT -> dir != Direction.EAST && dir != Direction.SOUTH;
            case TOP_RIGHT -> dir != Direction.WEST && dir != Direction.SOUTH;
            case BOTTOM_LEFT -> dir != Direction.EAST && dir != Direction.NORTH;
            case BOTTOM_RIGHT -> dir != Direction.WEST && dir != Direction.NORTH;
        };
    }

    /**
     * Converts the ice tile to a char for saving.
     * @return ice type as char
     * */
    public char toChar() {
        return switch (iceType) {
            case NORMAL -> 'I';
            case TOP_LEFT -> 'U';
            case TOP_RIGHT -> 'J';
            case BOTTOM_LEFT -> 'K';
            case BOTTOM_RIGHT -> 'L';
        };
    }
}
