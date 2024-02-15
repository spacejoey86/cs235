package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * The Tile class represents a single tile in a grid-based system, such as a game map.
 * Each tile has properties like its type, position, and walkability.
 *
 * @author Abdulrahman Almutairi
 * @version 0.2
 */
public abstract class Tile {
    // Tile properties

    /**
     * The Type of Tile.
     * */
    private final TileType tileType;

    /**
     * The location of the sprite for this tile.
     * */
    private String imagePath;

    /**
     * The Position of the Tile.
     * */
    private Point2D position;

    /**
     * Default constructor for creating a Tile object with default values.
     * @param type The type of tile
     * @param path The path of the tile image
     * @param tilePosition the position of the tile
     */
   Tile(TileType type, String path, Point2D tilePosition) {
        // Default constructor
        this.tileType = type;
        this.imagePath = path;
        this.position = tilePosition;
    }

    /**
     * Change the image path being used by this Tile.
     * @param path The new path to the image.
     */
    protected void updateImagePath(String path) {
       imagePath = path;
    }

    /**
     * Sets the position of the tile.
     *
     * @param tilePosition A Point2D containing the position
     */
    public void setPosition(Point2D tilePosition) {
        this.position = tilePosition;
    }

    /**
     * Returns the position of the tile.
     *
     * @return the position of the tile.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Gets the type of the tile.
     *
     * @return The type of the tile (e.g., Path, Wall).
     */
    public TileType getType() {
        return tileType;
    }

    /**
     * Gets the image URL for the tile.
     * @return The Image URL as a string
     * */
    public String getImagePath() {
        return imagePath;
    }
}
