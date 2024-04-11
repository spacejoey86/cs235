package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * An Invincible item can be picked up by the player and become Invincible.
 *
 * @author Juned
 * */
public class Invinc extends Item {

    /**
     * Default class for Invincible.
     * @param position The position of the Invincible item.
     * */
    Invinc(Point2D position) {
        super(TileType.INVINC, "sprites/Invincible.png", position);
    }
}

