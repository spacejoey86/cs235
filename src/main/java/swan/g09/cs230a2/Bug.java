package swan.g09.cs230a2;
import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The Bug class represents a type of monster that navigates
 * a game grid based on specific rules.
 * It adheres to movement constraints, follows an edge,
 * and avoids obstacles like other monsters or blocks.
 *
 * @author Haliat Sanusi
 * @author Barnaby Morley-Smith
 */
public class Bug extends Actor {
    /**
     * How many ticks should pass between each time the bug moves.
     */
    private static final int MOVE_INTERVAL = 8;

    /**
     * The list of tiles that the bug can walk on.
     */
    private static final ArrayList<TileType> WALKABLE =
            new ArrayList<>(List.of(TileType.PATH, TileType.BUTTON, TileType.TRAP));

    /**
     * Indicates whether the Bug should follow the left edge
     * of tiles when navigating.
     * If true, the Bug follows the left edge; if false,
     * it follows the right edge.
     */
    private boolean followLeftEdge;

    /**
     * Stores how many times the bug has been ticked.
     */
    private int currentTick = 0;

    /**
     * Constructor to initialize a Bug instance.
     *
     * @param position       The initial position of the Bug.
     * @param leftEdge       Boolean indicating whether the Bug
     *                       follows the left edge initially.
     */
    public Bug(Point2D position, boolean leftEdge) {
        super(TileType.BUG, "sprites/Bug.png", WALKABLE, position);
        this.followLeftEdge = leftEdge;
    }


    /**
     * Handles the tick operation for the Bug,
     * determining the next movement direction.
     * Moves the Bug in the determined direction or
     * handles alternative actions if unable to move.
     */
    @Override
    protected void tick() {
        currentTick++;

        if (currentTick % MOVE_INTERVAL != 0) {
            return;
        }

        Direction nextDirection = determineNextDirection();

        if (nextDirection != null && checkMove(nextDirection)) {
            move(nextDirection);
            setFacingDir(nextDirection);
        }
    }

    /**
     * Checks if the Bug can move in a specific direction based
     * on the grid layout and obstacles.
     *
     * @param dir The direction to check for movement.
     * @return True if the Bug can move in the specified direction;
     * otherwise, false.
     */
    @Override
    protected boolean checkMove(Direction dir) {
        Point2D newPosition = calculateNewPosition(dir);
        GameManager.checkTile(newPosition);

        TileType newTileType = checkPosition(newPosition);
        if (!canWalkOverTile(newTileType)) {
            return false;
        }

        if (isTileOccupiedByActor(newPosition)) {
            Actor collidedActor = GameManager.checkActor(newPosition);
            if (collidedActor instanceof Player) {
                if (!((Player) collidedActor).isInvincible()) {
                    GameManager.endGame(GameManager.DeathState.BUG_KILL);
                    GameManager.removeActor(newPosition);
                }  // Player is invincible, no action taken
            }
            return false;
        }
        return true;
    }

    /**
     * Determines the next direction for the Bug to move based
     * on edge-following rules and obstacles.
     *
     * @return The next valid direction for the Bug to move,
     * or null if no available path.
     */
    private Direction determineNextDirection() {
        if (isTrapped()) {
            return null;
        }

        Direction dir = getFacingDir();

        Point2D bugPos = getPosition();

        Point2D frontOfBug;
        Point2D sideOfBug;
        Point2D otherSideOfBug;
        Point2D backSideOfBug;

        int sideOffset = (followLeftEdge ? -1 : 1);

        switch (dir) {
            case NORTH -> {
                frontOfBug = new Point2D(bugPos.getX(), bugPos.getY() - 1);
                sideOfBug = new Point2D(bugPos.getX() + sideOffset, bugPos.getY());
                otherSideOfBug = new Point2D(bugPos.getX() - sideOffset, bugPos.getY());
                backSideOfBug = new Point2D(bugPos.getX() + sideOffset, bugPos.getY() + 1);
            }
            case EAST -> {
                frontOfBug = new Point2D(bugPos.getX() + 1, bugPos.getY());
                sideOfBug = new Point2D(bugPos.getX(), bugPos.getY() + sideOffset);
                otherSideOfBug = new Point2D(bugPos.getX(), bugPos.getY() - sideOffset);
                backSideOfBug = new Point2D(bugPos.getX() - 1, bugPos.getY() + sideOffset);
            }
            case SOUTH -> {
                frontOfBug = new Point2D(bugPos.getX(), bugPos.getY() + 1);
                sideOfBug = new Point2D(bugPos.getX() - sideOffset, bugPos.getY());
                otherSideOfBug = new Point2D(bugPos.getX() + sideOffset, bugPos.getY());
                backSideOfBug = new Point2D(bugPos.getX() - sideOffset, bugPos.getY() - 1);
            }
            case WEST -> {
                frontOfBug = new Point2D(bugPos.getX() - 1, bugPos.getY());
                sideOfBug = new Point2D(bugPos.getX(), bugPos.getY() - sideOffset);
                otherSideOfBug = new Point2D(bugPos.getX(), bugPos.getY() + sideOffset);
                backSideOfBug = new Point2D(bugPos.getX() + 1, bugPos.getY() - sideOffset);
            }
            default -> {
                frontOfBug = null;
                sideOfBug = null;
                otherSideOfBug = null;
                backSideOfBug = null;
            }
        }

        if (positionTraversable(sideOfBug, true) && !positionTraversable(backSideOfBug, true)) {
            return turnDirection(dir, followLeftEdge);
        }

        if (!positionTraversable(frontOfBug, true)
                && positionTraversable(otherSideOfBug, true)) {
            return turnDirection(dir, !followLeftEdge);
        }

        if (positionTraversable(frontOfBug, true)) {
            return dir;
        }

        // Turning around is the only option
        return turnDirection(turnDirection(dir, followLeftEdge), followLeftEdge);
    }

    /**
     * Turns a Direction left or right.
     * @param dir The original direction.
     * @param turnLeft Whether to turn left, false means turn right.
     * @return The turned direction.
     */
    private Direction turnDirection(Direction dir, boolean turnLeft) {
        switch (dir) {
            case NORTH -> {
                return (turnLeft ? Direction.WEST : Direction.EAST);
            }
            case EAST -> {
                return (turnLeft ? Direction.NORTH : Direction.SOUTH);
            }
            case SOUTH -> {
                return (turnLeft ? Direction.EAST : Direction.WEST);
            }
            case WEST -> {
                return (turnLeft ? Direction.SOUTH : Direction.NORTH);
            }
            default -> {
                return dir;
            }
        }
    }

    /**
     * Gets the current status of edge-following behavior for the Bug.
     *
     * @return True if the Bug follows the left edge; otherwise, false.
     */
    public boolean isFollowLeftEdge() {
        return followLeftEdge;
    }

    /**
     * Sets the edge-following behavior for the Bug.
     *
     * @param newfollowLeftEdge Boolean indicating whether the
     *                       Bug should follow the left edge.
     */
    public void setFollowLeftEdge(final boolean newfollowLeftEdge) {
        followLeftEdge = newfollowLeftEdge;
    }

    /**
     * Gets the correct character for serialising the Bug.
     * @return The character to store the bug as.
     */
    public Character toChar() {
        return (followLeftEdge ? '$' : '%');
    }
}
