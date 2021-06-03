package gui.controllers;

import client.Client;
import ft.Sound;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Class for the join Game Controller.
 *
 * @author vihofman
 */
public class JoinGameController {
  @FXML private TextField ipField;
  private Client client;

  /**
   * Sets client in JoinGameController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
    ipField.setOnKeyPressed(
        new EventHandler<>() {
          @Override
          public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
              gameView();
              keyEvent.consume();
            }
          }
        });
  }

  /** InvalidTriggered when wrong IP has been entered. */
  public void showErrorMessage() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(this.getClass().getResource("/views/ipError.fxml"));
      Parent ipError = loader.load();
      Scene ipErrorScene = new Scene(ipError);
      Stage window = new Stage();
      window.initModality(Modality.APPLICATION_MODAL);
      window.setTitle("ipError");
      window.setScene(ipErrorScene);
      window.setWidth(210);
      window.setHeight(180);
      window.show();
      Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
      double x = bounds.getMinX() + (bounds.getWidth() - window.getWidth()) * 0.5;
      double y = bounds.getMinY() + (bounds.getHeight() - window.getHeight()) * 0.7;
      window.setX(x);
      window.setY(y);
      window.show();
    } catch (IOException i) {
      client.showError(i.getMessage());
    }
  }

  /** Triggered when 'Join' clicked. */
  @FXML
  public void gameView() {
    Sound.playMusic(Sound.tileSet);
    if (ipField.getText().matches("[0-9.]+")) {
      client.getNetClient().setIp(ipField.getText().trim());
      client.getNetClient().setJoinGameController(this);
      client.getNetClient().connect();

    } else {
      ipField.setStyle("-fx-border-color: red ; -fx-border-width: 2px ;");
      showErrorMessage();
    }
  }

  /** Triggered when message CONNECT received. */
  public void loadGameView() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/gameView.fxml"));
    Parent gameView = loader.load();
    GameViewController controller = loader.getController();
    controller.setModel(client);

    Scene gameScene = new Scene(gameView);
    Stage window = client.getStage();
    window.setScene(gameScene);
    window.show();
  }

  /**
   * Method to open the exit Screen in a new window.
   *
   * @throws IOException IOException
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
   * Method to get back to the PlayScrabble Screen.
   *
   * @author mnetzer
   * @param mouseEvent to detect the current Stage
   * @throws IOException IOException
   */
  public void backToPlayScrabble(MouseEvent mouseEvent) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/playScrabbleView.fxml"));
    Parent playScrabbleView = loader.load();
    PlayScrabbleController controller = loader.getController();
    controller.setModel(client);

    Scene playScrabbleScene = new Scene(playScrabbleView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(playScrabbleScene);
    window.show();
  }
}
