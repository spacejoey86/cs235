package swan.g09.cs230a2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;



/**
 * The UI controller for the game view.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class GameViewController {

    /**
     * The map of inventory item indices to their image paths.
     */
    private static final Map<Integer, String> INVENTORY_IMAGE_PATH_MAP = Map.of(
            0, "sprites/Chip.png",
            1, "sprites/Key_Red.png",
            2, "sprites/Key_Green.png",
            3, "sprites/Key_Yellow.png",
            4, "sprites/Key_Blue.png"
    );

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
     * The image of the player's chip count.
     */
    @FXML
    private ImageView chipImage;

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
     * The label for the chip count.
     */
    @FXML
    private Label chipCountLabel;




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
     * Update the canvas to reflect the game state.
     * @param inventory The player's inventory.
     */
    public void updateInventoryDisplay(int[] inventory) {
        chipImage.setVisible(inventory[Player.InventorySlot.CHIP.ordinal()] > 0);
        int chipCount = inventory[Player.InventorySlot.CHIP.ordinal()];
        chipImage.setVisible(chipCount > 0);
        chipCountLabel.setText(String.valueOf(chipCount));
        redKeyImage.setVisible(inventory[Player.InventorySlot.RED_KEY.ordinal()] > 0);
        greenKeyImage.setVisible(inventory[Player.InventorySlot.GREEN_KEY.ordinal()] > 0);
        yellowKeyImage.setVisible(inventory[Player.InventorySlot.YELLOW_KEY.ordinal()] > 0);
        blueKeyImage.setVisible(inventory[Player.InventorySlot.BLUE_KEY.ordinal()] > 0);

        // Update images to reflect the count
        chipImage.setImage(generateImageForInventoryItem(Player.InventorySlot.CHIP.ordinal(),
                inventory[Player.InventorySlot.CHIP.ordinal()]));
        redKeyImage.setImage(generateImageForInventoryItem(Player.InventorySlot.RED_KEY.ordinal(),
                inventory[Player.InventorySlot.RED_KEY.ordinal()]));
        greenKeyImage.setImage(generateImageForInventoryItem(Player.InventorySlot.GREEN_KEY.ordinal(),
                inventory[Player.InventorySlot.GREEN_KEY.ordinal()]));
        yellowKeyImage.setImage(generateImageForInventoryItem(Player.InventorySlot.YELLOW_KEY.ordinal(),
                inventory[Player.InventorySlot.YELLOW_KEY.ordinal()]));
        blueKeyImage.setImage(generateImageForInventoryItem(Player.InventorySlot.BLUE_KEY.ordinal(),
                inventory[Player.InventorySlot.BLUE_KEY.ordinal()]));
    }

    /**
     * Generate an image for an inventory item.
     * @param index The index of the item in the inventory.
     * @param count The count of the item in the inventory.
     * @return The image for the inventory item.
     */
    private Image generateImageForInventoryItem(int index, int count) {
        String imagePath = INVENTORY_IMAGE_PATH_MAP.get(index);
        if (imagePath == null) {
            return null; // Or handle invalid index appropriately
        }
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
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
