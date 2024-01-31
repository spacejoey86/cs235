package swan.g09.cs230a2;

import javafx.geometry.Point2D;

/**
 * A Trap, if set, will prevent whatever walks on it from moving.
 *
 * @author Samuel Lomas
 * */
public class Trap extends ActionTile {

    /**
     * Handles whether the trap is set.
     * */
    private boolean set;

    /**
     * Stores an actor if it is trapped.
     */
    private Actor trappedActor;

    /**
     * Counts the number of actors currently pressing a button to disable the trap.
     * */
    private int pressCounter;

    /**
     * Default constructor for class Trap. Also creates linked Button.
     * @param position The position of the trap
     */
    public Trap(Point2D position) {
        super(TileType.TRAP, "sprites/Trap_Set.png", position);
        set = true;
        pressCounter = 0;
    }

    /**
     * Handles when an actor walks on the Trap.
     * @param a The Actor
     * */
    @Override
    public void walkedOn(Actor a) {
        if (set) {
            a.setTrapped(true);
            trappedActor = a;
        }
    }

    /**
     * Lets an actor calling it know if the trap is set.
     * @return true if trap is set
     * */
    public boolean getTrapped() {
        return set;
    }

    /**
     * Sets the trap.
     * */
    public void enable() {
        if (pressCounter > 0) {
           pressCounter--;
        }
        if (pressCounter == 0) {
            set = true;
            updateImagePath("sprites/Trap_Set.png");
        }
    }

    /**
     * Disarms the trap.
     * */
    public void disable() {
        pressCounter++;
        if (pressCounter > 0) {
            set = false;
            updateImagePath("sprites/Trap_Disarmed.png");
            // Free actor if trapped
            if (trappedActor != null) {
                trappedActor.setTrapped(false);
            }
            trappedActor = null;
        }
    }
}
