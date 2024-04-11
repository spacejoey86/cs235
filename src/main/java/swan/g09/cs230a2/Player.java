package swan.g09.cs230a2;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Player is an actor which is controlled by the user and
 * interacts with other tiles and actors in the level.
 *
 * @author Barnaby Morley-Smith, Samuel Lomas
 * @version 0.1
 */
public class Player extends Actor {

    /**
     * The player moves every x ticks.
     * */
    private static int MOVE_INTERVAL = 4;

    /**
     * The player's speed powerup status.
     */
    private static boolean hasSpeedPowerup = false;

    /**
     * The duration of the Invincible powerup.
     */
    private int invincibleDuration = 0;

    /**
     * The player's Invincible status.
     */
    private boolean isInvincible = false;



    /**
     * The duration of the speed powerup.
     */
    private int speedPowerupDuration = 0;

    /**
     * The original movement interval for the player.
     */
    private static final int ORIGINAL_MOVE_INTERVAL = 4;

    /**
     * The number of extra lives the player has.
     * */
    static int extraLives = 0;

    /**
     * The slots that different items take up in the inventory.
     */
    public enum InventorySlot {
        /** The inventory slot of the Chip item. */
        CHIP,
        /** The inventory slot of the red Key item. */
        RED_KEY,
        /** The inventory slot of the green Key item. */
        GREEN_KEY,
        /** The inventory slot of the yellow Key item. */
        YELLOW_KEY,
        /** The inventory slot of the blue Key item. */
        BLUE_KEY,
        /** The inventory slot of the extra life item. */
        EXTRA_LIFE;


        /**
         * Convert an inventory slot to a string.
         * @return The string representing this slot.
         */
        @Override
        public String toString() {
            return Character.toString("crgyb".charAt(this.ordinal()));
        }

        /**
         * Convert an inventory slot from a string.
         * @param in The string to parse.
         * @return The inventory slot represented by the provided string.
         */
        public static InventorySlot fromString(String in) {
            return switch (in) {
                case "c" -> CHIP;
                case "r" -> RED_KEY;
                case "g" -> GREEN_KEY;
                case "y" -> YELLOW_KEY;
                case "b" -> BLUE_KEY;
                default -> null;
            };
        }

    }

    /**
     * Tiles that the player can move over.
     * */
    private static final ArrayList<TileType> WALKABLE_TILES =
            new ArrayList<>(List.of(TileType.PATH, TileType.BUTTON,
                    TileType.LOCKED_DOOR, TileType.CHIP_SOCKET,
                    TileType.DIRT, TileType.EXIT, TileType.TRAP,
                    TileType.WATER, TileType.ICE, TileType.SPEED, TileType.INVINC, TileType.EXTRA, TileType.INCREASETIME));

    /**
     * The current tick.
     * */
    private int currentTick = 0;

    /**
     * The previous tick.
     * */
    private int lastMoveTick;

    /**
     * Stores the player's inventory.
     * This stores the amount of each item in the order [Chips, Key_R, Key_G, Key_Y, Key_B].
     * */
    private final int[] inventory = new int[]{0, 0, 0, 0, 0};

    /**
     * Default Constructor for Player.
     *
     * @param position the position of the player
     * */
    public Player(Point2D position) {
        super(TileType.PLAYER, "sprites/Player.png", WALKABLE_TILES, position);

        MOVE_INTERVAL = ORIGINAL_MOVE_INTERVAL;
        // Add tick handlers for arrow keys
        InputManager.addTickHandler(KeyCode.LEFT, () -> handleMovement(Direction.WEST));
        InputManager.addTickHandler(KeyCode.RIGHT, () -> handleMovement(Direction.EAST));
        InputManager.addTickHandler(KeyCode.UP, () -> handleMovement(Direction.NORTH));
        InputManager.addTickHandler(KeyCode.DOWN, () -> handleMovement(Direction.SOUTH));

        // Add tick handlers for WASD keys
        InputManager.addTickHandler(KeyCode.A, () -> handleMovement(Direction.WEST));
        InputManager.addTickHandler(KeyCode.D, () -> handleMovement(Direction.EAST));
        InputManager.addTickHandler(KeyCode.W, () -> handleMovement(Direction.NORTH));
        InputManager.addTickHandler(KeyCode.S, () -> handleMovement(Direction.SOUTH));

        // Set lastMoveTick to allow moving on the first tick
        lastMoveTick = -ORIGINAL_MOVE_INTERVAL;
    }

    private void resetMovementInterval() {
        MOVE_INTERVAL = ORIGINAL_MOVE_INTERVAL;
    }

    /**
     * Checks movement, and moves the player in a direction on keydown.
     * @param dir the direction of movement.
     * */
    private void handleMovement(Direction dir) {
        if (GameManager.isLevelRunning() && currentTick - MOVE_INTERVAL >= lastMoveTick && !isTrapped()) {
            // Use the modified checkMove method for speed power-up
            if (checkMove(dir)) {
                lastMoveTick = currentTick;
                move(dir);
                setFacingDir(dir);
            }
        }

        if (hasSpeedPowerup) {
            speedPowerupDuration--;
            if (speedPowerupDuration <= 0) {
                resetMovementInterval();
                hasSpeedPowerup = false;
            }
        }

        if (isInvincible) {
            invincibleDuration--;
            if (invincibleDuration <= 0) {
                isInvincible = false;
            }
        }
    }

