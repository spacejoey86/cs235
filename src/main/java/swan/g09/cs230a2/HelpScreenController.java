package swan.g09.cs230a2;

import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Controller for the help screen.
 */
public class HelpScreenController {

    /**
     * Flag to indicate if the help screen was opened from the pause menu.
     */
    private boolean openedFromPauseMenu;

    /**
     * Constructor for the help screen controller.
     */
    public HelpScreenController() {
        this.openedFromPauseMenu = false;
    }

    /**
     * Setter for the openedFromPauseMenu flag.
     *
     * @param openedFromPauseMenuFlag the flag to set
     */
    public void setOpenedFromPauseMenu(boolean openedFromPauseMenuFlag) {
        this.openedFromPauseMenu = openedFromPauseMenuFlag;
    }

    /**
     * Close the help screen.
     */
    @FXML
    public void closeHelp() {
        if (openedFromPauseMenu) {
            ChipsChallengeApplication.closeHelpMenu();
        } else {
            try {
                ChipsChallengeApplication.changeScene("main-view.fxml",
                        ChipsChallengeApplication.DEFAULT_VIEW_WIDTH,
                        ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

