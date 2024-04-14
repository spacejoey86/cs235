package swan.g09.cs230a2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Objects;

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
     * Number of milliseconds in a second.
     */
    private static final int MILLIS_IN_SECOND = 1000;

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
     * The image of the player's chip count.
     */
    @FXML
    private ImageView chipImage;

    /**
     * The label for the chip count.
     */
    @FXML
    private Label chipCountLabel;

    /**
     * The image of the player's red key count.
     */
    @FXML
    private ImageView redKeyImage;

    /**
     * The image of the player's green key count.
     */
    @FXML
    private ImageView greenKeyImage;

    /**
     * The image of the player's yellow key count.
     */
    @FXML
    private ImageView yellowKeyImage;

    /**
     * The image of the player's blue key count.
     */
    @FXML
    private ImageView blueKeyImage;

    /**
     * Extra life inventory indicator.
     */
    @FXML
    private ImageView extraLifeImage;

    /**
     * Label for the count of extra lifes.
     */
    @FXML
    private Label extraLifeCountLabel;

    /**
     * Invincibility inventory indicator.
     */
    @FXML
    private ImageView invincibleImage;

    /**
     * Label for the duration of invincibility remaining.
     */
    @FXML
    private Label invincibleRemainingLabel;

    /**
     * Speed boost inventory indicator.
     */
    @FXML
    private ImageView speedImage;

    /**
     * Label for the duration of speed boost remaining.
     */
    @FXML
    private Label speedRemainingLabel;

    /**
     * If the canvas has drawn since the last tick.
     */
    private boolean renderedSinceLastTick = false;

    /**
     * Turns seconds to minutes and seconds.
     * @param seconds Level time remaining.
     * @return A string with the duration formatted.
     */
    private static String formatLevelTime(int seconds) {
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

        chipImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Chip.png"))));
        redKeyImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Key_Red.png"))));
        greenKeyImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Key_Green.png"))));
        yellowKeyImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Key_Yellow.png"))));
        blueKeyImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Key_Blue.png"))));
        extraLifeImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/ExtraLife.png"))));
        invincibleImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Invincible.png"))));
        speedImage.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("sprites/Speed.png"))));

        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

        clock.setText(formatLevelTime(Clock.getRemainingTime()));
        canvas.draw();
    }

    /**
     * Starts the game.
     *
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
                GameManager.updateInventoryDisplay();
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

            if (ChipsChallengeApplication.barnacleEventStarted) {
                ChipsChallengeApplication.endEvent();
            }

            gameEndOverlay.setVisible(true);
            winLoseText.setText("You lose!");

            String deathText = switch (deathState) {
                case CRUSH ->
                    "Crushed by a moving block.";
                case DROWN ->
                    "You couldn't swim.";
                case BOUNCED ->
                    "Bounced on by a pink ball.";
                case BUG_KILL ->
                    "Bug ate you.";
                case TIME_OUT ->
                    "Ran out of time.";
                case FROG_KILL ->
                    "Frog killed you.";
                case EXTRA ->
                    "You have an extra life.";
                case BARNACLE ->
                    "Barnacle killed you";
            };
            flavourText.setText(deathText);

            playAgainBtn.setVisible(true);
            playAgainBtn.setManaged(true);
            nextLevelBtn.setVisible(false);
            nextLevelBtn.setManaged(false);
        });
    }

    /**
     * Update the canvas to reflect the game state.
     *
     * @param inventory The player's inventory.
     */
    public void updateInventoryDisplay(int[] inventory) {
        Player player = (Player) GameManager.checkActor(GameManager.getPlayerPosition());

        int chipCount = inventory[Player.InventorySlot.CHIP.ordinal()];
        chipImage.setVisible(chipCount > 0);
        chipCountLabel.setVisible(chipCount > 0);
        chipCountLabel.setText(String.valueOf(chipCount));

        redKeyImage.setVisible(inventory[Player.InventorySlot.RED_KEY.ordinal()] > 0);
        greenKeyImage.setVisible(inventory[Player.InventorySlot.GREEN_KEY.ordinal()] > 0);
        yellowKeyImage.setVisible(inventory[Player.InventorySlot.YELLOW_KEY.ordinal()] > 0);
        blueKeyImage.setVisible(inventory[Player.InventorySlot.BLUE_KEY.ordinal()] > 0);

        int extraLives = Player.getExtraLives();
        extraLifeImage.setVisible(extraLives > 0);
        extraLifeCountLabel.setVisible(extraLives > 0);
        extraLifeCountLabel.setText(String.valueOf(extraLives));

        int invincibleRemaining = player.getInvincibleRemaining() / (MILLIS_IN_SECOND / GameTimer.getTickRate());
        invincibleImage.setVisible(invincibleRemaining > 0);
        invincibleRemainingLabel.setVisible(invincibleRemaining > 0);
        invincibleRemainingLabel.setText(formatLevelTime(invincibleRemaining));

        int speedBoostRemaining = player.getSpeedBoostRemaining() / (MILLIS_IN_SECOND / GameTimer.getTickRate());
        speedImage.setVisible(speedBoostRemaining > 0);
        speedRemainingLabel.setVisible(speedBoostRemaining > 0);
        speedRemainingLabel.setText(formatLevelTime(speedBoostRemaining));
    }

    /**
     * Reset the inventory display.
     */
    public void resetInventoryDisplay() {
        // Hide or reset the inventory item images
        chipImage.setVisible(false);
        redKeyImage.setVisible(false);
        greenKeyImage.setVisible(false);
        yellowKeyImage.setVisible(false);
        blueKeyImage.setVisible(false);
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
     */
    @FXML
    public void playAgain() {
        GameManager.restartLevel(false);
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
