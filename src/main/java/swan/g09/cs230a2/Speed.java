package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A Speed item can be picked up by the player and improve speed.
 *
 * @author Juned
 * */
public class Speed extends Item {

    /**
     * Default class for Speed.
     * @param position The position of the speed item.
     * */
    Speed(Point2D position) {
        super(TileType.SPEED, "sprites/Speed.png", position);
    }
}
