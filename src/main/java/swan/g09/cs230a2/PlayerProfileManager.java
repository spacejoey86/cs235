package swan.g09.cs230a2;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Manages a collection of player profiles including creation, deletion,
 * and retrieval of profiles.
 * This class consists of static methods and attributes only.
 * Prevents multiple instantiations.
 *
 * @author Haliat Sanusi, Samuel Lomas
 */
public final class PlayerProfileManager {
    /**
     * Map containing player profiles indexed by player names.
     * It stores PlayerProfile objects associated with their
     * respective player names.
     */
    private static final Map<String, PlayerProfile> PLAYER_PROFILES =
            new HashMap<>();

    /**
     * Stores the working directory for the player profiles.
     * */
    private static String appDataDirectory;

    /**
     * Handles whether the players data has been loaded.
     * */
    private static boolean loadedData = false;

    /**
     * Private constructor to prevent instantiation.
     * */
    private PlayerProfileManager() {

    }

    /**
     * Finds and sets up the path to the local appdata directory.
     *
     * @throws IOException where there is an error setting up the path
     * */
    public static void setupAppDataPath() throws IOException {
        String operatingSystem = System.getProperty("os.name").toUpperCase();

        //Default case, for Windows machines
        if (operatingSystem.contains("WIN")) {
            String appData = System.getenv("AppData");
            String local = File.separator + "CS230a2";
            Files.createDirectories(Paths.get(appData + local));
            appDataDirectory = appData + local + File.separator;

            //Secondary case, for linux/macOS
        } else {
            String userHome = System.getProperty("user.home");
            String configDir = File.separator + ".config" + File.separator + "CS230a2";
            Files.createDirectories(Paths.get(userHome, configDir));
            appDataDirectory = userHome + configDir + File.separator;
        }
    }

    /**
     * Loads profile data from local appdata directory.
     *
     * @throws IOException where there is an error handling profile data
     * */
    public static void loadProfileData() throws IOException {
        try {
            populateHashMap();
        } catch (FileNotFoundException e) {
            Files.createFile(Paths.get(appDataDirectory + "users.csv"));
        }
        loadedData = true;
    }

    /**
     * When closing the game, all player profiles are saved to the file.
     *
     * @throws IOException when file cannot be written to
     * */
    public static void savePlayerProfiles() throws IOException {
        Writer fileWriter = new FileWriter(appDataDirectory + "users.csv", false);
        StringBuilder toWrite = new StringBuilder();
        for (PlayerProfile p : PLAYER_PROFILES.values()) {
            toWrite.append(p.toString()).append("\n");
        }
        toWrite = new StringBuilder
                (toWrite.substring(0, Math.max(0, toWrite.length() - 1)));
        fileWriter.write(toWrite.toString());
        fileWriter.close();
    }

    /**
     * Populates the hashmap with player profiles loaded from file.
     * @throws FileNotFoundException If the file cannot be found.
     * */
    private static void populateHashMap() throws FileNotFoundException {
        Scanner csvReader = new Scanner(new File(appDataDirectory + "users.csv"));
        while (csvReader.hasNext()) {
            String[] line = csvReader.next().split(",");
            PlayerProfile player = new PlayerProfile(line[0], Integer.parseInt(line[1]));
            PLAYER_PROFILES.put(line[0], player);
        }
        csvReader.close();
    }

    /**
     * Creates a new player profile and adds it to the manager.
     *
     * @param playerName The name of the player for the new profile.
     * @throws IllegalArgumentException if profile name has a comma or already exists
     */
    public static void createPlayerProfile(final String playerName) throws IllegalArgumentException {
        if (!playerName.matches("^[a-zA-Z0-9_-]+$")) {
            showAlert
                    ("Profile name can only contain letters, numbers, hyphen and underscore!");
            throw new IllegalArgumentException
                    ("Profile name can only contain letters, numbers, hyphen and underscore!");
        }
        if (hasPlayerProfile(playerName)) {
            showAlert
                    ("Profile already exists!");
            throw new IllegalArgumentException
                    ("Profile already exists!");
        }
        PlayerProfile profile = new PlayerProfile(playerName);
        PLAYER_PROFILES.put(playerName, profile);
    }

    /**
     * Shows an alert with the given message.
     *
     * @param message The message to be displayed in the alert.
     */
    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Deletes a player profile based on the given player name.
     *
     * @param playerName The name of the player whose profile should be deleted.
     */
    public static void deletePlayerProfile(final String playerName) {
        PLAYER_PROFILES.remove(playerName);
    }

    /**
     * Retrieves the player profile associated with the given player name.
     *
     * @param playerName The name of the player for the profile to be retrieved.
     * @return The PlayerProfile object associated with the player name,
     * or null if not found.
     */
    public static PlayerProfile getPlayerProfile(final String playerName) {
        return PLAYER_PROFILES.get(playerName);
    }

    /**
     * Checks if there is a player profile associated with the given player name.
     *
     * @param playerName The name of the player to be checked for.
     * @return Whether a PlayerProfile could be found for the given name.
     */
    public static boolean hasPlayerProfile(final String playerName) {
        return PLAYER_PROFILES.containsKey(playerName);
    }

    /**
     * Retrieves all player profiles.
     *
     * @return The List of PlayerProfile objects.
     */
    public static List<PlayerProfile> getAllPlayerProfiles() {
        return PLAYER_PROFILES.values().stream().toList();
    }

    /**
     * Gets the appdata directory.
     * @return the appdata directory
     * */
    public static String getAppDataDirectory() {
        return appDataDirectory;
    }

    /**
     * Gets whether the profile data has been loaded.
     * @return true if profile data loaded
     * */
    public static boolean isLoadedData() {
        return loadedData;
    }
}
