package gui.controllers;

import client.Client;
import client.PlayerProfile;
import ft.Sound;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the playerProfileView.
 *
 * @author mnetzer
 */
public class PlayerProfileController {

  @FXML private Label playerNo;
  @FXML private Label playerName;
  @FXML private Label playerTotalPoints;
  @FXML private Label playerCurrentHighscore;
  @FXML private Label playerPlayedGames;
  @FXML private Label playerWins;
  @FXML private Label playerLosses;
  @FXML private Label playerScrabblerSince;
  @FXML private ImageView profileImage;

  private Client client;
  private List<PlayerProfile> profiles;
  private int selectedIdx = 0;

  /**
   * Sets client in JoinGameController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
    this.profiles = client.getPlayerProfiles();
    if (profiles.size() > 0 && client.getSelectedProfile() != null) {
      selectedIdx = profiles.indexOf(client.getSelectedProfile());
      showPlayer();
    }
  }

  /** Update statistics according to the last selected player in the list. */
  public void showPlayer() {
    playerNo.setText(String.valueOf(selectedIdx + 1));
    playerName.setText(profiles.get(selectedIdx).getName());
    playerTotalPoints.setText(String.valueOf(profiles.get(selectedIdx).getTotalScore()));
    playerCurrentHighscore.setText(String.valueOf(profiles.get(selectedIdx).getHighScore()));
    playerPlayedGames.setText(profiles.get(selectedIdx).getPlayedGames() + " Game(s)");
    playerWins.setText(String.valueOf(profiles.get(selectedIdx).getWins()));
    playerLosses.setText(String.valueOf(profiles.get(selectedIdx).getLosses()));
    playerScrabblerSince.setText(profiles.get(selectedIdx).getCreation());
    if (profiles.get(selectedIdx).getImage() != null) {
      profileImage.setImage(profiles.get(selectedIdx).getImage());
    } else {
      profileImage.setImage(new Image(getClass().getResourceAsStream("/pictures/ProfileIcon.png")));
    }
  }

  /**
   * Method to get back to the playerLobby Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void backToLogin(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/playerLobbyView.fxml"));
    Parent playerLobbyView = loader.load();
    PlayerLobbyController controller = loader.getController();
    controller.setModel(client);

    Scene profileControllerScene = new Scene(playerLobbyView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(profileControllerScene);
    window.show();
  }

  /** Shows prompt to edit the name of the currently selected profile. */
  public void editProfile() {
    TextInputDialog td = new TextInputDialog();
    Sound.playMusic(Sound.tileSet);
    td.setTitle("Edit Profile");
    td.setHeaderText("Enter new name of profile");
    td.setContentText("Name: ");
    DialogPane dialogPane = td.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog");
    Optional<String> result = td.showAndWait();
    result.ifPresent(
        name -> {
          profiles.get(selectedIdx).setName(name);
          showPlayer();
          client.savePlayerProfiles();
        });
  }

  /**
   * Method to open the exit Screen in a new window.
   *
   * @throws IOException fxml file not found
   */
  public void exitGame() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/exitGame.fxml"));
    Parent exitGameView = loader.load();
    ExitGameController controller = loader.getController();
    controller.setModel(client);

    Scene exitGameScene = new Scene(exitGameView);
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("Exit Game");
    window.setScene(exitGameScene);
    window.setWidth(300);
    window.setHeight(200);
    window.showAndWait();
  }

  /** Shows the previous playerProfile in the list. Jumps from the beginning to the end. */
  public void previousPlayer() {
    Sound.playMusic(Sound.tileSet);
    selectedIdx = Math.abs((selectedIdx - 1) % profiles.size());
    client.setSelectedProfile(profiles.get(selectedIdx));
    showPlayer();
  }

  /** Shows the next playerProfile in the list. Jumps from the end to the beginning. */
  public void nextPlayer() {
    Sound.playMusic(Sound.tileSet);
    selectedIdx = (selectedIdx + 1) % profiles.size();
    client.setSelectedProfile(profiles.get(selectedIdx));
    showPlayer();
  }

  /** Shows prompt for the name of the new profile and creates the profile. */
  public void createNewProfile() {
    TextInputDialog td = new TextInputDialog();
    td.setTitle("Create New Profile");
    td.setHeaderText("Enter name of new profile");
    td.setContentText("Name: ");
    DialogPane dialogPane = td.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog");
    Optional<String> result = td.showAndWait();
    result.ifPresent(
        name -> {
          profiles.add(new PlayerProfile(name, 0, 0, 0, 0, LocalDate.now(), LocalDate.now()));
          selectedIdx = profiles.size() - 1;
          showPlayer();
          client.setSelectedProfile(profiles.get(selectedIdx));
          client.savePlayerProfiles();
          Sound.playMusic(Sound.playerCreated);
        });
  }

  /** Deletes the selected profile. Last one cannot be deleted. */
  public void deleteProfile() {
    Sound.playMusic(Sound.tileSet);
    if (profiles.size() <= 1) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Minimum Profile");
      alert.setHeaderText(null);
      alert.setContentText("You cannot delete your last profile!");
      DialogPane dialogPane = alert.getDialogPane();
      dialogPane
          .getStylesheets()
          .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
      dialogPane.getStyleClass().add("dialog");
      alert.showAndWait();
    } else {
      profiles.remove(profiles.get(selectedIdx));
      selectedIdx = Math.abs((selectedIdx - 1) % profiles.size());
      showPlayer();
      client.setSelectedProfile(profiles.get(selectedIdx));
      client.savePlayerProfiles();
    }
  }

  /** Choose file to change the profile image. */
  @FXML
  public void changeImage() {
    FileChooser chooser = new FileChooser();
    Sound.playMusic(Sound.tileSet);
    // Extention filter
    FileChooser.ExtensionFilter extentionFilter =
        new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *jpeg)", "*.png;*.jpg;*.jpeg");
    chooser.getExtensionFilters().add(extentionFilter);
    File file = new File(System.getProperty("user.dir"));
    chooser.setInitialDirectory(file);

    File chosenFile = chooser.showOpenDialog(null);
    if (chosenFile != null) {
      profiles.get(selectedIdx).setImage(new Image(chosenFile.toURI().toString()));
    }
    client.savePlayerProfiles();
    showPlayer();
  }
}
