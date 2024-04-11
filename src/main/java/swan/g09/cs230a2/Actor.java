package swan.g09.cs230a2;

import javafx.geometry.Point2D;

import java.util.ArrayList;

/**
 * The Actor Class is an abstract class that defines the behaviours for each of the on-screen actors.
 * @author Samuel Lomas
 * @version 0.2
 * */
abstract class Actor extends Tile {
    /**
     * Arraylist storing the types of tile that this actor may walk over.
     * */
    private final ArrayList<TileType> walkableTiles;

    /**
     * Which direction the Actor is facing.
     */
    private Direction facing = Direction.NORTH;

    /**
     * Declares whether the actor is trapped or not.
     * */
    private boolean trapped;

    /**
     * Default constructor for class Actor.
     * @param type The type of actor to be used
     * @param url the relative URL to the image of the actor
     * @param walkable An arraylist of tiles the actor can walk over
     * @param position The position of the actor
     * */
    Actor(TileType type, String url, ArrayList<TileType> walkable, Point2D position) {
        super(type, url, position);
        this.walkableTiles = walkable;
        trapped = false;
    }

    /**
     * This Method should handle how the actor processes its movements per tick.
     * */
    protected abstract void tick();

    /**
     * This Method should check if moving in a specific direction is valid.
     * @param dir the direction of movement
     * @return A boolean confirming if moving in that direction is possible
     * */
    protected abstract boolean checkMove(Direction dir);

    /**
     * This method moves the actor one tile in the requested direction.
     * @param dir the direction to move in
     * */
    protected void move(Direction dir) {
        Point2D destPos = directionToPosition(getPosition(), dir);
        if (destPos != null) {
            GameManager.moveActor(getPosition(), destPos);
        }
    }

    /**
     * Get the position the Actor will be in if it moves in a given direction from a given position.
     * @param pos The position the Actor want to check from.
     * @param dir The direction the Actor wants to move in.
     * @return The position the Actor will be in if it moves.
     */
    protected Point2D directionToPosition(Point2D pos, Direction dir) {
        return switch (dir) {
            case NORTH -> new Point2D(pos.getX(), pos.getY() - 1);
            case EAST -> new Point2D(pos.getX() + 1, pos.getY());
            case SOUTH -> new Point2D(pos.getX(), pos.getY() + 1);
            case WEST -> new Point2D(pos.getX() - 1, pos.getY());
            default -> null;
        };
    }

    /**
     * Checks if the Actor can walk over a specific tile type.
     * @param tileType The type of tile to check.
     * @return True if the Actor can walk over the tile; otherwise, false.
     */
    protected boolean canWalkOverTile(final TileType tileType) {
        return walkableTiles.contains(tileType);
    }

    /**
     * This method calls the layer class for the tile at a given position.
     * @param position an int[] [x,y] representing the coordinates
     * @return The type of tile at that position, null if none
     * */
    protected TileType checkPosition(Point2D position) {
        Tile tile = GameManager.checkTile(position);
        if (tile == null) {
            return null;
        }

        return tile.getType();
    }

    /**
     * Check if a tile at a certain position is able to be moved to.
     * @param pos Position to move to.
     * @param isMonster If the actor is a monster, consider players to not be an obstacle.
     * @return True if the actor can move to the position.
     */
    protected boolean positionTraversable(Point2D pos, boolean isMonster) {
        TileType newTileType = checkPosition(pos);
        if (!canWalkOverTile(newTileType)) {
            return false;
        }

        if (isTileOccupiedByActor(pos)) {
            if (isMonster && GameManager.checkActor(pos) instanceof Player) {
                return !((Player) GameManager.checkActor(pos)).isInvincible();
            }
        }

        return true;
    }

    /**
     * Turns a direction into a char.
     * @param d The direction the actor is facing.
     * @return char representing direction.
     * */
    public static char directionToChar(Direction d) {
        return switch (d) {
            case NORTH -> 'N';
            case EAST -> 'E';
            case SOUTH -> 'S';
            case WEST -> 'W';
        };
    }

    /**
     * Checks if a tile at a specific position is
     * occupied by another actor.
     *
     * @param position The position to check for tile occupancy.
     * @return True if the tile is occupied by a monster or block;
     * otherwise, false.
     */
    protected boolean isTileOccupiedByActor(final Point2D position) {
        Actor actorAtPosition = GameManager.checkActor(position);
        return actorAtPosition != null;
    }

    /**
     * Calculates the new position after moving in a specific direction.
     *
     * @param dir The direction in which the Bug intends to move.
     * @return The new position after moving in the specified direction.
     */
    protected Point2D calculateNewPosition(final Direction dir) {
        double newX = getPosition().getX();
        double newY = getPosition().getY();

        switch (dir) {
            case NORTH:
                newY--;
                break;
            case EAST:
                newX++;
                break;
            case SOUTH:
                newY++;
                break;
            case WEST:
                newX--;
                break;
            default:
        }

        return new Point2D(newX, newY);
    }

    /**
     * Used by the Trap class to tell an actor if they're trapped.
     * @param isTrapped the state of being trapped.
     * */
    public void setTrapped(boolean isTrapped) {
        this.trapped = isTrapped;
    }

    /**
     * Returns true if the actor is trapped.
     * @return true if the actor is trapped.
     * */
    public boolean isTrapped() {
        return trapped;
    }

    /**
     * Changes the direction the actor is facing.
     * @param dir The new direction the actor is facing.
     * */
    public void setFacingDir(Direction dir) {
        facing = dir;
    }

    /**
     * Returns the direction the actor is facing.
     * @return The direction the actor is facing.
     * */
    public Direction getFacingDir() {
        return facing;
    }

    /**
     * Returns walkable tiles.
     * @return walkable tiles
     * */
    public ArrayList<TileType> getWalkableTiles() {
        return walkableTiles;
    }
}
