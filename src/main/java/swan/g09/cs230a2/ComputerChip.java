package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A computer chip can be picked up by the player and used to unlock doors.
 *
 * @author Samuel Lomas
 * */
public class ComputerChip extends Item {

    /**
     * Default class for Computer Chip.
     * @param position The position of the chip.
     * */
    ComputerChip(Point2D position) {
        super(TileType.CHIP, "sprites/Chip.png", position);
    }
}
