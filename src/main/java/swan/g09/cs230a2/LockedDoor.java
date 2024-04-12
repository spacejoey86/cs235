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
            case 'R' -> updateImagePath("sprites/LockedDoor_Red.png");
            case 'G' -> updateImagePath("sprites/LockedDoor_Green.png");
            case 'Y' -> updateImagePath("sprites/LockedDoor_Yellow.png");
            case 'B' -> updateImagePath("sprites/LockedDoor_Blue.png");
            default -> throw new IllegalArgumentException("invalid door save character");
        }
        colour = col;
    }

    /**
     * Tests whether a player can go through a locked door.
     * @param inventory the player's inventory.
     * @return A boolean to confirm
     * */
    public boolean testLock(int[] inventory) {
        boolean unlocked = false;
        if (colour == 'R' && inventory[Player.InventorySlot.RED_KEY.ordinal()] > 0) {
            inventory[Player.InventorySlot.RED_KEY.ordinal()]--; // Use a red key
            unlocked = true;
        } else if (colour == 'G' && inventory[Player.InventorySlot.GREEN_KEY.ordinal()] > 0) {
            inventory[Player.InventorySlot.GREEN_KEY.ordinal()]--; // Use a green key
            unlocked = true;
        } else if (colour == 'Y' && inventory[Player.InventorySlot.YELLOW_KEY.ordinal()] > 0) {
            inventory[Player.InventorySlot.YELLOW_KEY.ordinal()]--; // Use a yellow key
            unlocked = true;
        } else if (colour == 'B' && inventory[Player.InventorySlot.BLUE_KEY.ordinal()] > 0) {
            inventory[Player.InventorySlot.BLUE_KEY.ordinal()]--; // Use a blue key
            unlocked = true;
        }

        if (unlocked) {
            GameManager.setPath(getPosition()); // Open the door
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
