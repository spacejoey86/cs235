package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Path is tile that all actors may walk over.
 * @author Samuel Lomas
 * @version 0.1
 * */
public class Path extends Tile {

    /**
     * Default Constructor for Path.
     *
     * @param position the position of the path
     * */
    public Path(Point2D position) {
        super(TileType.PATH, "sprites/Path.png", position);
    }
}
