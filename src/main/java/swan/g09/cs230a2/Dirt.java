package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Dirt is a tile that becomes a path when walked on.
 *
 * @author Samuel Lomas
 * @version 0.1
 * */
public class Dirt extends ActionTile {

    /**
     * Default constructor for class Dirt.
     * @param position the position of the Dirt
     * */
    Dirt(Point2D position) {
        super(TileType.DIRT, "sprites/Dirt.png", position);
    }

    /**
     * Handle Dirt being walked on.
     * @param a the actor walking on dirt
     * */
    public void walkedOn(Actor a) {
        if (a.getType() == TileType.PLAYER) {
            GameManager.setPath(super.getPosition());
        }
    }
}
