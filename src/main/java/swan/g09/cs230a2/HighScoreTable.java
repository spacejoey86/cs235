package swan.g09.cs230a2;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Manages high score tables per level and player profiles.
 * Records player scores and displays top scores for each level.
 * This class consists of static methods and attributes only.
 * Prevents multiple instantiations.
 *
 * @author Haliat Sanusi
 */
public final class HighScoreTable {
    /**
     * Represents the maximum number of scores to keep in the high score table.
     */
    private static final int MAX_SCORES_TO_KEEP = 10;
    /**
     * Map containing high scores for different levels.
     * It stores a mapping between levels (keys)
     * and lists of players' scores (values).
     */
    private static final Map<Integer, List<PlayerData>> HIGH_SCORES =
            new HashMap<>();

    private HighScoreTable() {
        // Private constructor to prevent instantiation
    }
    /**
     * Adds a player's score to the high score table for a specific level.
     * Updates the maximum unlocked level for the player if necessary.
     *
     * @param level  The level for which the score is being added.
     * @param player The Player object containing the player's name and score.
     * @throws IOException If high scores could not be loaded.
     */
    public static void addScore(final int level, final PlayerData player) throws IOException {
        String playerName = player.getName();
        int playerScore = player.getScore();

        PlayerProfile playerProfile =
                PlayerProfileManager.getPlayerProfile(playerName);
        if (playerProfile != null && level
                > playerProfile.getMaxUnlockedLevel()) {
            playerProfile.setMaxUnlockedLevel(level);
        }

        HIGH_SCORES.putIfAbsent(level, loadHighScores(level));
        List<PlayerData> scores = HIGH_SCORES.get(level);

        if (scores.size() < MAX_SCORES_TO_KEEP
                || playerScore > scores.getLast().getScore()) {
            scores.add(player);
            scores.sort(Comparator.comparingInt(PlayerData::getScore).reversed());
            if (scores.size() > MAX_SCORES_TO_KEEP) {
                scores.remove(MAX_SCORES_TO_KEEP);
            }
        }
    }
    /**
     * Displays the high scores for a specific level.
     *
     * @param level The level for which high scores will be displayed.
     * @return A list containing the high scores for the specified level.
     */
    public static List<PlayerData> displayHighScores(final int level) {
        List<PlayerData> scoresCopy = new ArrayList<>();
        if (HIGH_SCORES.containsKey(level)) {
            List<PlayerData> scores = HIGH_SCORES.get(level);
            System.out.println("High Scores for Level " + level + ":");
            for (int i = 0; i < scores.size(); i++) {
                PlayerData player = scores.get(i);
                System.out.println((i + 1) + ". " + player.getName()
                        + " - " + player.getScore());
                scoresCopy.add(new PlayerData(player.getName(),
                        player.getScore()));
                // Adding a copy of Player to scoresCopy
            }
        } else {
            System.out.println("No high scores available for Level " + level);
        }
        return scoresCopy;
    }
    /**
     * Loads high scores for a specific level from a file.
     *
     * @param level The level for which high scores will be loaded.
     * @return A list containing the loaded high scores for the specified level.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static List<PlayerData> loadHighScores(int level) throws IOException {
        List<PlayerData> loadedScores = new ArrayList<>();
        String filePath = getAppDataDirectory() + "level_" + level + ".csv";

        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            while (fileScanner.hasNextLine()) {
                String[] data = fileScanner.nextLine().split(",");
                String playerName = data[0];
                int playerScore = Integer.parseInt(data[1]);
                loadedScores.add(new PlayerData(playerName, playerScore));
            }
        } catch (FileNotFoundException e) {
            // If file not found, return an empty list
        }

        return loadedScores;
    }
    /**
     * Saves high scores for a specific level to a file.
     *
     * @param level  The level for which high scores will be saved.
     * @param scores The list of high scores to be saved.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    public static void saveHighScores(final int level,
                                      final List<PlayerData> scores)
            throws IOException {
        String filePath = getAppDataDirectory() + "level_" + level + ".csv";

        try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(filePath))) {
            for (PlayerData player : scores) {
                writer.write(player.getName() + "," + player.getScore() + "\n");
            }
        }
    }
   /**
     * Gets the app data directory for high scores.
     *
     * @return The directory path where high score files are stored.
     * @throws IOException If the HighScores directory did not exist and couldn't be created.
     */
    private static String getAppDataDirectory() throws IOException {
        String appDataDirectory = PlayerProfileManager.getAppDataDirectory()
                + "HighScores" + File.separator;

        Files.createDirectories(Path.of(appDataDirectory));
        return appDataDirectory;
    }
}

