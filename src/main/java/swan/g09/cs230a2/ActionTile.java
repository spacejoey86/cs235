package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * ActionTile is an abstract subclass of Tile that is able to handle being walked over by an actor.
 *
 * @author Samuel Lomas
 * @version 0.1
 * */
abstract class ActionTile extends Tile {

    /**
     * Default constructor for class ActionTile.
     * @param type The type of actor to be used
     * @param url the relative URL to the image of the actor
     * @param position The position of the tile
     * */
    ActionTile(TileType type, String url, Point2D position) {
        super(type, url, position);
    }

    /**
     * For handling being walked on by an actor.
     * @param a The actor that has stepped on the tile
     * */
    public abstract void walkedOn(Actor a);

    /**
     * For handling being walked off by an actor.
     * @param a The actor that has stepped off the tile
     * */
    public void walkedOff(Actor a) {
        // Do nothing by default
    }

    /**
     * Handler that gets called every tick.
     */
    public void tick() {
        // do nothing by default
    }
}
