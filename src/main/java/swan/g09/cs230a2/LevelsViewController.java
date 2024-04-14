package swan.g09.cs230a2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

import static swan.g09.cs230a2.HighScoresViewController.NAME_COLUMN_WIDTH;

/**
 * The UI controller for the levels view.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class LevelsViewController {

    /**
     * The width of the duration column.
     */
    public static final double DURATION_COLUMN_SIZE = 0.2;
    /**
     * The level table UI element.
     */
    @FXML
    private TableView<LevelDetails> levelTable;

    /**
     * The level name table column UI element.
     */
    @FXML
    private TableColumn<LevelDetails, String> levelName;

    /**
     * The duration table column UI element.
     */
    @FXML
    private TableColumn<LevelDetails, String> duration;

    /**
     * The high scores table column UI element.
     */
    @FXML
    private TableColumn<LevelDetails, Integer> highScores;

    /**
     * The loaded level subheader UI element.
     */
    @FXML
    private Text loadedLevelSubheader;

    /**
     * The loaded level path UI element.
     */
    @FXML
    private Label loadedLevelPath;

    /**
     * The level load error UI element.
     */
    @FXML
    private Label levelLoadError;

    /**
     * Initialisation method for the Levels view.
     */
    @FXML
    public void initialize() {
        ArrayList<LevelDetails> levelDetailsList = readLevelIndex();
        levelTable.getItems().setAll(levelDetailsList);

        levelName.setCellValueFactory(new PropertyValueFactory<LevelDetails, String>("name"));
        duration.setCellValueFactory(new PropertyValueFactory<LevelDetails, String>("duration"));
        highScores.setCellValueFactory(new PropertyValueFactory<LevelDetails, Integer>("score"));
        // Automatically size columns
        levelName.prefWidthProperty().bind(levelTable.widthProperty().multiply(NAME_COLUMN_WIDTH));
        duration.prefWidthProperty().bind(levelTable.widthProperty().multiply(DURATION_COLUMN_SIZE));
        highScores.prefWidthProperty().bind(levelTable.widthProperty().multiply(DURATION_COLUMN_SIZE));

        // Disable rows in the table if they're not unlocked
        int maxLevel = GameManager.getPlayerProfile().getMaxUnlockedLevel();
        levelTable.setRowFactory(tv -> {
            TableRow<LevelDetails> row = new TableRow<>();
            row.disableProperty().bind(row.indexProperty().greaterThanOrEqualTo(maxLevel));
            row.disableProperty().addListener((options, oldValue, newValue) -> {
                if (row.getIndex() >= levelDetailsList.size()) {
                    return;
                }
                if (newValue && row.getItem() != null) {
                    row.setBackground(Background.fill(Color.LIGHTGRAY));
                } else {
                    row.setBackground(null);
                }
            });
            return row;
        });
    }

    private ArrayList<LevelDetails> readLevelIndex() {
        ArrayList<LevelDetails> detailsList = new ArrayList<>();

        try {
            detailsList = LevelDetails.readLevelIndex();
        } catch (IOException e) {
            levelLoadError.setVisible(true);
            loadedLevelSubheader.setVisible(false);
            loadedLevelPath.setVisible(false);
            levelLoadError.setText(e.getMessage());
        }

        return detailsList;
    }

    /**
     * Loads a level from a file.
     *
     * @param event The event from the action trigger.
     */
    @FXML
    private void loadLevelFromFile(ActionEvent event) {
        event.consume();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Level Files", "*.txt", "*.level"));
        fileChooser.setTitle("Select Level File");
        File file = fileChooser.showOpenDialog(ChipsChallengeApplication.getStage());

        if (file != null) {
            try {
                GameManager.loadLevel(file.getAbsolutePath(), null, true);

                levelLoadError.setVisible(false);
                loadedLevelSubheader.setVisible(true);
                loadedLevelPath.setVisible(true);
                loadedLevelPath.setText(file.getAbsolutePath());
            } catch (InputMismatchException | IOException e) {
                levelLoadError.setVisible(true);
                loadedLevelSubheader.setVisible(false);
                loadedLevelPath.setVisible(false);
                levelLoadError.setText(e.getMessage());
            }
        }
    }

    /**
     * Navigates to the game view.
     *
     * @param event The event from the action trigger.
     */
    @FXML
    private void playSelectedLevel(ActionEvent event) {
        event.consume();

        LevelDetails details = levelTable.getSelectionModel().getSelectedItem();
        if (details == null) {
            return;
        }

        try {
            GameManager.loadLevel(details.getPath(), details.getLevelNum(), details.isLastLevel());
        } catch (InputMismatchException | IOException e) {
            return;
        }

        if (!GameManager.isLevelLoaded()) {
            return;
        }

        try {
            ChipsChallengeApplication.changeScene("game-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, true);
        } catch (IOException e) {
            System.out.println("Failed to load game view:\n" + e);
        }
    }

    /**
     * Navigates to the game view.
     *
     * @param event The event from the action trigger.
     */
    @FXML
    private void playCustomLevel(ActionEvent event) {
        event.consume();

        if (!GameManager.isLevelLoaded()) {
            return;
        }

        try {
            ChipsChallengeApplication.changeScene("game-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, true);
        } catch (IOException e) {
            System.out.println("Failed to load game view:\n" + e);
        }
    }

    /**
     * Navigates to the players view.
     *
     * @param event The event from the action trigger.
     */
    @FXML
    private void goBack(ActionEvent event) {
        event.consume();

        try {
            ChipsChallengeApplication.changeScene("player-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH, ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
        } catch (IOException e) {
            System.out.println("Failed to load player view:\n" + e);
        }
    }
}
