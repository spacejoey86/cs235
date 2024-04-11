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
    private final char colour;

    /**
     * Default constructor for class Key.
     * @param pos the position of the key.
     * @param col the colour of the key.
     * */
    Key(Point2D pos, char col) {
        super(TileType.KEY, "sprites/Key_Red.png", pos);
        switch (col) {
            default:
            case 'R':
                updateImagePath("sprites/Key_Red.png");
                break;
            case 'G':
                updateImagePath("sprites/Key_Green.png");
                break;
            case 'Y':
                updateImagePath("sprites/Key_Yellow.png");
                break;
            case 'B':
                updateImagePath("sprites/Key_Blue.png");
                break;
        }
        colour = col;
    }

    /**
     * Gets the colour of the key.
     * @return char for colour
     * */
    public char getColour() {
        return colour;
    }

    /**
     * Gets the character of the key for saving.
     * @return char for saving
     * */
    public char toChar() {
        return Character.toLowerCase(colour);
    }
}
