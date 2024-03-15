package swan.g09.cs230a2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The level details class gets a level's metadata from level-index.txt
 * as well as the duration from the level file.
 *
 * @author Barnaby Morley-Smith
 */
public class LevelDetails {
    /**
     * The regex pattern for levels in the level index.
     */
    private static final String LEVEL_INDEX_PATTERN = "^(.+?), *(.+)";

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_1 = 1;

    /**
     * Index used for groups in regex matching.
     */
    private static final int REGEX_MATCHER_GROUP_2 = 2;

    /**
     * The number of seconds in a minute.
     */
    private static final int SECONDS_IN_MINUTE = 60;

    /**
     * The path to the resource used by this level.
     */
    private final String path;

    /**
     * The name of this level.
     */
    private final String name;

    /**
     * The duration of this level (in minutes and seconds).
     */
    private final String duration;

    /**
     * If this level is the last in the load order.
     */
    private final boolean isLastLevel;

    /**
     * The number of the level in the load order.
     */
    private final int levelNum;

    /**
     * The Highest score for this level.
     */
    private final int score;

    /**
     * Default constructor for LevelDetails.
     *
     * @param resourcePath The path to the level in the project resources.
     * @param levelName    The name of the level.
     * @param lastLevel    Is this level the last
     * @param lvlNum       The number of the level in the load order
     * @param tempScore    The highest score for this level
     * @throws IOException If the file doesn't exist.
     */
    public LevelDetails(String resourcePath, String levelName, boolean lastLevel, int lvlNum, int tempScore)
            throws IOException {
        path = resourcePath;
        name = levelName;
        isLastLevel = lastLevel;
        levelNum = lvlNum;
        score = tempScore;

        // Read the duration from the level file
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream != null) {
            Scanner in = new Scanner(stream);
            in.nextLine();
            duration = formatLevelTime(in.nextInt());
        } else {
            duration = "0:00";
        }
    }

    /**
     * Turns seconds to minutes and seconds.
     *
     * @param seconds Level time remaining.
     * @return A string with the duration formatted.
     */
    private String formatLevelTime(int seconds) {
        return String.format("%02d:%02d", seconds / SECONDS_IN_MINUTE, seconds % SECONDS_IN_MINUTE);
    }

    /**
     * Get the level's resource path.
     *
     * @return The path to the level's resource.
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the level number in ordering.
     *
     * @return levelNum
     */
    public int getLevelNum() {
        return levelNum;
    }

    /**
     * Get the level's name.
     *
     * @return The name of the level.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the level's duration.
     *
     * @return The duration on the level.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Get the level's highest score.
     *
     * @return The highest score on the level.
     */
    public int getScore() {
        return score;
    }

    /**
     * Reads the level index file.
     *
     * @return The list of level details.
     * @throws IOException If the file couldn't be read successfully.
     */
    public static ArrayList<LevelDetails> readLevelIndex() throws IOException {
        ArrayList<LevelDetails> detailsList = new ArrayList<>();

        InputStream stream = LevelDetails.class.getResourceAsStream("level-index.txt");
        if (stream == null) {
            throw new IOException("Could not read level index");
        }

        Scanner in = new Scanner(stream);
        int lvlNum = 0;
        while (in.hasNextLine()) {
            lvlNum++;
            String line = in.nextLine();
            boolean lastLevel = !in.hasNextLine();
            Pattern pattern = Pattern.compile(LEVEL_INDEX_PATTERN);
            Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                String levelPath = matcher.group(REGEX_MATCHER_GROUP_1);
                String lvlName = matcher.group(REGEX_MATCHER_GROUP_2);
                int tempScore = 0;
                try {
                    tempScore = HighScoreTable.loadHighScores(lvlNum - 1).get(0).getScore();
                } catch (IOException | IndexOutOfBoundsException e) {
                    tempScore = 0;
                }

                LevelDetails details = new LevelDetails(levelPath, lvlName, lastLevel, lvlNum, tempScore);
                detailsList.add(details);
            }
        }

        return detailsList;
    }

    /**
     * returns true if this is the last level in the defined order.
     *
     * @return true if last level in levels file.
     */
    public boolean isLastLevel() {
        return isLastLevel;
    }

    /**
     * Converts the LevelDetails to a string, gets the level name.
     *
     * @return The level name.
     */
    @Override
    public String toString() {
        return name;
    }
}
