package swan.g09.cs230a2;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

/**
 * Player is an actor which is controlled by the user and interacts with other
 * tiles and actors in the level.
 *
 * @author Barnaby Morley-Smith, Samuel Lomas
 * @version 0.1
 */
public class Player extends Actor {

    /**
     * The original movement interval for the player.
     */
    private static final int ORIGINAL_MOVE_INTERVAL = 4;

    /**
     * The player moves every x ticks.
     *
     */
    private static int moveInterval = ORIGINAL_MOVE_INTERVAL;

    /**
     * The duration of the Invincible powerup remaining.
     */
    private int invincibleDurationRemaining = 0;

    /**
     * The duration of the speed powerup remaining.
     */
    private int speedPowerupDurationRemaining = 0;

    /**
     * The duration that timed powerups last.
     */
    private static final int POWERUP_DURATION = 300;

    /**
     * The extra time that an extra time powerup gives you.
     */
    private static final int EXTRA_TIME = 30;

    /**
     * The number of extra lives the player has.
     *
     */
    private static int extraLives = 0;

    /**
     * The slots that different items take up in the inventory.
     */
    public enum InventorySlot {
        /**
         * The inventory slot of the Chip item.
         */
        CHIP,
        /**
         * The inventory slot of the red Key item.
         */
        RED_KEY,
        /**
         * The inventory slot of the green Key item.
         */
        GREEN_KEY,
        /**
         * The inventory slot of the yellow Key item.
         */
        YELLOW_KEY,
        /**
         * The inventory slot of the blue Key item.
         */
        BLUE_KEY,
        /**
         * The inventory slot of the extra life item.
         */
        EXTRA_LIFE;

        /**
         * Convert an inventory slot to a string.
         *
         * @return The string representing this slot.
         */
        @Override
        public String toString() {
            return Character.toString("crgybe".charAt(this.ordinal()));
        }

        /**
         * Convert an inventory slot from a string.
         *
         * @param in The string to parse.
         * @return The inventory slot represented by the provided string.
         */
        public static InventorySlot fromString(String in) {
            return switch (in) {
                case "c" ->
                    CHIP;
                case "r" ->
                    RED_KEY;
                case "g" ->
                    GREEN_KEY;
                case "y" ->
                    YELLOW_KEY;
                case "b" ->
                    BLUE_KEY;
                case "e" ->
                    EXTRA_LIFE;
                default ->
                    throw new IllegalArgumentException("Invalid inventory slot character");
            };
        }

    }

    /**
     * Tiles that the player can move over.
     *
     */
    private static final ArrayList<TileType> WALKABLE_TILES
            = new ArrayList<>(List.of(
                    TileType.PATH, TileType.BUTTON,
                    TileType.LOCKED_DOOR, TileType.CHIP_SOCKET,
                    TileType.DIRT, TileType.EXIT, TileType.TRAP,
                    TileType.WATER, TileType.ICE, TileType.SPEED,
                    TileType.INVINC, TileType.EXTRA,
                    TileType.INCREASETIME,
                    TileType.BOAT_PATH
            ));

    /**
     * The current tick.
     *
     */
    private int currentTick = 0;

    /**
     * The previous tick.
     *
     */
    private int lastMoveTick;

    /**
     * Stores the player's inventory. This stores the amount of each item in the
     * order [Chips, Key_R, Key_G, Key_Y, Key_B, EXTRA_LIFE].
     *
     */
    private static final int[] INVENTORY = new int[]{0, 0, 0, 0, 0, 0};

    /**
     * Default Constructor for Player.
     *
     * @param position the position of the player
     *
     */
    public Player(Point2D position) {
        super(TileType.PLAYER, "sprites/Player.png", WALKABLE_TILES, position);

        moveInterval = ORIGINAL_MOVE_INTERVAL;
        // Add tick handlers for arrow keys
        InputManager.addTickHandler(KeyCode.LEFT, () -> handleMovement(Direction.WEST));
        InputManager.addTickHandler(KeyCode.RIGHT, () -> handleMovement(Direction.EAST));
        InputManager.addTickHandler(KeyCode.UP, () -> handleMovement(Direction.NORTH));
        InputManager.addTickHandler(KeyCode.DOWN, () -> handleMovement(Direction.SOUTH));

        // Add tick handlers for WASD keys
        InputManager.addTickHandler(KeyCode.A, () -> {
            handleMovement(Direction.WEST);
        });
        InputManager.addTickHandler(KeyCode.D, () -> {
            handleMovement(Direction.EAST);
        });
        InputManager.addTickHandler(KeyCode.W, () -> {
            handleMovement(Direction.NORTH);
        });
        InputManager.addTickHandler(KeyCode.S, () -> {
            handleMovement(Direction.SOUTH);
        });

        InputManager.addKeydownHandler(KeyCode.ESCAPE,
                ChipsChallengeApplication::openPauseMenu);

        // Set lastMoveTick to allow moving on the first tick
        lastMoveTick = -ORIGINAL_MOVE_INTERVAL;
    }

