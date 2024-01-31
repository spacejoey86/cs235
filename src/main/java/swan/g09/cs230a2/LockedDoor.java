package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * Class LockedDoor is a door that can be opened with a key.
 * @author Samuel Lomas, Abdulrahman Almutairi
 * */
public class LockedDoor extends Tile {

    /**
     * The colour of the Locked door.
     * */
    private final char colour;

    /**
     * Default constructor for class LockedDoor.
     * @param pos the position of the door.
     * @param col the colour of the door.
     * */
    LockedDoor(Point2D pos, char col) {
        super(TileType.LOCKED_DOOR, "sprites/LockedDoor_Red.png", pos);
        switch (col) {
            default:
            case 'R':
                updateImagePath("sprites/LockedDoor_Red.png");
                break;
            case 'G':
                updateImagePath("sprites/LockedDoor_Green.png");
                break;
            case 'Y':
                updateImagePath("sprites/LockedDoor_Yellow.png");
                break;
            case 'B':
                updateImagePath("sprites/LockedDoor_Blue.png");
                break;
        }
        colour = col;
    }

    /**
     * Tests whether a player can go through a locked door.
     * @param inventory the player's inventory.
     * @return A boolean to confirm
     * */
    public boolean testLock(int[] inventory) {
        boolean redKey = inventory[Player.InventorySlot.RED_KEY.ordinal()] > 0 && colour == 'R';
        boolean greenKey = inventory[Player.InventorySlot.GREEN_KEY.ordinal()] > 0 && colour == 'G';
        boolean yellowKey = inventory[Player.InventorySlot.YELLOW_KEY.ordinal()] > 0 && colour == 'Y';
        boolean blueKey = inventory[Player.InventorySlot.BLUE_KEY.ordinal()] > 0 && colour == 'B';
        if (redKey || greenKey || yellowKey || blueKey) {
            GameManager.setPath(getPosition());
            return true;
        }
        return false;
    }

    /**
     * Converts the lockedDoor into a char for saving.
     * @return char of the lockedDoor
     * */
    public char toChar() {
        return colour;
    }
}
