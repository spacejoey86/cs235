package swan.g09.cs230a2;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;

public class BarnacleEvent extends VBox {

    private static final String EVENT_BUTTON_TEXT = "You are trapped. " +
            "Click this 3 times to free yourself.";

    private static final int EVENT_BUTTON_HEIGHT = 10;

    private static final int EVENT_BUTTON_WIDTH = 10;

    private int numClicks = 0;

    private int endEventClicks = 3;

    public static boolean eventWon;
    public static boolean eventLost;
    private final long endTime = System.currentTimeMillis() + 5000;

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

    private void eventController() {
        numClicks++;
        System.out.println(System.currentTimeMillis()+ " " + endTime );
        
        if (numClicks >= endEventClicks && !(System.currentTimeMillis() > endTime)) {
            ChipsChallengeApplication.endEvent();
            eventWon = true;
        }

        if (System.currentTimeMillis() > endTime){
            ChipsChallengeApplication.endEvent();
            GameManager.endGame(GameManager.DeathState.BARNACLE);
            eventWon = false;
        }
            
    }



}
