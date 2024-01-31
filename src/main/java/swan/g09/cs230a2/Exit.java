package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * The {@code Exit} class is a subclass of {@code ActionTile} representing an exit tile in the game.
 * It can handle being walked over by an actor.
 * @author Abdulrahman Almutairi
 */
public class Exit extends ActionTile {

    /**
     * Constructs an exit tile with the specified type, image URL, and position.
     *
     *  @param position The position of the exit tile.
     */
    public Exit(Point2D position) {
        super(TileType.EXIT, "sprites/Exit.png", position);
    }

    /**
     * Handles the event of an actor walking on the exit tile.
     * Checks if the actor is a player and prints a message if true.
     *
     * @param a The actor that has stepped on the exit tile.
     */
    @Override
    public void walkedOn(Actor a) {
        // Check if the actor is a player
        if (a.getType() == TileType.PLAYER) {
            GameManager.winLevel();
        }
    }
}
