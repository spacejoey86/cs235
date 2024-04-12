package swan.g09.cs230a2;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

/**
 * @author Connor Milford
 * @version 1.0
 */

public class BarnacleEvent extends VBox {

    /** Message displayed on the event button */
    private static final String EVENT_BUTTON_TEXT = "You are trapped. " +
            "Click this 3 times to free yourself.";

    /** Height of the event button */
    private static final int EVENT_BUTTON_HEIGHT = 10;

    /** Width of the event button */
    private static final int EVENT_BUTTON_WIDTH = 5;

    /** Times the event button has been clicked */
    private int numClicks = 0;

    /** Times the event button needs to be clicked to end the event */
    private int endEventClicks = 3;

    /** True if the event has been won. */
    public static boolean eventWon;

    /** True if the event has been lost. */
    public static boolean eventLost;

    /** Time that the event must be completed by. */
    private final long endTime = System.currentTimeMillis() + 5000;

    /**
     * Constructor for the barnacle.
     */
    public BarnacleEvent() {
        eventWon = false;
        eventLost = false;

        final StackPane root = (StackPane) ChipsChallengeApplication.getStage()
                .getScene().getRoot();

        final Button eventButton = new Button(EVENT_BUTTON_TEXT);
        eventButton.setOnAction(e -> eventController());

        eventButton.prefHeightProperty().bind(root.heightProperty().divide(EVENT_BUTTON_HEIGHT));
        eventButton.prefWidthProperty().bind(root.widthProperty().divide(EVENT_BUTTON_WIDTH));

        this.setAlignment(Pos.CENTER);
        this.getChildren().add(eventButton);
    }

    /**
     * Controls what happens on button click,
     * checks if the button has been pressed 3 times and that the player has not run out of time.
     */
    private void eventController() {
        numClicks++;

        if (numClicks >= endEventClicks && !(System.currentTimeMillis() > endTime)) {
            ChipsChallengeApplication.endEvent();
            eventWon = true;
        }

        if (System.currentTimeMillis() > endTime) {
            ChipsChallengeApplication.endEvent();
            GameManager.endGame(GameManager.DeathState.BARNACLE);
            eventWon = false;
        }
    }
}
