package swan.g09.cs230a2;

import javafx.geometry.Point2D;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;


/**
 * The GameManager class is responsible for managing communications between tiles on each layer.
 *
 * @author Samuel Lomas
 * @version 0.1
 * */
public class GameManager {

    /**
     * Enum for handling the different ways the player can die.
     * */
    public enum DeathState {
        /** Player has run out of time. */
        TIME_OUT,
        /** Player has fallen into water. */
        DROWN,
        /** Player been killed by the frog. */
        FROG_KILL,
        /** Player has been killed by the bug.*/
        BUG_KILL,
        /** Player has been crushed by the block. */
        CRUSH,
        /** Player has been bounced on by the pink ball. */
        BOUNCED,
        /** Player has died from an extra life. */
        EXTRA
    }

    /**
     * The width of the loaded level.
     */
    private static int levelWidth;

    /**
     * The height of the loaded level.
     */
    private static int levelHeight;

    /**
     * The Layer class containing all Tiles.
     * */
    private static Layer<Tile> tileLayer;

    /**
     * The Layer class containing all Items.
     * */
    private static Layer<Item> itemLayer;

    /**
     * The Layer class containing all Actors.
     * */
    private static Layer<Actor> actorLayer;

    /**
     * The PlayerProfile of the current player.
     */
    private static PlayerProfile playerProfile;

    /**
     * The timer managing the game.
     */
    private static GameTimer gameTimer;

    /**
     * The file name of the level, minus file type.
     * */
    private static String levelName;

    /**
     * The view controller for the game view.
     */
    private static GameViewController gameViewController;

    /**
     * The currently loaded level.
     */
    private static Level level;

    /**
     * Is this the last level.
     * */
    private static Boolean isLastLevel;

    /**
     * The number of the level in the load order.
     * */
    private static Integer levelNumber;

    /**
     * Default Constructor for GameManager.
     * */
    public GameManager() {

    }

