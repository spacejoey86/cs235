package swan.g09.cs230a2;


import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

/**
 * Pause screen for the level
 * @author Connor Milford
 */

public class PauseMenu extends VBox {

    /** Text displayed on the resume button */
    private static final String RESUME_BUTTON_TEXT = "Resume";

    /** Text displayed on the quit button */
    private static final String  QUIT_BUTTON_TEXT = "Quit";

    /** Text displayed on the help menu button */
    private static final String HELP_MENU_BUTTON_TEXT = "Help menu";

    /** Pause menu item width, relative to the window. */
    private static final int PAUSE_MENU_WIDGET_WIDTH = 8;

    /** Pause menu item height, relative to the window. */
    private static final int PAUSE_MENU_WIDGET_HEIGHT = 4;

    /** Pause menu item spacing */
    private static final int PAUSE_MENU_SPACING = 50;

    /**
     * Constructor for the pause menu.
     */
    public PauseMenu(){
        final StackPane root = (StackPane) ChipsChallengeApplication.getStage().getScene().getRoot();
        
        final Button resumeButton = new Button(RESUME_BUTTON_TEXT);
        final Button quitButton = new Button(QUIT_BUTTON_TEXT);
        final Button helpMenuButton = new Button(HELP_MENU_BUTTON_TEXT);

        resumeButton.setOnAction( e -> resumeGame());
        quitButton.setOnAction( e -> quitGame());
        helpMenuButton.setOnAction( e -> openHelpMenu());

        resumeButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        resumeButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        quitButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        quitButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        helpMenuButton.prefHeightProperty().bind(root.heightProperty().divide(PAUSE_MENU_WIDGET_HEIGHT));
        helpMenuButton.prefWidthProperty().bind(root.widthProperty().divide(PAUSE_MENU_WIDGET_WIDTH));

        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(resumeButton, helpMenuButton,quitButton);
        this.setSpacing(PAUSE_MENU_SPACING);
    }

    /**
     * Opens the pause menu.
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
     * Opens the help menu.
     */
    private void openHelpMenu(){
        //TODO: probably do something here
    }



}