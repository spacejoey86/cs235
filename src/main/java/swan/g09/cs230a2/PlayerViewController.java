package swan.g09.cs230a2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;

import static swan.g09.cs230a2.HighScoresViewController.COLUMN_GAP;
import static swan.g09.cs230a2.HighScoresViewController.NAME_COLUMN_WIDTH;

/**
 * The UI controller for the players view.
 *
 * @author Barnaby Morley-Smith
 * @version 0.1
 */
public class PlayerViewController {

    /**
     * The player table UI element.
     */
    @FXML
    private TableView<PlayerProfile> playerTable;

    /**
     * The player name table column UI element.
     */
    @FXML
    private TableColumn<PlayerProfile, String> playerName;

    /**
     * The max unlocked level table column UI element.
     */
    @FXML
    private TableColumn<PlayerProfile, String> maxUnlockedLevel;

    /**
     * The name text input UI element.
     */
    @FXML
    private TextField nameInput;

    /**
     * Stores a potential autosave, if one is found.
     * */
    private static File candidateAutoSave;

    /**
     * Initialisation method for the Players view.
     */
    @FXML
    public void initialize() {
        fillPlayerTable();

        // Automatically size columns
        playerName.prefWidthProperty().bind(playerTable.widthProperty().multiply(NAME_COLUMN_WIDTH));
        maxUnlockedLevel.prefWidthProperty().bind(playerTable.widthProperty()
                .subtract(COLUMN_GAP).subtract(playerName.widthProperty()));
    }

    /**
     * Populates the player table with data.
     */
    private void fillPlayerTable() {
        try {
            PlayerProfileManager.loadProfileData();
        } catch (IOException e) {
            throw new RuntimeException("Error loading profile data!");
        }
        List<PlayerProfile> playerProfiles = PlayerProfileManager.getAllPlayerProfiles();
        playerName.setCellValueFactory(new PropertyValueFactory<PlayerProfile, String>("playerName"));
        maxUnlockedLevel.setCellValueFactory(new PropertyValueFactory<PlayerProfile, String>("maxUnlockedLevel"));
        playerTable.getItems().setAll(playerProfiles);
    }

    /**
     * Create a new player using the UI.
     * @param event The event from the action trigger.
     */
    @FXML
    private void createPlayer(ActionEvent event) {
        String name = nameInput.getText();
        nameInput.setText("");

        if (name.isEmpty() || PlayerProfileManager.hasPlayerProfile(name)) {
            return;
        }

        PlayerProfileManager.createPlayerProfile(name);
        fillPlayerTable();
    }

    /**
     * Delete a player using the UI.
     * @param event The event from the action trigger.@param event
     */
    @FXML
    private void deletePlayer(ActionEvent event) {
        event.consume();

        PlayerProfile player = playerTable.getSelectionModel().getSelectedItem();

        if (player == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + player.getPlayerName() + "?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            PlayerProfileManager.deletePlayerProfile(player.getPlayerName());
            try {
                PlayerProfileManager.savePlayerProfiles();
            } catch (IOException e) {
                throw new RuntimeException("Error saving player data!");
            }

            fillPlayerTable();
        }
    }

    /**
     * Select a player and navigate to the levels view.
     * @param event The event from the action trigger.
     */
    @FXML
    private void selectPlayer(ActionEvent event) {
        event.consume();

        PlayerProfile player = playerTable.getSelectionModel().getSelectedItem();

        if (player == null) {
            return;
        }

        GameManager.setPlayerProfile(player);

        if (checkForAutosave(player)) {
            // Evil String manipulation, barnaby may execute me for this and that would be justified.
            String levelName = candidateAutoSave.getName().split("-")[1]
                    .replaceAll("^level", "").replaceAll("\\.sav$", "");
            String alertText = String.format("Autosave for player %s found for level %s, "
                    + "do you wish to load this level?", player.getPlayerName(), levelName);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, alertText,
                    ButtonType.YES, new ButtonType("Delete", ButtonData.NO));
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                try {
                    GameManager.loadFromAutosave(candidateAutoSave.getPath());
                } catch (InputMismatchException e) {
                    return;
                }

                if (!GameManager.isLevelLoaded()) {
                    return;
                }

                try {
                    LevelDetails.readLevelIndex();
                    ChipsChallengeApplication.changeScene("game-view.fxml",
                            ChipsChallengeApplication.DEFAULT_VIEW_WIDTH,
                            ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, true);
                } catch (IOException e) {
                    System.out.println("Failed to load game view:\n" + e);
                }
            } else {
                candidateAutoSave.delete();
            }
        } else {
            try {
                ChipsChallengeApplication.changeScene("levels-view.fxml",
                        ChipsChallengeApplication.DEFAULT_VIEW_WIDTH,
                        ChipsChallengeApplication.DEFAULT_VIEW_HEIGHT, false);
            } catch (IOException e) {
                System.out.println("Failed to load level select view:\n" + e);
            }
        }
    }

    /**
     * Checks for an autosave under the player's profile.
     * @param p the player's profile
     * @return true if autosave exists
     */
    private boolean checkForAutosave(PlayerProfile p) {
        File appData = new File(PlayerProfileManager.getAppDataDirectory());
        File[] files = appData.listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (!file.isDirectory() && file.getPath().endsWith("sav")) {
                String fName = file.getName();
                if (fName.split("-")[0].equals(p.getPlayerName())) {
                    candidateAutoSave = file;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Navigate to the main view.
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

    /**
     * Delete the candidate auto save if it matches the supplied level number.
     * Called when winning or losing the level.
     * @param levelNumber The level that was won/lost.
     */
    public static void tryDeleteAutoSave(int levelNumber) {
        if (candidateAutoSave != null) {
            String levelName = candidateAutoSave.getName().split("-")[1].replaceAll("\\.sav$", "");
            if (levelName.equals("level" + levelNumber)) {
                candidateAutoSave.delete();
            }
        }
    }
}