    /**
     * Checks for a tile at a position.
     * @param position the position to be checked
     * @return A tile if found, null if none
     * @throws IllegalStateException if level not yet started
     * */
    public static Tile checkTile(Point2D position) {
        if (tileLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        return tileLayer.getAtPosition(position);
    }

    /**
     * Checks for an actor at a position.
     * @param position the position to be checked
     * @return An actor if found, null if none
     * @throws IllegalStateException if level not yet started
     * */
    public static Actor checkActor(Point2D position) {
        if (actorLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        return actorLayer.getAtPosition(position);
    }

    /**
     * Checks for an item at a position.
     * @param position the position to be checked
     * @return An item if found, null if none
     * @throws IllegalStateException if level not yet started
     * */
    public static Item checkItem(Point2D position) {
        if (itemLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        return itemLayer.getAtPosition(position);
    }

    /**
     * Sets a Tile to a path at a position.
     *
     * @param position Position2D of the tile
     * */
    public static void setPath(Point2D position) {
        replaceTile(position, new Path(position));
    }

    /**
     * Replaces a tile at a position with another tile.
     * @param position The position of the tile.
     * @param tile the tile to be created.
     * @throws IllegalStateException If level not yet loaded
     * */
    public static void replaceTile(Point2D position, Tile tile) {
        if (tileLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        tileLayer.setAtPosition(position, tile);
    }

    /**
     * Move an actor to another position.
     * @param from The position of the actor.
     * @param to The position the actor will move to
     * @throws IllegalStateException If level not yet loaded
     * */
    public static void moveActor(Point2D from, Point2D to) {
        if (actorLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }

        Actor actor = actorLayer.getAtPosition(from);

        if (actor != null) {
            actor.setPosition(to);
            actorLayer.removeFromPosition(from);
            actorLayer.setAtPosition(to, actor);

            Tile offTile = tileLayer.getAtPosition(from);
            if (offTile instanceof ActionTile) {
                ((ActionTile) offTile).walkedOff(actor);
            }

            Tile onTile = tileLayer.getAtPosition(to);
            if (onTile instanceof ActionTile) {
                ((ActionTile) onTile).walkedOn(actor);
            }
        }
    }

    /**
     * Removes an Item from the layer.
     *
     * @param position the position of the item
     * @throws IllegalStateException if Level has not yet been loaded
     * */
    public static void removeItem(Point2D position) {
        if (itemLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        itemLayer.removeFromPosition(position);
    }

    /**
     * Removes an Actor from the layer.
     *
     * @param position the position of the Actor
     * @throws IllegalStateException if Level has not yet been loaded
     * */
    public static void removeActor(Point2D position) {
        if (actorLayer == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }
        actorLayer.removeFromPosition(position);
    }

    /**
     * Set the profile for the current player.
     * @param profile The new player's profile.
     */
    public static void setPlayerProfile(PlayerProfile profile) {
        playerProfile = profile;
    }

    /**
     * Get the current player's profile.
     * @return The profile of the current player.
     */
    public static PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    /**
     * Sets up commonly used attributes in a level.
     * @param lv the level to get details from
     * */
    private static void setLevelParams(Level lv) {
        levelWidth = lv.getWidth();
        levelHeight = lv.getHeight();

        tileLayer = lv.getTileLayer();
        actorLayer = lv.getActorLayer();
        itemLayer = lv.getItemLayer();

        // Trigger walkedOn for ActionTiles an Actor spawns on
        for (Actor a : actorLayer.getAllElements()) {
            Tile tile = tileLayer.getAtPosition(a.getPosition());
            if (tile instanceof ActionTile) {
                ((ActionTile) tile).walkedOn(a);
            }
        }

        Clock.setLevelDuration(level.getDuration());
    }

    /**
     * Loads a level from a given path.
     * @param path the level path.
     * @param lvlNumber the level number in load order, null if unordered
     * @param lastLevel whether the level is last in the load order.
     * @throws InputMismatchException If the level is the incorrect format.
     * @throws IOException If the level is being read from resources, and it can't be accessed.
     */
    public static void loadLevel(String path, Integer lvlNumber, boolean lastLevel)
            throws InputMismatchException, IOException {
        try {
            levelNumber = lvlNumber;
            isLastLevel = lastLevel;
            if (levelNumber != null) {
                level = new Level(Level.class.getResourceAsStream(path));
            } else {
                level = new Level(path);
            }

            String[] pathParts = path.split("[\\\\/]");
            levelName = pathParts[pathParts.length - 1].split("\\.")[0];
            level.readFile();
            setLevelParams(level);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path);
        }
    }

    /**
     * Load a level from an Auto save.
     * @param path The path to the level file,
     * @throws InputMismatchException If the level format was wrong (should never occur).
     */
    public static void loadFromAutosave(String path) throws InputMismatchException {
        try {
            level = new Level(path);
            String[] pathParts = path.split("[\\\\/]");
            String tmp = pathParts[pathParts.length - 1].split("\\.")[0];
            levelName = tmp.split("-")[1];

            level.readFile();

            setLevelParams(level);

            levelNumber = level.getLevelNumber();
            isLastLevel = level.isLastLevel();

            Player p = (Player) checkActor(actorLayer.findPositionsOf(TileType.PLAYER).get(0));
            p.setInventory(level.getInventory());
        } catch (FileNotFoundException e) {
            System.out.println("File not found!" + path);
        }
    }

    /**
     * Loads the next level in the load order.
     * @throws IOException If the level cannot be loaded.
     * */
    public static void loadNextLevel() throws IOException {
        ArrayList<LevelDetails> levelDetails = LevelDetails.readLevelIndex();
        LevelDetails nextLevelDetails = levelDetails.get(levelNumber);
        loadLevel(nextLevelDetails.getPath(), nextLevelDetails.getLevelNum(), nextLevelDetails.isLastLevel());
    }

    /**
     * Restart the current level.
     * Resets the state of the level.
     */
    public static void restartLevel(boolean usedExtraLife) {
        if (level == null) {
            throw new IllegalStateException("Level has not yet been loaded!");
        }

        tileLayer = level.getTileLayer();
        actorLayer = level.getActorLayer();
        itemLayer = level.getItemLayer();

        // Trigger walkedOn for ActionTiles an Actor spawns on
        for (Actor a : actorLayer.getAllElements()) {
            Tile tile = tileLayer.getAtPosition(a.getPosition());
            if (tile instanceof ActionTile) {
                ((ActionTile) tile).walkedOn(a);
            }
        }

        // Reset the timer and level duration if an extra life was used
        if (usedExtraLife) {
            Clock.resetLevelDuration();
            Clock.resetRemainingTime();
        } else {
            Clock.setLevelDuration(level.getDuration());
        }

        // Logging player information
        Point2D playerPosition = getPlayerPosition();
        System.out.println("Player Position after restart: " + playerPosition);

        Player player = (Player) checkActor(playerPosition);
        if (player != null) {
            System.out.println("Player Inventory after restart: " + Arrays.toString(player.getInventory()));
        }
    }



    /**
     * Checks if a level has been loaded.
     *
     * @return Whether a level has been loaded.
     */
    public static boolean isLevelLoaded() {
        return tileLayer != null;
    }

    /**
     * Checks if the game timer is running.
     * @return Whether the timer is running.
     */
    public static boolean isGameTimerRunning() {
        return gameTimer != null && gameTimer.isRunning();
    }

    /**
     * Stops the game timer running.
     */
    public static void stopTimer() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    /**
     * Gets the width of the currently loaded level.
     *
     * @return The width of the level.
     */
    public static int getLevelWidth() {
        return levelWidth;
    }

    /**
     * Gets the height of the currently loaded level.
     *
     * @return The height of the level.
     */
    public static int getLevelHeight() {
        return levelHeight;
    }

    /**
     * Returns whether it is possible to load a next level.
     * @return whether it is possible to load a next level.
     * @throws IllegalStateException if level not loaded.
     * */
    public static boolean isNextPossible() throws IllegalStateException {
        if (!isLevelLoaded()) {
            throw new IllegalStateException("Level not loaded!");
        }
        return !isLastLevel && levelNumber != null;
    }

    /**
     * Gets the position of the player, if one exists.
     *
     * @return The position of the player.
     */
    public static Point2D getPlayerPosition() {
        ArrayList<Point2D> playerPositions = actorLayer.findPositionsOf(TileType.PLAYER);
        if (playerPositions.isEmpty()) {
            return null;
        }
        return playerPositions.getFirst();
    }

    /**
     * Starts the game.
     *
     * @param viewController The view controller for the game.
     * @throws IllegalStateException if level not initiated.
     * */
    public static void startGame(GameViewController viewController) throws IllegalStateException {
        if (tileLayer == null) {
            throw new IllegalStateException("Level not loaded!");
        }

        gameViewController = viewController;
        gameTimer = new GameTimer();
        gameTimer.setGameViewController(viewController);

        gameTimer.start();
        gameTimer.startLevel();
    }

    /**
     * Ends the game and restarts the current level.
     *
     * @param deathState Enum death state pertaining to the way the player died.
     * @throws IllegalStateException if level not initiated.
     */
    public static void endGame(DeathState deathState) throws IllegalStateException {
        if (tileLayer == null) {
            throw new IllegalStateException("Level not loaded!");
        }

        System.out.println("End Game called with death state: " + deathState);

        if (deathState != DeathState.EXTRA) {
            if (Player.extraLives > 0) {
                // If the player has an extra life, reset the player, decrement the extra lives, and reset the timer
                Player.extraLives--;
                Player player = (Player) checkActor(getPlayerPosition());
                player.setInventory(new int[]{0, 0, 0, 0, 0});
                restartLevel(true);
                System.out.println("Player used an extra life. Remaining extra lives: " + Player.extraLives);
                return;
            }
        }

        if (levelNumber != null) {
            PlayerViewController.tryDeleteAutoSave(levelNumber);
        }

        stopTimer();
        gameViewController.gameLose(deathState);
    }



    /**
     * Processes score for the level, and loads the next.
     *
     * @throws IllegalStateException if level not loaded
     * */
    public static void winLevel() throws IllegalStateException {
        if (tileLayer == null) {
            throw new IllegalStateException("Level not loaded!");
        }
        if (levelNumber != null) {
            PlayerViewController.tryDeleteAutoSave(levelNumber);
        }
        stopTimer();
        // change max completed level
        if (levelNumber != null && playerProfile.getMaxUnlockedLevel() == levelNumber) {
            playerProfile.setMaxUnlockedLevel(levelNumber + 1);
            try {
                int numLevels = LevelDetails.readLevelIndex().size();
                if (levelNumber >= numLevels) {
                    playerProfile.setMaxUnlockedLevel(numLevels);
                }
            } catch (IOException e) {
                // Do nothing.
            }
            try {
                PlayerProfileManager.savePlayerProfiles();
            } catch (IOException e) {
                throw new RuntimeException("Could not save player profiles!\n" + e);
            }

        }
        gameViewController.gameWin();

        if (levelNumber != null) {
            // Get the player's score and level number
            int playerScore = Clock.getRemainingTime(); // Example: Using remaining time as score

            // Create PlayerData object for the current player's score
            PlayerData playerScoreData = new PlayerData(playerProfile.getPlayerName(), playerScore);

            try {
                // Add the player's score to high scores for the level
                HighScoreTable.addScore(levelNumber - 1, playerScoreData);

                // Save the updated high scores for this level
                List<PlayerData> updatedScores = HighScoreTable.displayHighScores(levelNumber - 1);
                HighScoreTable.saveHighScores(levelNumber - 1, updatedScores);
            } catch (IOException e) {
                // Handle the IOException accordingly
                e.printStackTrace(); // Example: Printing stack trace for simplicity
            }
        }
    }
    /**
     * Outputs actors and their facing direction as a string.
     * @return String of actors and their directions.
     * */
    private static String getActorDirections() {
        StringBuilder outStr = new StringBuilder();

        for (Actor a : actorLayer.getAllElements()) {
            Point2D pos = a.getPosition();
            char d = Actor.directionToChar(a.getFacingDir());
            outStr.append(String.format("(%d,%d) @ %s\n", (int) pos.getX(), (int) pos.getY(), d));
        }
        return outStr.toString();
    }

    /**
     * Get list of chips in their correct save file format (x,y) # n.
     * @return list of chip readers and counts
     * */
    private static String getChipCounts() {
        StringBuilder outStr = new StringBuilder();
        ArrayList<Point2D> chipSockets = tileLayer.findPositionsOf(TileType.CHIP_SOCKET);
        for (Point2D p : chipSockets) {
            if (tileLayer.getAtPosition(p) instanceof ChipSocket chipSocket) {
                outStr.append(String.format("(%d,%d) # %d\n",
                        (int) p.getX(), (int) p.getY(), chipSocket.getRequiredChips()));
            }
        }
        return outStr.toString();
    }

    /**
     * Get the player's inventory in their correct save file format i / n.
     * @return List of items in the player's inventory.
     */
    private static String getInventory() {
        StringBuilder outStr = new StringBuilder();
        Actor playerActor = checkActor(getPlayerPosition());
        if (playerActor instanceof Player player) {
            int[] inv = player.getInventory();
            for (int i = 0; i < inv.length; i++) {
                outStr.append(String.format("%s / %d\n", Player.InventorySlot.values()[i].toString(), inv[i]));
            }
        }
        return outStr.toString();
    }

    /**
     * Get list of traps and the buttons they are linked to.
     * @return A list of buttons and the traps they are linked to
     * */
    private static String getButtonAssocs() {
        StringBuilder outStr = new StringBuilder();
        ArrayList<Point2D> buttons = tileLayer.findPositionsOf(TileType.BUTTON);
        for (Point2D p : buttons) {
            Button b = (Button) tileLayer.getAtPosition(p);
            ArrayList<Trap> linkedTraps = b.getLinkedTraps();
            for (Trap t : linkedTraps) {
                Point2D pos = t.getPosition();
                outStr.append(String.format("(%d,%d) -> (%d,%d)\n",
                        (int) p.getX(), (int) p.getY(), (int) pos.getX(), (int) pos.getY()));
            }
        }
        return outStr.toString();
    }

    /**
     * On an abrupt exit, the level progress is to be saved.
     * */
    public static void saveLevelProgress() {
        String tileLayerStr = tileLayer.toString();
        String actorLayerStr = actorLayer.toString();
        String itemLayerStr = itemLayer.toString();
        String levelDims = String.format("%d,%d", levelWidth, levelHeight);
        String timeRemaining = Integer.toString(Clock.getRemainingTime());
        String chipCounts = getChipCounts();
        String buttonAssocs = getButtonAssocs();
        String actorDirecitons = getActorDirections();
        String inventory = getInventory();
        String levelFlags = String.format("%d,%d", levelNumber, isLastLevel ? 1 : 0);

        String saveFile = String.format("%s\n%s\n\n%s\n%s\n%s\n%s%s%s\n%s\n%s", levelDims, timeRemaining,
                tileLayerStr, actorLayerStr, itemLayerStr,
                buttonAssocs, chipCounts, actorDirecitons, inventory, levelFlags);
        String fileName = String.format("%s-%s.sav", playerProfile.getPlayerName(), levelName);
        String workingDir = PlayerProfileManager.getAppDataDirectory();

        try {
            Writer fileWriter = new FileWriter(workingDir + fileName, false);
            fileWriter.write(saveFile);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to write level data:" + e);
        }
    }

    /**
     * Returns true if the level is being timed.
     * @return true if level is being timed.
     * */
    public static boolean isLevelRunning() {
        return gameTimer != null && gameTimer.isRunning() && gameTimer.isTimingLevel();
    }


}
