package gui.controllers;

import client.Client;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Controller for the error Message for uploading invalid dictionary .txt file.
 *
 * @author vihofman
 */
public class DictionaryErrorMessageController {
  private Client client;

  /**
   * Sets client instance in CreateGameController, creates Server and connect first client.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
  }

  /**
   * Close the window.
   *
   * @param mouseEvent used to get current stage
   */
  public void closeError(MouseEvent mouseEvent) { // close the error message
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.close();
  }
}
