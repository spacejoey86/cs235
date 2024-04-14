package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A boat that moves back and forth, and the player can walk on and off it.
 * @author Dylan
 */
public final class BoatPath extends ActionTile {
    /**
     * The direction the boat should travel after this tile.
     */
    private final Direction pushDirection;

    /**
     * Whether a boat is currently on this tile.
     */
    private Boolean boatHere;

    /**
     * Keeps track on whether this tile has already been moved this tick to maintain update order.
     */
    private Boolean movedThisTick = false;

    /**
     * If the boat's path is not a loop, this keeps track of which direction it is currently going.
     */
    private Boolean reverse = false;

    /**
     * How long since the boat has last moved, so it can move every MOVE_INTERVAL ticks.
     */
    private int ticksSinceMove = 0;

    /**
     * The interval between each time the boat moves.
     */
    private static final int MOVE_INTERVAL = 7;

    /**
     * Constructor for a BoatPath.
     * @param newPosition The coordinate this BoatPath is located at
     * @param pathPushDirection The direction the boat should take from this path
     * @param isBoatHere Whether this tile should currently have a boat on it
     */
    public BoatPath(Point2D newPosition, Direction pathPushDirection, Boolean isBoatHere) {
        super(TileType.BOAT_PATH, "sprites/Water.png", newPosition);
        this.pushDirection = pathPushDirection;
        this.boatHere = isBoatHere;
        if (this.boatHere) {
            updateImagePath("sprites/Boat.png");
        }
    }

    /**
     * Handles the water being walked on.
     * Becomes a path if block, ends game if player.
     * @param a The actor walking over the tile.
     * */
    @Override
    public void walkedOn(Actor a) {
        if (a.getType() == TileType.BLOCK && !this.boatHere) {
            GameManager.removeActor(getPosition());
            GameManager.setPath(getPosition());
        } else if (a.getType() == TileType.PLAYER && !this.boatHere) {
            GameManager.removeActor(getPosition());
            GameManager.endGame(GameManager.DeathState.DROWN);
        }
    }

    /**
     * Method to move a boat onto this tile.
     * This is where the sprite is updated
     * Should be called before moveBoatAway in tick so any rider doesn't drown
     * @param isReversing whether the boat is currently reversing (for non-loop paths)
     */
    public void moveBoatTo(Boolean isReversing) {
        this.boatHere = true;
        this.movedThisTick = true;
        this.reverse = isReversing;

        updateImagePath("sprites/Boat.png");
    }

    /**
     * Getter for whether a boat is on this tile.
     * @return whether there is a boat on this tile
     */
    public Boolean getBoatPresence() {
        return this.boatHere;
    }

    /**
     * Getter for whether the boat is currently reversing.
     * @return whether this boat is reversing
     */
    public Boolean getReversing() {
        return this.reverse;
    }

    /**
     * Set the boat to reversing along the path.
     */
    public void setReverse() {
        this.reverse = true;
    }

    /**
     * Method to update a tile when a boat moves away from it.
     * Should be called after moveBoatTo in tick so any rider doesn't drown
     */
    private void moveBoatAway() {
        this.boatHere = false;
        updateImagePath("sprites/Water.png");

        Actor rider = GameManager.checkActor(this.getPosition());
        if (rider != null) {
            if (this.reverse) {
                rider.move(this.pushDirection.flipDirection());
            } else {
                rider.move(this.pushDirection);
            }
        }
    }

    @Override
    public void tick() {
        if (this.boatHere) {
            Point2D nextPosition = this.pushDirection.calculateNewPosition(this.getPosition());
            if (this.reverse) {
                nextPosition = this.pushDirection.flipDirection().calculateNewPosition(this.getPosition());
            }
            Tile nextTile = GameManager.checkTile(nextPosition);

            if (!(nextTile instanceof BoatPath)) {
                this.reverse = !this.reverse;
            }

            if (this.reverse) {
                nextPosition = this.pushDirection.flipDirection().calculateNewPosition(this.getPosition());
                nextTile = GameManager.checkTile(nextPosition);
            }

            if (ticksSinceMove > BoatPath.MOVE_INTERVAL && !this.movedThisTick
                && nextTile instanceof BoatPath nextBoatTile) {
                this.ticksSinceMove = 0;
                nextBoatTile.moveBoatTo(this.reverse);
                this.moveBoatAway();
            } else {
                this.movedThisTick = false;
                this.ticksSinceMove += 1;
            }
        }
    }

    /**
     * Get the save file representation of the current tile.
     * @return the character representing the tile in its current state
     */
    public char toChar() {
        return switch (this.pushDirection) {
            case NORTH -> 'A';
            case EAST -> 'F';
            case SOUTH -> 'H';
            case WEST -> 'M';
        };
    }
}
