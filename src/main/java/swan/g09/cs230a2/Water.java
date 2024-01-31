package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Water kills the player if walked on, and becomes a path if a block is pushed onto it.
 * @author Samuel Lomas
 * */
public class Water extends ActionTile {

    /**
     * Default constructor for class Water.
     * @param position the position of the tile.
     */
    Water(Point2D position) {
        super(TileType.WATER, "sprites/Water.png", position);
    }

    /**
     * Handles the water being walked on.
     * Becomes a path if block, ends game if player.
     * @param a The actor walking over the tile.
     * */
    @Override
    public void walkedOn(Actor a) {
        if (a.getType() == TileType.BLOCK) {
            GameManager.removeActor(getPosition());
            GameManager.setPath(getPosition());
        } else if (a.getType() == TileType.PLAYER) {
            GameManager.removeActor(getPosition());
            GameManager.endGame(GameManager.DeathState.DROWN);
        }
    }

}
