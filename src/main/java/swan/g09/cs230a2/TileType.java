package swan.g09.cs230a2;

/**
 * The TileType enum represents the possible types of tiles in a grid-based
 * system, such as a game map. Each enum constant corresponds to a specific type
 * of tile, like PATH, WALL, etc.
 *
 * @author Abdulrahman Almutairi
 * @version 0.2
 */
public enum TileType {
    /**
     * Represents a path or walkable area.
     */
    PATH,
    /**
     * Represents a dirt tile.
     */
    DIRT,
    /**
     * Represents a solid wall tile.
     */
    WALL,
    /**
     * Represents an exit tile.
     */
    EXIT,
    /**
     * Represents a button tile.
     */
    BUTTON,
    /**
     * Represents a trap tile.
     */
    TRAP,
    /**
     * Represents a water tile.
     */
    WATER,
    /**
     * Represents a chip socket tile.
     */
    CHIP_SOCKET,
    /**
     * Represents a locked door tile.
     */
    LOCKED_DOOR,
    /**
     * Represents an ice tile.
     */
    ICE,
    /**
     * Represents a key.
     */
    KEY,
    /**
     * Represents a chip.
     */
    CHIP,
    /**
     * Represents the player.
     */
    PLAYER,
    /**
     * Represents a block.
     */
    BLOCK,
    /**
     * Represents a pink ball.
     */
    PINK_BALL,
    /**
     * Represents a bug.
     */
    BUG,
    /**
     * Represents a frog.
     */
    FROG,
    /**
     * Represents a boat and its path.
     */
    BOAT_PATH,
    /**
     * Represents Speed.
     */
    SPEED,
    /**
     * Represents Invincible.
     */
    INVINC,
    /**
     * Represents Extra Life.
     */
    EXTRA,
    /**
     * Represents Increase Time.
     */
    INCREASETIME
}
