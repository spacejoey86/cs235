package swan.g09.cs230a2;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Layer class handles an individual layer of Tile subclasses.
 *
 * @param <T> The type of Tile to fill the class
 * @author Nation Gurung, Samuel Lomas
 * @version 0.2
 */
public class Layer<T> {

    /**
     * Hashmap used to store tiles.
     */
    private final Map<Point2D, T> grid;

    /**
     * The level handling this layer.
     */
    private final Level level;


    /**
     * Default constructor for Layer.
     *
     * @param l the level handling the layer
     */
    public Layer(Level l) {
        grid = new HashMap<Point2D, T>();
        level = l;
    }

    /**
     * Set a position p to a tile.
     *
     * @param p       a Point2D representing position
     * @param element the Tile to insert
     */
    public void setAtPosition(Point2D p, T element) {
        grid.put(p, element);
    }

    /**
     * Gets the tile at a given position, null if none.
     *
     * @param p the position to search at
     * @return A tile if exists.
     */
    public T getAtPosition(Point2D p) {
        return grid.get(p);
    }

    /**
     * Removes a tile from position p.
     *
     * @param p the position to remove the tile from
     */
    public void removeFromPosition(Point2D p) {
        grid.remove(p);
    }

    /**
     * Get a list of all the coordinates that a specific tile type is found at.
     *
     * @param type The type to search for.
     * @return The list of coordinates where matching elements were found.
     */
    public ArrayList<Point2D> findPositionsOf(TileType type) {
        ArrayList<Point2D> list = new ArrayList<>();
        for (Point2D coordinate : grid.keySet()) {
            Tile elem = (Tile) grid.get(coordinate);
            if (elem.getType() == type) {
                list.add(coordinate);
            }
        }
        return list;
    }

    /**
     * Gets all elements as an ArrayList.
     * @return An ArrayList of all the elements.
     */
    public ArrayList<T> getAllElements() {
        return new ArrayList<T>(grid.values());
    }

    /**
     * Converts a tile back to a character.
     *
     * @param t the tile
     * @return a character representing the tile
     */
    private char tileToChar(T t) {
        if (t == null) {
            return  '-';
        } else if (t instanceof Actor) {
            Tile tile = (Actor) t;
            return switch (tile.getType()) {
                case PLAYER -> '*';
                case BLOCK -> '#';
                case PINK_BALL -> '@';
                case BUG -> ((Bug) t).toChar();
                case FROG -> '^';
                default -> '-';
            };
        } else if (t instanceof Item) {
            Tile tile = (Item) t;
            switch (tile.getType()) {
                case CHIP:
                    return 'c';
                case KEY:
                    Key k = (Key) t;
                    return k.toChar();
                default:
                    return '-';
            }
        } else {
            Tile tile = (Tile) t;
            return switch (tile.getType()) {
                case PATH -> 'P';
                case DIRT -> 'D';
                case WALL -> 'W';
                case EXIT -> 'E';
                case BUTTON -> 'C';
                case TRAP -> 'T';
                case WATER -> 'O';
                case CHIP_SOCKET -> 'S';
                case LOCKED_DOOR -> {
                    LockedDoor ld = (LockedDoor) t;
                    yield ld.toChar();
                }
                case ICE -> {
                    Ice ic = (Ice) t;
                    yield ic.toChar();
                }
                case BOAT_PATH -> {
                    BoatPath bp = (BoatPath) t;
                    yield bp.toChar();
                }
                default -> '-';
            };
        }
    }

    /**
     * Converts the layer into a string.
     *
     * @return the layer as a string
     */
    public String toString() {
        String outstr = "";

        for (int y = 0; y < level.getHeight(); y++) {

            for (int x = 0; x < level.getWidth(); x++) {
                Point2D pos = new Point2D(x, y);
                outstr += tileToChar(grid.get(pos));
            }
            outstr += "\n";
        }
        return outstr;
    }
}
