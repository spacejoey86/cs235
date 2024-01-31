package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * The Item Class is an abstract class that defines the behaviours for each of the on-screen items.
 * @author Samuel Lomas
 * @version 0.2
 * */
abstract class Item extends Tile {

    /**
     * Default constructor for class ActionTile.
     *
     * @param type     The type of actor to be used
     * @param url      the relative URL to the image of the actor
     * @param position The position of the Item
     */
    Item(TileType type, String url, Point2D position) {
        super(type, url, position);
    }
}
