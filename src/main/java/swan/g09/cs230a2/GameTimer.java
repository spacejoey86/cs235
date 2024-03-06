package swan.g09.cs230a2;

import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Point2D;

/**
 * A Timer that ticks every entity in the level on a variable interval.
 * @author Samuel Lomas
 * @version 0.1
 * */
public class GameTimer extends TimerTask {

    /**
     * The interval between ticks, in milliseconds.
     * */
    private static final int TICK_RATE = 50;

    /**
     * The number of milliseconds in a second.
     */
    private static final double MILLIS_IN_SECOND = 1000.0;

    /**
     * The internal timer used for scheduling the tick loop.
     */
    private final Timer timer;

    /**
     * The current tick that the timer is on.
     * */
    private int currentTick;

    /**
     * The tick for the start of a level.
     * */
    private int levelStartTick;

    /**
     * The tick for the end of a level.
     * */
    private int levelEndTick;

    /**
     * Boolean used to control the state of the timer.
     * */
    private boolean timerRunning;

    /**
     * A boolean used for detecting whether a level is being timed.
     * */
    private boolean timingLevel;

    /**
     * A boolean used to pause and unpause the timer.
     */
    private boolean paused;

    /**
     * The view controller for rendering the game.
     * */
    private GameViewController gameViewController;

    /**
     * Constructor for class Timer.
     * */
    public GameTimer() {
        timerRunning = false;
        timingLevel = false;
        currentTick = 0;
        paused = false;
        timer = new Timer();
    }

    /**
     * This method handles the main timer loop.
     * */
    @Override
    public void run() {
        if (timerRunning && !paused) {
            currentTick++; //Increment Tick
            InputManager.tick(); //Tick over the input manager

            if (timingLevel) {
                Clock.tick(currentTick - levelStartTick); // Tick over the clock
            }

            // Tick all actors and action tiles
            for (int x = 0; x < GameManager.getLevelWidth(); x++) {
                for (int y = 0; y < GameManager.getLevelHeight(); y++) {
                    Point2D coordinate = new Point2D(x, y);

                    Tile t = GameManager.checkTile(coordinate);
                    if (t instanceof ActionTile actionTile) {
                        actionTile.tick();
                    }

                    Actor a = GameManager.checkActor(coordinate);
                    if (a != null) {
                        a.tick();
                    }
                }
            }

            if (gameViewController != null) {
                gameViewController.tick();
            }
        }
    }

    /**
     * Set the view controller that draws the game.
     * @param controller The view controller to request redraws upon.
     * */
    public void setGameViewController(GameViewController controller) {
        gameViewController = controller;
    }

    /**
     * Sets the tick for a level ending.
     * @throws IllegalStateException if the timer has not been started yet or if level is not being timed
     * */
    public void endLevel() throws IllegalStateException {
        if (!timerRunning) {
            throw new IllegalStateException("Timer has not been started!");
        } else if (!timingLevel) {
            throw new IllegalStateException("Level is not being timed!");
        }
        levelEndTick = currentTick;
        timingLevel = false;
    }

    /**
     * Sets the tick for a level starting.
     * @throws IllegalStateException if the timer has not been started yet or level is already being timed
     * */
    public void startLevel() throws IllegalStateException {
        if (!timerRunning) {
            throw new IllegalStateException("Timer has not been started!");
        } else if (timingLevel) {
            throw new IllegalStateException("Level is already being timed!");
        }
        levelStartTick = currentTick;
        timingLevel = true;
    }

    /**
     * Gets the time to complete the previous level, in seconds.
     * @return the time taken to complete the level, in seconds as a double
     * @throws IllegalStateException if the timer has not been started or the level has not finished being timed
     * */
    public double getTimeToComplete() throws IllegalStateException {

        //TODO: Find a way to tell if a level has never been timed before
        if (!timerRunning) {
            throw new IllegalStateException("Timer has not been started!");
        }  else if (timingLevel) {
            throw new IllegalStateException("Level has not finished being timed!");
        }

        double levelTimeMS = (levelEndTick - levelStartTick) * TICK_RATE;
        return levelTimeMS / MILLIS_IN_SECOND; //calculate ticks taken while in level, and convert to seconds
    }

    /**
     * Starts the timer.
     * @throws IllegalStateException if timer is already running
     * */
    public void start() {
        if (timerRunning) {
            throw new IllegalStateException("Timer is already Running!");
        }
        timerRunning = true;
        timer.schedule(this, TICK_RATE, TICK_RATE);
    }

    /**
     * Stops the timer.
     * @throws IllegalStateException if timer is not running
     * */
    public void stop() throws IllegalStateException {
        if (!timerRunning) {
            throw new IllegalStateException("Timer is not Running!");
        }
        timerRunning = false;
        timer.cancel();
    }

    /**
     * Gets the tick rate of the Timer.
     * @return The time between ticks in milliseconds.
     */
    public static int getTickRate() {
        return TICK_RATE;
    }

    /**
     * Check if the timer is running.
     * @return Whether the timer is running.
     */
    public boolean isRunning() {
        return timerRunning;
    }

    /**
     * Check if a level is being timed.
     * @return a boolean if the level is being timed.
     * */
    public boolean isTimingLevel() {
        return timingLevel;
    }

    /**
     * Pauses the timer at the current time.
     */
    public void pauseTimer() {
        this.paused = true;

    }

    /**
     * Unpauses the timer.
     */
    public void unpauseTimer() {
        this.paused = false;
    }

    /**
     * Returns true if timer is paused, otherwise false.
     * @return paused, Whether the timer is paused.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Restarts the timer to the level default.
     */
    public void resetLevelTimer() {
        levelStartTick = currentTick;
    }
}
