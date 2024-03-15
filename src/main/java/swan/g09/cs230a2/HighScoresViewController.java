package swan.g09.cs230a2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The UI controller for the high scores view.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class HighScoresViewController {
    /**
     * The width of the name column.
     */
    public static final double NAME_COLUMN_WIDTH = 0.6;

    /**
     * The gap between columns.
     */
    public static final double COLUMN_GAP = 2.0;

    /**
     * The high score table UI element.
     */
    @FXML
    private TableView<PlayerData> highScoreTable;

    /**
     * The username table column UI element.
     */
    @FXML
    private TableColumn<PlayerData, String> username;

    /**
     * The score table column UI element.
     */
    @FXML
    private TableColumn<PlayerData, String> score;

    /**
     * The level select combo box.
     */
    @FXML
    private ComboBox<LevelDetails> levelComboBox;

    /**
     * Initialisation method for the High Scores view.
     */
    @FXML
    public void initialize() {
        ArrayList<LevelDetails> detailsList = new ArrayList<>();

        try {
            detailsList = LevelDetails.readLevelIndex();
        } catch (IOException e) {
            // Couldn't read level details :(
        }

        levelComboBox.getItems().setAll(detailsList);
        if (!detailsList.isEmpty()) {
            levelComboBox.getSelectionModel().select(0);
            selectLevel(0);
        }

        // Listen to select changes
        levelComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            selectLevel(levelComboBox.getSelectionModel().getSelectedIndex());
        });

        username.setCellValueFactory(new PropertyValueFactory<PlayerData, String>("name"));
        score.setCellValueFactory(new PropertyValueFactory<PlayerData, String>("score"));

        // Automatically size columns
        username.prefWidthProperty().bind(highScoreTable.widthProperty().multiply(NAME_COLUMN_WIDTH));
        score.prefWidthProperty().bind(highScoreTable.widthProperty()
                .subtract(COLUMN_GAP).subtract(username.widthProperty()));
    }

    /**
     * Select a level to show high scores for.
     * 
     * @param index The level index.
     */
    private void selectLevel(int index) {
        try {
            List<PlayerData> highScores = HighScoreTable.loadHighScores(index);
            highScoreTable.getItems().setAll(highScores);
        } catch (IOException e) {
            // Clear high scores if it can't be read
            highScoreTable.getItems().clear();
        }
    }

    /**
     * Navigates to the main view.
     * 
     * @param event The event from the action trigger.
     */
    @FXML
    private void goBack(ActionEvent event) {
        event.consume();

        try {
            ChipsChallengeApplication.changeScene("main-view.fxml",
                    ChipsChallengeApplication.MAIN_VIEW_WIDTH, ChipsChallengeApplication.MAIN_VIEW_HEIGHT, false);
        } catch (IOException e) {
            System.out.println("Failed to load main view:\n" + e);
        }
    }
}
