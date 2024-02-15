package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * The key can be picked up by the player to unlock a door.
 * @author Samuel Lomas, Abdulrahman Almutairi
 * */
public class Key extends Item {

    /**
     * The colour of the key.
     * */
    private final KeyColour colour;

    /**
     * The colour of a key
     */
    public enum KeyColour {
        /**
         * Red key
         */
        RED,

        /**
         * Green key
         */
        GREEN,

        /**
         * Yellow key
         */
        YELLOW,

        /**
         * Blue key
         */
        BLUE;

        /**
         * the path of the key type
         * @return the key
         */
        private String path;

        static {
            RED.path = "sprites/Key_Red.png";
            GREEN.path = "sprites/Key_Green.png";
            YELLOW.path = "sprites/Key_Yellow.png";
            BLUE.path = "sprites/Key_Blue.png";
        }

        /**
         * the export string of the key type
         * @return the export string
         */
        private char export;

        static {
            RED.export = 'R';
            GREEN.export = 'G';
            YELLOW.export = 'Y';
            BLUE.export = 'B';
        }
    }

    /**
     * Default constructor for class Key.
     * @param pos the position of the door.
     * @param col the colour of the door.
     * */
    Key(Point2D pos, KeyColour col) {
        super(TileType.KEY, col.path, pos);
        colour = col;
    }

    /**
     * Gets the colour of the key.
     * @return char for colour
     * */
    public KeyColour getColour() {
        return colour;
    }

    /**
     * Gets the character of the key for saving.
     * @return char for saving
     * */
    public char toChar() {
        return colour.export;
    }
}
