package swan.g09.cs230a2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Application class for the JavaFX application.
 * Handles navigating to different views and saving persistent files.
 *
 * @author Barnaby Morley-Smith
 * @author Samuel Lomas
 */
public class ChipsChallengeApplication extends Application {
    /**
     * The width of the main view.
     */
    public static final int MAIN_VIEW_WIDTH = 480;

    /**
     * The height of the main view.
     */
    public static final int MAIN_VIEW_HEIGHT = 320;

    /**
     * The default width of views.
     */
    public static final int DEFAULT_VIEW_WIDTH = 1280;

    /**
     * The default height of views.
     */
    public static final int DEFAULT_VIEW_HEIGHT = 720;

    /**
     * The stage being shown to the user.
     */
    private static Stage primaryStage;

    /**
     * Whether the game is paused or not.
     */
    private static boolean paused = false;

    /**
     * Whether or not the barnacle event has started, true if started.
     */
    public static boolean barnacleEventStarted = false;

    /**
     * Runs when the JavaFX program is started.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws IOException If the main view cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ChipsChallengeApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), MAIN_VIEW_WIDTH, MAIN_VIEW_HEIGHT);
        stage.setMinWidth(MAIN_VIEW_WIDTH);
        stage.setMinHeight(MAIN_VIEW_HEIGHT);
        stage.setTitle("Chip's Challenge");
        stage.setScene(scene);
        stage.centerOnScreen();

        // Set up appdata path
        PlayerProfileManager.setupAppDataPath();

        // Set up input manager
        InputManager.setScene(scene);

        stage.show();
    }

    /**
     * Runs when the JavaFX program is terminated.
     * Handles stopping the game timer.
     */
    @Override
    public void stop() {
        boolean wasInGame = GameManager.isLevelRunning();
        GameManager.stopTimer();
        if (PlayerProfileManager.isLoadedData()) {
            try {
                PlayerProfileManager.savePlayerProfiles();
            } catch (IOException e) {
                throw new RuntimeException("Error in saving profile data!");
            }
        }
        if (wasInGame) {
            GameManager.saveLevelProgress();
        }
    }

    /**
     * Main method for the program.
     * @param args Startup args, ignored.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Navigate to another scene.
     * @param fxmlPath The path to the FXML file to load.
     * @param width The width to make the stage.
     * @param height The height to make the stage.
     * @param maximise Whether the window should be maximised
     * @throws IOException If the FXML file cannot be loaded.
     */
    public static void changeScene(String fxmlPath, int width, int height, boolean maximise) throws IOException {
        if (primaryStage == null) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(ChipsChallengeApplication.class.getResource(fxmlPath));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        primaryStage.setMaximized(false);
        primaryStage.setScene(scene);

        if (maximise) {
            primaryStage.setMaximized(true);
        } else {
            primaryStage.centerOnScreen();
        }
        // Update scene in input manager
        InputManager.setScene(scene);
    }

    /**
     * Gets the currently loaded stage.
     * @return The stage being shown to the user.
     */
    public static Stage getStage() {
        return primaryStage;
    }


    /**
     * Starts the barnacle event
     */
    public static void startEvent() {
        // ensures thread is open
        Platform.runLater(() -> {
            if (!barnacleEventStarted) {
                final StackPane root = (StackPane) getStage().getScene().getRoot();
                root.getChildren().add(new BarnacleEvent());
                barnacleEventStarted = true;
            }
        });
    }

    /**
     * Ends the barnacle event
     */
    public static void endEvent() {
        Platform.runLater(() -> {
            final StackPane root = (StackPane) getStage().getScene().getRoot();
            if (root.getChildren().size() > 1 && barnacleEventStarted) {
                root.getChildren().remove(root.getChildren().size() -1);
                barnacleEventStarted = false;
            }
        });
    }

    /**
     * Opens the pause menu.
     */
    public static void openPauseMenu() {
        // ensures thread is open
        Platform.runLater(() -> {
            if (!paused) {
                GameManager.pauseTimer();
                final StackPane root = (StackPane) getStage().getScene().getRoot();
                root.getChildren().add(new PauseMenu());
                paused = true;
            }
        });
    }

    /**
     * Closes the pause menu.
     */
    public static void closePauseMenu() {
        // ensures thread is open
        Platform.runLater(() -> {
            final StackPane root = (StackPane) getStage().getScene().getRoot();
            if (root.getChildren().size() > 1 && paused) {
                root.getChildren().remove(root.getChildren().size() - 1);
                GameManager.unpauseTimer();
                paused = false;
            }
        });
    }

    /**
     * Set the paused state of the game.
     * @param isPaused Whether the game should be paused
     */
    public static void setPaused(boolean isPaused) {
        paused = isPaused;
    }
}