    private void resetMovementInterval() {
        moveInterval = ORIGINAL_MOVE_INTERVAL;
    }

    /**
     * Checks movement, and moves the player in a direction on keydown.
     *
     * @param dir the direction of movement.
     *
     */
    private void handleMovement(Direction dir) {
        if (GameManager.isLevelRunning() && currentTick - moveInterval >= lastMoveTick && !isTrapped()) {
            // Use the modified checkMove method for speed power-up
            if (checkMove(dir)) {
                lastMoveTick = currentTick;
                move(dir);
                setFacingDir(dir);
            }
        }
    }

    /**
     * Allows the timer to tick the player class.
     */
    @Override
    protected void tick() {
        currentTick++;

        if (speedPowerupDurationRemaining > 0) {
            speedPowerupDurationRemaining--;
            if (speedPowerupDurationRemaining <= 0) {
                resetMovementInterval();
            }
        }

        if (isInvincible()) {
            invincibleDurationRemaining--;
        }
    }

    /**
     * Checks if the player can move in any direction. If it can, this also
     * carries out any required actions.
     *
     * @param dir The direction of movement
     * @return If movement is allowed.
     */
    @Override
    protected boolean checkMove(Direction dir) {
        Point2D nextPos = dir.calculateNewPosition(this.getPosition());
        Tile nextTile = GameManager.checkTile(nextPos);

        if (nextTile == null || !canWalkOverTile(nextTile.getType())) {
            return false;
        }

        // If the player can walk over the tile, carry out any actions needed
        if (!checkSpecialTiles(nextTile, dir)) {
            return false;
        }

        // If the tile has an actor on it
        if (!collideActors(nextPos, dir)) {
            return false;
        }

        //Check if the tile has an item on it
        collectItems(nextPos);

        return true;
    }

    private boolean checkSpecialTiles(Tile nextTile, Direction moveDirection) {
        switch (nextTile.getType()) {
            case LOCKED_DOOR -> {
                LockedDoor door = (LockedDoor) nextTile;
                if (!door.testLock(INVENTORY)) {
                    return false;
                }
            }
            case CHIP_SOCKET -> {
                ChipSocket socket = (ChipSocket) nextTile;
                int oldChips = INVENTORY[InventorySlot.CHIP.ordinal()];
                INVENTORY[InventorySlot.CHIP.ordinal()] = oldChips - socket.getRequiredChips();
                if (INVENTORY[InventorySlot.CHIP.ordinal()] < 1) {
                    INVENTORY[InventorySlot.CHIP.ordinal()] = 0;
                }
                if (!socket.deductChips(oldChips)) {
                    return false;
                }
            }
            case ICE -> {
                Ice iceTile = (Ice) nextTile;
                if (!iceTile.canBeMovedOntoFrom(moveDirection)) { // Don't allow moving onto ice from its "wall" side
                    return false;
                }
            }
            default -> {
            }
        }
        return true;
    }

