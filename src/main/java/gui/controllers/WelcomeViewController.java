package gui.controllers;

import client.Client;
import ft.Sound;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the WelcomeView.
 *
 * @author mnetzer
 */
public class WelcomeViewController {

  private Client client;

  /**
   * Sets client in JoinGameController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
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
   * Method to get to the playerLobby Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void continueToLobby(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/playerLobbyView.fxml"));
    Parent lobbyView = loader.load();
    PlayerLobbyController controller = loader.getController();
    controller.setModel(client);

    Scene lobbyScene = new Scene(lobbyView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(lobbyScene);
    window.show();
  }
}
