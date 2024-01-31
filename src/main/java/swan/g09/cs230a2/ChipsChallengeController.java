package swan.g09.cs230a2;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller class for the main view.
 *
 * @author Barnaby Morley-Smith
 */
public class ChipsChallengeController {
    /**
     * Navigate to the player select view.
     * @param event The event from the action trigger.
     */
    @FXML
    private void playGame(ActionEvent event) {
        event.consume();

        try {
            ChipsChallengeApplication.changeScene("player-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
        } catch (IOException e) {
            System.out.println("Failed to load player select view:\n" + e);
        }
    }

    /**
     * Navigate to the high scores view.
     * @param event The event from the action trigger.
     */
    @FXML
    private void viewHighScores(ActionEvent event) {
        event.consume();

        try {
            ChipsChallengeApplication.changeScene("high-scores-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
        } catch (IOException e) {
            System.out.println("Failed to load high scores view:\n" + e);
        }
    }

    /**
     * Close the game.
     * @param event The event from the action trigger.
     */
    @FXML
    private void quitGame(ActionEvent event) {
        event.consume();

        Platform.exit();
        System.exit(0);
    }
}
