package gui.controllers;

import client.Client;
import ft.Sound;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the exitGameView.
 *
 * @author mnetzer
 */
public class ExitGameController {
  private Client client;

  /**
   * Sets client instance in ExitGameController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
  }

  /** Closes all windows and ends the game. */
  public void exitGame(MouseEvent mouseEvent) {
    Sound.playMusic(Sound.tileSet);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.close();
    if (client.getNetClient() != null && client.getNetClient().isHost()) {
      client.getNetClient().disconnect();
    }
    Platform.exit();
    System.exit(0);
  }

  /** Application will be continued only the exit window is closed. */
  public void cancelExit(MouseEvent mouseEvent) {
    Sound.playMusic(Sound.tileSet);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.close();
  }
}
