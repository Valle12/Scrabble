package gui.controllers;

import client.Client;
import ft.Sound;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the playerLobbyView.
 *
 * @author mnetzer
 */
public class PlayerLobbyController {

  @FXML private Label username;
  @FXML private ImageView profileImage;

  private Client client;

  /**
   * Sets client in JoinGameController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
    if (client.getSelectedProfile() != null) {
      username.setText(client.getSelectedProfile().getName());
      if (client.getSelectedProfile().getImage() != null) {
        profileImage.setImage(client.getSelectedProfile().getImage());
      }
    }
  }

  /**
   * Method to get back to the PlayScrabble Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void playScrabble(MouseEvent mouseEvent) throws IOException {
    Sound.playMusic(Sound.tileSet);
    FXMLLoader loader = new FXMLLoader();

    loader.setLocation(this.getClass().getResource("/views/playScrabbleView.fxml"));
    Parent playScrabbleView = loader.load();
    PlayScrabbleController controller = loader.getController();
    controller.setModel(client);

    Scene playScrabbleScene = new Scene(playScrabbleView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(playScrabbleScene);
    window.show();
  }

  /**
   * Method to get to the Settings Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void settings(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/settings.fxml"));
    Parent settings = loader.load();
    SettingsController controller = loader.getController();
    controller.setModel(client);

    Scene settingsScene = new Scene(settings);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(settingsScene);
    window.show();
  }

  /**
   * Method to get back to the WelcomeView Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void backToLogin(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/welcomeView.fxml"));
    Parent welcomeView = loader.load();
    WelcomeViewController controller = loader.getController();
    controller.setModel(client);

    Scene welcomeScene = new Scene(welcomeView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(welcomeScene);
    window.show();
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

  /**
   * Method to get to the playerProfile Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void changeToProfileView(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/playerProfileView.fxml"));
    Parent profileControllerView = loader.load();
    PlayerProfileController controller = loader.getController();
    controller.setModel(client);

    Scene profileControllerScene = new Scene(profileControllerView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(profileControllerScene);
    window.show();
  }
}