    /**
     * Allows the timer to tick the player class.
     */
    @Override
    protected void tick() {
        currentTick++;
    }

    /**
     * Checks if the player can move in any direction.
     * @param dir The direction of movement
     * @return If movement is allowed.
     */
    @Override
    protected boolean checkMove(Direction dir) {
        Point2D nextPos = calculateNewPosition(dir);
        Tile nextTile = GameManager.checkTile(nextPos);

        if (nextTile == null || !canWalkOverTile(nextTile.getType())) {
            return false;
        }

        // If the player can walk over the tile, carry out any actions needed
        switch (nextTile.getType()) {
            case LOCKED_DOOR -> {
                LockedDoor door = (LockedDoor) nextTile;
                if (!door.testLock(inventory)) {
                    return false;
                }
            }
            case CHIP_SOCKET -> {
                ChipSocket socket = (ChipSocket) nextTile;
                int oldChips = inventory[InventorySlot.CHIP.ordinal()];
                inventory[InventorySlot.CHIP.ordinal()] = oldChips - socket.getRequiredChips();
                if (inventory[InventorySlot.CHIP.ordinal()] < 1) {
                    inventory[InventorySlot.CHIP.ordinal()] = 0;
                }
                if (!socket.deductChips(oldChips)) {
                    return false;
                }
            }
            case ICE -> {
                Ice iceTile = (Ice) nextTile;
                if (!iceTile.canBeMovedOntoFrom(dir)) { // Don't allow moving onto ice from its "wall" side
                    return false;
                }
            }
            default -> {
            }
        }

        // If the tile has an actor on it
        if (isTileOccupiedByActor(nextPos)) {
            Actor nextActor = GameManager.checkActor(nextPos);

            // Check if the player is invincible, and if so, skip death scenarios (killing the other actor)
            if (nextActor instanceof Player && isInvincible) {
                return true;
            }

            switch (nextActor.getType()) {
                case FROG:
                    if (!(nextActor instanceof Player) && !isInvincible) {
                        GameManager.endGame(GameManager.DeathState.FROG_KILL);
                        GameManager.removeActor(getPosition());
                        return false;
                    }
                    break;
                case BUG:
                    if (!(nextActor instanceof Player) && !isInvincible) {
                        GameManager.endGame(GameManager.DeathState.BUG_KILL);
                        GameManager.removeActor(getPosition());
                        return false;
                    }
                    break;
                case PINK_BALL:
                    if (!(nextActor instanceof Player) && !isInvincible) {
                        GameManager.endGame(GameManager.DeathState.BOUNCED);
                        GameManager.removeActor(nextPos);  // Remove the PINK_BALL instead of the player
                        return false;
                    }
                    break;
                case BLOCK:
                    if (!nextActor.checkMove(dir)) {
                        return false;
                    }
                    nextActor.setFacingDir(dir);
                    nextActor.move(dir);
                    break;
                default:
                    return false;
            }
        }

        //Check if the tile has an item on it
        Item nextItem = GameManager.checkItem(nextPos);
        if (nextItem != null) {
            switch (nextItem.getType()) {
                case CHIP -> {
                    inventory[0]++;
                    GameManager.removeItem(nextPos);
                }
                case KEY -> {
                    Key key = (Key) nextItem;
                    switch (key.getColour()) {
                        case 'R' -> inventory[InventorySlot.RED_KEY.ordinal()]++;
                        case 'G' -> inventory[InventorySlot.GREEN_KEY.ordinal()]++;
                        case 'Y' -> inventory[InventorySlot.YELLOW_KEY.ordinal()]++;
                        case 'B' -> inventory[InventorySlot.BLUE_KEY.ordinal()]++;
                    }
                    GameManager.removeItem(nextPos);
                }
                case SPEED -> {
                    if (!hasSpeedPowerup) {
                        hasSpeedPowerup = true;
                        speedPowerupDuration = 300;
                        MOVE_INTERVAL = 2;
                    }
                    GameManager.removeItem(nextPos); // Remove the speed powerup from the level
                }
                case INVINC -> {
                    isInvincible = true;
                    invincibleDuration = 300;
                    GameManager.removeItem(nextPos); // Remove the invincibility from the level
                }
                case EXTRA -> {
                    if (extraLives < 1) {
                        // Increment extra lives
                        extraLives++;
                    } else {
                        inventory[InventorySlot.EXTRA_LIFE.ordinal()]++;
                    }
                    GameManager.removeItem(nextPos); // Remove the extra life from the level
                    return true;
                }
                case INCREASETIME -> {
                    int updatedTime = GameTimer.addTime(30);
                    if (updatedTime != -1) {
                    }
                    GameManager.removeItem(nextPos); // Remove the increase time from the level
                }
            }
            return true;
        }
        return true;
    }

    /**
     * Get the player's inventory.
     * @return The player's inventory.
     */
    public int[] getInventory() {
        return inventory;
    }

    /**
     * Set the player's inventory.
     * @param inv The player's inventory.
     */
    public void setInventory(int[] inv) {
        if (inv.length != inventory.length) {
            throw new RuntimeException("Inventory length didn't match expected: "
                    + inv.length + " != " + inventory.length);
        }
        System.arraycopy(inv, 0, inventory, 0, inventory.length);
    }

    public boolean isInvincible() {
        return isInvincible;
    }
}
