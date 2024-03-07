package swan.g09.cs230a2;

/**
 * A {@code Clock} that displays the remaining time for each level.
 * Stores this time for use in calculating score.
 * @author Caragh Waite
 * @author Barnaby Morley-Smith
 * @version 0.1
 **/
public class Clock {
    /**
     * Number of milliseconds in a second.
     */
    private static final int MS_PER_SEC = 1000;

    /**
     * The total time for the level.
     */
    private static int totalTime = 0;

    /**
     * The time remaining on the level.
     */
    private static int remainingTime = 0;

    /**
     * Re-initialises the clock with the supplied level duration.
     * @param duration The duration in seconds of the level.
     */
    public static void setLevelDuration(int duration) {
        totalTime = duration;
        remainingTime = duration;
    }

    /**
     * Ticks the clock's internal timer, used by GameTimer.
     * @param currentLevelTick The count of how many ticks have passed since the level began.
     */
    public static void tick(int currentLevelTick) {
      remainingTime = Math.max(0, totalTime - (currentLevelTick * GameTimer.getTickRate() / MS_PER_SEC));
      if (remainingTime == 0) {
          GameManager.endGame(GameManager.DeathState.TIME_OUT);
      }
    }

    /**
     * Get the remaining time on the level.
     * @return The number of seconds left.
     */
    public static int getRemainingTime() {
        return remainingTime;
    }

    /**
     * Check if the level's timer has reached zero.
     * @return Whether the clock's remaining time has run out.
     */
    public static boolean isFinished() {
        return remainingTime == 0;
    }
}
