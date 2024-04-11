package swan.g09.cs230a2;


import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Pause screen for the level.
 * @author Connor Milford
 */

public class PauseMenu extends VBox {

    //TODO: Add save, restart from save, quit game, return to menu

    /** Text displayed on the resume button. */
    private static final String RESUME_BUTTON_TEXT = "Resume";

    /** Text displayed on the quit button. */
    private static final String  RETURN_BUTTON_TEXT = "Return";

    /** Text displayed on the help menu button. */
    private static final String HELP_MENU_BUTTON_TEXT = "Help menu";

    /** Text displayed on the restart button. */
    private static final String RESTART_BUTTON_TEXT = "Restart";

    /** Text displayed on the restart button. */
    private static final String SAVE_BUTTON_TEXT = "Save";

    /** Text displayed on the load from save button. */
    private static final String LOAD_FROM_SAVE_BUTTON_TEXT = "Load from save";

    /** Text displayed on popup when saved from pause menu. */
    private static final String SAVE_ALERT_TEXT = "Game data saved.";

    /** Text displayed on popup when save loaded from pause menu. */
    private static final String LOAD_ALERT_TEXT = "Game loaded from save.";

    /** Pause menu item width, relative to the window. */
    private static final int PAUSE_MENU_WIDGET_WIDTH = 10;

    /** Pause menu item height, relative to the window. */
    private static final double PAUSE_MENU_WIDGET_HEIGHT = 10;

    /** Pause menu item spacing. */
    private static final int PAUSE_MENU_SPACING = 50;

    /**
     * Constructor for the pause menu.
     */
    public PauseMenu() {
        final StackPane root = (StackPane) ChipsChallengeApplication.getStage().getScene().getRoot();
        final Button resumeButton = new Button(RESUME_BUTTON_TEXT);
        final Button returnButton = new Button(RETURN_BUTTON_TEXT);
        final Button helpMenuButton = new Button(HELP_MENU_BUTTON_TEXT);
        final Button restartButton = new Button(RESTART_BUTTON_TEXT);
        final Button saveButton = new Button(SAVE_BUTTON_TEXT);
        final Button loadFromSaveButton = new Button(LOAD_FROM_SAVE_BUTTON_TEXT);

        resumeButton.setOnAction(e -> resumeGame());
        returnButton.setOnAction(e -> quitGame());
        helpMenuButton.setOnAction(e -> openHelpMenu());
        restartButton.setOnAction(e -> restartLevel());
        saveButton.setOnAction(e -> saveGame());
        loadFromSaveButton.setOnAction(e -> loadFromSave());

        resumeButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        resumeButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        returnButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        returnButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        helpMenuButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        helpMenuButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        restartButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        restartButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        saveButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        saveButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        loadFromSaveButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        loadFromSaveButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(resumeButton, restartButton, helpMenuButton,
                saveButton, loadFromSaveButton, returnButton);
        this.setSpacing(PAUSE_MENU_SPACING);
    }

    /**
     * Closes the pause menu.
     */
    private void resumeGame() {
        ChipsChallengeApplication.closePauseMenu();
    }

    /**
     * Quits the current game scene and navigates to the main menu view fxml file.
     */
    private void quitGame() {
        try {
            ChipsChallengeApplication.changeScene("main-view.fxml",
                    ChipsChallengeApplication.DEFAULT_VIEW_WIDTH,
                    ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false
            );
        } catch (IOException e) {
            System.out.println("Failed to load levels view: \n" + e.getMessage());
        }
    }

    /**
     * Restarts the current level.
     */
    private void restartLevel() {
        GameManager.restartLevel(false);
        GameManager.resetTimer();
        resumeGame();
    }

    /**
     * Opens the help menu.
     */
    private void openHelpMenu() {
        //TODO: probably do something here
    }

    /**
     * Saves the game from the menu.
     */
    private void saveGame() {
        GameManager.saveLevelProgress();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, SAVE_ALERT_TEXT);
        alert.showAndWait();
    }

    /**
     * Loads the game from a chosen save file.
     */
    private void loadFromSave() {
        // need to find path to save file
        String path = filePicker();
        if (path != null) {
            GameManager.loadFromAutosave(path);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, LOAD_ALERT_TEXT);
            alert.showAndWait();
        }
    }

    /**
     * Chooses a save file to be loaded.
     * @return path, the path of the file chosen
     */
    private String filePicker() {
        FileChooser fileChooser = new FileChooser();

        //Filter file types to just .sav
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(
                "Save Files (*.sav)", "*.sav");
        fileChooser.getExtensionFilters().add(extensionFilter);

        //Set default dir
        File saveFileDir = new File(PlayerProfileManager.getAppDataDirectory());
        fileChooser.setInitialDirectory(saveFileDir);

        File chosenFile = fileChooser.showOpenDialog(null);

        String path = null;
        if (chosenFile != null) {
            path = chosenFile.getPath();
        }

        return path;
    }
}
