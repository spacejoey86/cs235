package swan.g09.cs230a2;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The PinkBall class represents a type of monster in the game.
 * Pink balls can only travel over path, button, and trap tiles
 * and only if these tiles do not contain another monster or
 * block. Other tile types and tiles that contain another monster
 * or block are considered blocking tiles. A pink ball travels in a
 * straight line until it hits a blocking tile, where it reverses
 * direction and travels backwards. Each pink ball is initially set
 * in a particular direction when the level is loaded.
 * @author Haliat Sanusi
 * @author Barnaby Morley-Smith
 */
public class PinkBall extends Actor {
    /**
     * How many ticks should pass between each time the pink ball moves.
     */
    private final int MOVE_INTERVAL;

    /**
     * The list of tiles that the bug can walk on.
     */
    private static final ArrayList<TileType> WALKABLE =
            new ArrayList<>(List.of(TileType.PATH, TileType.BUTTON, TileType.TRAP));

    /**
     * Stores how many times the bug has been ticked.
     */
    private int currentTick = 0;

    /**
     * Constructor for the PinkBall class.
     * Initializes the PinkBall with specified parameters.
     *
     * @param position The initial position of the pink ball
     */
    public PinkBall(final Point2D position, int moveInterval) {
        super(TileType.PINK_BALL, "sprites/PinkBall.png", WALKABLE, position);
        this.MOVE_INTERVAL = moveInterval;
    }

    /**
     * This method defines the tick behavior for the PinkBall.
     * PinkBall is designed for extension, and if subclasses
     * override this method, they should ensure to call the
     * super.tick() to maintain the core functionality
     * of PinkBall's movement behavior.
     */
    @Override
    protected void tick() {
        currentTick++;

        if (currentTick % this.MOVE_INTERVAL != 0) {
            return;
        }

        boolean isMoveValid = checkMove(getFacingDir());
        if (!isMoveValid) {
            // If the current direction is not a valid move,
            // reverse the direction
            switch (getFacingDir()) {
                case NORTH:
                    setFacingDir(Direction.SOUTH);
                    break;
                case EAST:
                    setFacingDir(Direction.WEST);
                    break;
                case SOUTH:
                    setFacingDir(Direction.NORTH);
                    break;
                case WEST:
                    setFacingDir(Direction.EAST);
                    break;
                default:
                    break;
            }
        }
        if (checkMove(getFacingDir())) {
            move(getFacingDir());
        }
    }
    /**
     * Checks if moving in a specific direction is valid for the PinkBall.
     * Subclasses extending PinkBall may override this method to customize
     * movement behavior. When overridden, ensure to maintain logic that
     * verifies the validity of movement and calls to super.checkMove()
     * to retain core movement functionality.
     *
     * @param dir the direction of movement
     * @return true if the movement in the given direction is valid,
     * false otherwise
     */
    @Override
    protected boolean checkMove(final Direction dir) {
        Point2D newPosition = calculateNewPosition(dir);

        Actor potActor = GameManager.checkActor(newPosition);
        if (potActor != null && potActor.getType() == TileType.PLAYER) {
            GameManager.endGame(GameManager.DeathState.BOUNCED);
            GameManager.removeActor(newPosition);
            return false;
        }

        TileType newTileType = checkPosition(newPosition);
        return canWalkOverTile(newTileType) && !isTileOccupiedByActor(newPosition) && !isTrapped();
    }
}
