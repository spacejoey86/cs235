package swan.g09.cs230a2;

import java.util.ArrayList;

import javafx.geometry.Point2D;

/**
 * Button Class that can be stepped on to disable traps.
 *
 * @author Samuel Lomas
 * @version 0.1
 * */
public class Button extends ActionTile {

    /**
     * Stores the trap that the button controls.
     * */
    private ArrayList<Trap> linkedTraps = new ArrayList<>();

    /**
     * Handles whether the trap is stepped on.
     * */
    private boolean isSteppedOn;

    /**
     * Default constructor for Button.
     *
     * @param position Point2D representing position of the button
     * */
    public Button(Point2D position) {
        super(TileType.BUTTON, "sprites/Button.png", position);
        isSteppedOn = false;
    }

    /**
     * Link this button class to an array of traps.
     *
     * @param traps an arraylist of traps
     * @throws IllegalStateException if traps are already set
     * */
    public void setLinkedTraps(ArrayList<Trap> traps) throws IllegalStateException {
        linkedTraps = traps;
    }

    /**
     * Handles whenever the button is stepped on.
     *
     * @param a the actor stepping on the trap
     * */
    @Override
    public void walkedOn(Actor a) {
        for (Trap linkedTrap : linkedTraps) {
            linkedTrap.disable();
        }
        isSteppedOn = true;
    }

    /**
     * Handles whenever the button is walked off of.
     *
     * @throws IllegalStateException if button not stepped on in first place
     * @param a The actor walking off the tile.
     * */
    @Override
    public void walkedOff(Actor a) {
        if (isSteppedOn) {
            for (Trap linkedTrap : linkedTraps) {
                linkedTrap.enable();
            }
            isSteppedOn = false;
        } else {
            throw new IllegalStateException("Button was not stepped on in the first place!");
        }
    }

    /**
     * Returns an arraylist of linked traps.
     * @return an arraylist of linked traps
     * */
    public ArrayList<Trap> getLinkedTraps() {
        return linkedTraps;
    }
}
