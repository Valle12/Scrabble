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
 * Controller for the playerScrabbleView.
 *
 * @author mnetzer
 */
public class PlayScrabbleController {

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
   * Method to get back to the playerLobby Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void backToPlayerLobby(MouseEvent mouseEvent) throws IOException {
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

  /**
   * Method to start playing the Tutorial.
   *
   * @param mouseEvent to detect the current stage
   * @throws IOException fxml file not found
   */
  public void playTutorial(MouseEvent mouseEvent) throws IOException {
    Sound.playMusic(Sound.tileSet);
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(this.getClass().getResource("/views/tutorial.fxml"));
    Parent tutorial = loader.load();
    TutorialController controller = loader.getController();
    controller.setModel(client);

    Scene tutorialScene = new Scene(tutorial);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(tutorialScene);
    window.show();
  }

  /**
   * Method to get to the CreateGame Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void createGame(MouseEvent mouseEvent) throws IOException {
    Sound.playMusic(Sound.tileSet);
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(this.getClass().getResource("/views/createGame.fxml"));
    Parent createGameView = loader.load();
    CreateGameController controller = loader.getController();
    controller.setModel(client);
    Scene createGameScene = new Scene(createGameView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(createGameScene);
    window.show();
  }

  /**
   * Method to get to the joinGame Screen.
   *
   * @param mouseEvent to detect the current Stage
   * @throws IOException fxml file not found
   */
  public void joinGame(MouseEvent mouseEvent) throws IOException {
    Sound.playMusic(Sound.tileSet);
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(this.getClass().getResource("/views/joinGameView.fxml"));
    Parent joinGameView = loader.load();
    JoinGameController controller = loader.getController();
    controller.setModel(client);

    Scene joinGameScene = new Scene(joinGameView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(joinGameScene);
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
}
