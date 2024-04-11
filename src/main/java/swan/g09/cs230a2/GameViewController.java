package swan.g09.cs230a2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * The UI controller for the game view.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class GameViewController {
    /**
     * Number of seconds in a minute.
     */
    private static final int SECONDS_IN_MINUTE = 60;

    /**
     * The clock UI element.
     */
    @FXML
    private Text clock;

    /**
     * The canvas which renders the game.
     */
    private GameCanvas canvas;

    /**
     * The UI element which contains the canvas.
     */
    @FXML
    private Pane canvasPane;

    /**
     * The start button UI element.
     */
    @FXML
    private Button startButton;

    /**
     * The overlay that shows when the game ends.
     */
    @FXML
    private VBox gameEndOverlay;

    /**
     * The text in the game end overlay that says why it ended.
     */
    @FXML
    private Label winLoseText;

    /**
     * The flavour text in the game end overlay.
     */
    @FXML
    private Label flavourText;

    /**
     * Button to play again when you lose.
     */
    @FXML
    private Button playAgainBtn;

    /**
     * Button to play the next level when you win.
     */
    @FXML
    private Button nextLevelBtn;

    /**
     * If the canvas has drawn since the last tick.
     */
    private boolean renderedSinceLastTick = false;

    /**
     * Turns seconds to minutes and seconds.
     * @param seconds Level time remaining.
     * @return A string with the duration formatted.
     */
    private String formatLevelTime(int seconds) {
        return String.format("%02d:%02d", seconds / SECONDS_IN_MINUTE, seconds % SECONDS_IN_MINUTE);
    }

    /**
     * Initialisation method for the Game view.
     */
    @FXML
    public void initialize() {
        canvas = new GameCanvas();
        canvasPane.getChildren().add(canvas);

        gameEndOverlay.setVisible(false);

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        clock.setText(formatLevelTime(Clock.getRemainingTime()));
        canvas.draw();
    }

    /**
     * Starts the game.
     * @param event The event from the action trigger.
     */
    @FXML
    private void startGame(ActionEvent event) {
        event.consume();

        GameManager.startGame(this);
        startButton.setVisible(false);
    }

    /**
     * Allows the timer to request the game be re-rendered on ticks.
     */
    public void tick() {
        renderedSinceLastTick = false;
        Platform.runLater(() -> {
            if (!renderedSinceLastTick) {
                renderedSinceLastTick = true;
                clock.setText(formatLevelTime(Clock.getRemainingTime()));
                canvas.draw();
            }
        });
    }

    /**
     * Handle GUI changes when winning the level.
     */
    public void gameWin() {
        Platform.runLater(() -> {
            gameEndOverlay.setVisible(true);
            winLoseText.setText("You win!");
            flavourText.setText(formatLevelTime(Clock.getRemainingTime()) + " remaining.");

            playAgainBtn.setVisible(false);
            playAgainBtn.setManaged(false);
            //Only display next buttons if it is possible.
            boolean nxtPoss = GameManager.isNextPossible();
            nextLevelBtn.setVisible(nxtPoss);
            nextLevelBtn.setManaged(nxtPoss);
        });
    }

    /**
     * Handle GUI changes when losing the level.
     * @param deathState The player's death state.
     */
    public void gameLose(GameManager.DeathState deathState) {
        Platform.runLater(() -> {
            gameEndOverlay.setVisible(true);
            winLoseText.setText("You lose!");

            String deathText = switch (deathState) {
                case CRUSH -> "Crushed by a moving block.";
                case DROWN -> "You couldn't swim.";
                case BOUNCED -> "Bounced on by a pink ball.";
                case BUG_KILL -> "Bug ate you.";
                case TIME_OUT -> "Ran out of time.";
                case FROG_KILL -> "Frog killed you.";
                case BARNACLE -> "Barnacle killed you";
                case null -> "Died by some unknown cause...";
            };
            flavourText.setText(deathText);

            playAgainBtn.setVisible(true);
            playAgainBtn.setManaged(true);
            nextLevelBtn.setVisible(false);
            nextLevelBtn.setManaged(false);
        });
    }

    /**
     * Navigate to the level select view.
     * @param event The event from the action trigger.
     */
    @FXML
    public void goBack(ActionEvent event) {
        event.consume();

        try {
            ChipsChallengeApplication.changeScene("levels-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
        } catch (IOException e) {
            System.out.println("Failed to load levels view:\n" + e);
        }
    }

    /**
     * Play the level again.
     * @param event The event from the action trigger.
     */
    @FXML
    public void playAgain(ActionEvent event) {
        GameManager.restartLevel();
        gameEndOverlay.setVisible(false);

        GameManager.startGame(this);
    }

    /**
     * Play the next level.
     * @param event The event from the action trigger.
     */
    @FXML
    public void nextLevel(ActionEvent event) {
        event.consume();

        try {
            GameManager.loadNextLevel();
            gameEndOverlay.setVisible(false);
            GameManager.startGame(this);
        } catch (IOException e) {
            throw new RuntimeException("Error loading next level!\n" + e);
        }
    }
}
