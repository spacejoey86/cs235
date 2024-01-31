package swan.g09.cs230a2;

/**
 * Represents a player with a name and score.
 * This class encapsulates information about a player, including
 * their name and score.
 * It provides methods to access and retrieve player details.
 * @author Haliat Sanusi
 */
public class PlayerData {
    /**
     * The name of the player.
     */
    private final String name;

    /**
     * The score acheived by the player.
     */
    private final int score;

    /**
     * Constructs a Player object with a name and score.
     *
     * @param playerName The name of the player.
     * @param playerScore The score achieved by the player.
     */
    public PlayerData(final String playerName, final int playerScore) {
        name = playerName; // 'this.name' refers to the field 'name'
        score = playerScore; // 'this.score' refers to the field 'score'
    }

    /**
     * Retrieves the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the score of the player.
     *
     * @return The score of the player.
     */
    public int getScore() {
        return score;
    }
}
