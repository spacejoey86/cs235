package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * The {@code ChipSocket} is a tile that acts like the {@code LockedDoor}
 * but deducts a currency of chips from the player's inventory.
 *
 * @author Samuel Lomas
 * */
public class ChipSocket extends Tile {

    /**
     * The chips needed to get rid of the chipsocket.
     * */
    private int requiredChips;

    /**
     * Default constructor for chipsocket.
     * @param position The position of the socket.
     * */
    ChipSocket(Point2D position) {
        super(TileType.CHIP_SOCKET, "sprites/Chip_Socket.png", position);
    }

    /**
     * Sets the required chip count for the socket.
     * @param chips The chips needed to unlock it.
     * */
    public void setRequiredChips(int chips) {
        requiredChips = chips;
    }

    /**
     * Returns the chips needed.
     * @return the chips needed.
     * */
    public int getRequiredChips() {
        return requiredChips;
    }

    /**
     * Deducts an amount of chips from the chip count.
     * If less than 1, turn into a path and return true so the player may move.
     * @param chips The number of chips to deduct.
     * @return true if turned into a path.
     * */
    public boolean deductChips(int chips) {
        requiredChips -= chips;
        if (requiredChips < 1) {
            GameManager.setPath(getPosition());
            return true;
        }
        return false;
    }
}
