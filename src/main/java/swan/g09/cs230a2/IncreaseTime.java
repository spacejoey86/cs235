package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * An IncreaseTime item can be picked up by the player and increase the time.
 *
 * @version 0.1
 * @author Juned
 * */
public class IncreaseTime extends Item {

    /**
     * Default class for IncreaseTime.
     * @param position The position of the IncreaseTime item.
     * */
    IncreaseTime(Point2D position) {
        super(TileType.INCREASETIME, "sprites/IncreaseTime.png", position);
    }
}

