package swan.g09.cs230a2;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * The block class is an actor that only moves when pushed by the player or sliding over ice.
 *
 * @author Samuel Lomas
 * */
public class Block extends Actor {

    /**
     * An arraylist of tiles that the block can move over.
     * */
    private static final ArrayList<TileType> WALKABLE =
            new ArrayList<>(List.of(TileType.PATH, TileType.BUTTON,
                    TileType.TRAP, TileType.WATER, TileType.DIRT, TileType.ICE));

    /**
     * Default constructor for class Block.
     * @param position The position of the block.
     * */
    Block(Point2D position) {
        super(TileType.BLOCK, "sprites/Block.png", WALKABLE, position);
    }


    /**
     * ticks the block, does nothing.
     * */
    @Override
    protected void tick() {

    }

    /**
     * Checks if the block can move in a direction.
     * If yes, the block moves in that direction.
     *
     * @param dir The direction.
     * @return true if the player can move; otherwise, returns false.
     * */
    @Override
    protected boolean checkMove(Direction dir) {
        Point2D nextPos = calculateNewPosition(dir);
        Tile nextTile = GameManager.checkTile(nextPos);
        Actor nextActor = GameManager.checkActor(nextPos);

        if (nextActor instanceof Player) {
            GameManager.removeActor(nextActor.getPosition());
            GameManager.endGame(GameManager.DeathState.CRUSH);
            return true;
        }

        if (nextTile != null && canWalkOverTile(nextTile.getType())
                && !isTileOccupiedByActor(nextPos) && GameManager.isLevelRunning()) {
            return true;
        }
        return false;
    }
}