    /**
     * Deal with any actors when moving onto a tile.
     *
     * @param tilePosition the position the player is trying to move to.
     * @param moveDirection the direction the player is trying to move in.
     * @return true if the move was allowed.
     */
    private boolean collideActors(Point2D tilePosition, Direction moveDirection) {
        if (isTileOccupiedByActor(tilePosition)) {
            Actor nextActor = GameManager.checkActor(tilePosition);

            // Check if the player is invincible, and if so, skip death scenarios (killing the other actor)
            if (nextActor instanceof Player && isInvincible()) {
                return true;
            }

            switch (nextActor.getType()) {
                case FROG -> {
                    if (!(nextActor instanceof Player) && !isInvincible()) {
                        GameManager.endGame(GameManager.DeathState.FROG_KILL);
                        GameManager.removeActor(getPosition());
                        return false;
                    }
                }
                case BUG -> {
                    if (!(nextActor instanceof Player) && !isInvincible()) {
                        GameManager.endGame(GameManager.DeathState.BUG_KILL);
                        GameManager.removeActor(getPosition());
                        return false;
                    }
                }
                case PINK_BALL -> {
                    if (!(nextActor instanceof Player) && !isInvincible()) {
                        GameManager.endGame(GameManager.DeathState.BOUNCED);
                        GameManager.removeActor(tilePosition);  // Remove the PINK_BALL instead of the player
                        return false;
                    }
                }
                case BLOCK -> {
                    if (!nextActor.checkMove(moveDirection)) {
                        return false;
                    }
                    nextActor.setFacingDir(moveDirection);
                    nextActor.move(moveDirection);
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deal with any items when moving onto a tile.
     *
     * @param tilePosition the position of the tile to pick up items from
     */
    private void collectItems(Point2D tilePosition) {
        Item nextItem = GameManager.checkItem(tilePosition);
        if (nextItem != null) {
            switch (nextItem.getType()) {
                case CHIP -> {
                    INVENTORY[0]++;
                }
                case KEY -> {
                    Key key = (Key) nextItem;
                    switch (key.getColour()) {
                        case 'R' ->
                            INVENTORY[InventorySlot.RED_KEY.ordinal()]++;
                        case 'G' ->
                            INVENTORY[InventorySlot.GREEN_KEY.ordinal()]++;
                        case 'Y' ->
                            INVENTORY[InventorySlot.YELLOW_KEY.ordinal()]++;
                        case 'B' ->
                            INVENTORY[InventorySlot.BLUE_KEY.ordinal()]++;
                        default -> {
                            throw new UnsupportedOperationException("unknown key colour picked up by player");
                        }
                    }
                }
                case SPEED -> {
                    if (speedPowerupDurationRemaining == 0) {
                        speedPowerupDurationRemaining = POWERUP_DURATION;
                        moveInterval = 2;
                    }
                }
                case INVINC -> {
                    invincibleDurationRemaining = POWERUP_DURATION;
                }
                case EXTRA -> {
                    if (extraLives < 1) {
                        // Increment extra lives
                        extraLives++;
                    } else {
                        INVENTORY[InventorySlot.EXTRA_LIFE.ordinal()]++;
                    }
                }
                case INCREASETIME -> {
                    GameTimer.addTime(EXTRA_TIME);
                }
                default -> {
                    throw new UnsupportedOperationException("Unknown item picked up by player");
                }
            }
            GameManager.removeItem(tilePosition);
        }
    }

    /**
     * Get the player's inventory.
     *
     * @return The player's inventory.
     */
    public static int[] getInventory() {
        return INVENTORY;
    }

    /**
     * Set the player's inventory.
     *
     * @param inv The player's inventory.
     */
    public void setInventory(int[] inv) {
        if (inv.length != INVENTORY.length) {
            throw new RuntimeException("Inventory length didn't match expected: "
                    + inv.length + " != " + INVENTORY.length);
        }
        System.arraycopy(inv, 0, INVENTORY, 0, INVENTORY.length);
    }

    /**
     * check if the player is invincible.
     *
     * @return true if the player is invincible
     */
    public boolean isInvincible() {
        return invincibleDurationRemaining > 0;
    }

    /**
     * Get the duration of invincibility remaining.
     *
     * @return the duration of invincibility remaining (0 means none remaining)
     */
    public int getInvincibleRemaining() {
        return invincibleDurationRemaining;
    }

    /**
     * Get the number of extra lives the player has.
     *
     * @return the number of extra lives the player has
     */
    public static int getExtraLives() {
        return extraLives;
    }

    /**
     * Set the number of extra lives the player has.
     *
     * @param numLives The new number of lives the player should have
     */
    public static void setExtraLives(int numLives) throws IllegalArgumentException {
        if (numLives < 0) {
            throw new IllegalArgumentException("Extra lives cannot be negative");
        }
        extraLives = numLives;
    }

    /**
     * Get the time remaining of speed boost powerup.
     *
     * @return the time remaining (0 if there is no speed boost applied)
     */
    public int getSpeedBoostRemaining() {
        return this.speedPowerupDurationRemaining;
    }
}
