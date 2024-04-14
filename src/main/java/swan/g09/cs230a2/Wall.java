package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Wall is a tile that no actors may walk over.
 * @author Barnaby Morley-Smith
 * @version 0.1
 * */
public class Wall extends Tile {

    /**
     * Default Constructor for wall.
     *
     * @param position the position of the wall
     * */
    public Wall(Point2D position) {
        super(TileType.WALL, "sprites/Wall.png", position);
    }
}
