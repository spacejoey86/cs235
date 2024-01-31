package swan.g09.cs230a2;

/**
 * Represents a player profile that holds information
 * about a player's name and unlocked levels.
 * Manages player profiles including creation, deletion,
 * and retrieval of player information.
 *
 * @author Haliat Sanusi
 */


public class PlayerProfile {
    /**
     * The name of the player.
     */
    private final String playerName;
    /**
     * The maximum level unlocked by the player.
     */
    private int maxUnlockedLevel;

    /**
     * Constructs a PlayerProfile object with a specified player name.
     *
     * @param name The name of the player.
     */
    public PlayerProfile(final String name) {
        this.playerName = name;
        this.maxUnlockedLevel = 1; // Initially unlocked level is set to 1
    }

    /**
     * Alternate constructor for a playerprofile with a different starting level.
     * @param name The name of the player
     * @param level The level that the player has unlocked
     * */
    public PlayerProfile(String name, int level) {
        this.playerName = name;
        this.maxUnlockedLevel = level;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Retrieves the maximum unlocked level for the player.
     *
     * @return The maximum unlocked level for the player.
     */
    public int getMaxUnlockedLevel() {
        return maxUnlockedLevel;
    }

    /**
     * Sets the maximum unlocked level for the player.
     *
     * @param level The maximum unlocked level
     * to be set for the player.
     */
    public void setMaxUnlockedLevel(final int level) {
        this.maxUnlockedLevel = level;
    }

    /**
     * Adds 1 to the max unlocked level.
     * */
    public void addMaxUnlockedLevel() {
        maxUnlockedLevel++;
    }

    /**
     * Converts player profile to a string.
     * @return a string representing the player profile
     * */
    public String toString() {
        return playerName + "," + maxUnlockedLevel;
    }
}
