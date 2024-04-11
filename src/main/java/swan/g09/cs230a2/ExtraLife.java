package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * An ExtraLife item can be picked up by the player and gain an extra life.
 *
 * @version 0.1
 * @author Juned
 * */
public class ExtraLife extends Item {

    /**
     * Default class for ExtraLife.
     * @param position The position of the ExtraLife item.
     * */
    ExtraLife(Point2D position) {
        super(TileType.EXTRA, "sprites/ExtraLife.png", position);
    }
}

