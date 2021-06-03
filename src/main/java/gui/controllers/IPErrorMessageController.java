package gui.controllers;

import client.Client;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

/**
 * Controller for the error Message for entering wrong IP at Joining game.
 *
 * @author vihofman
 */
public class IPErrorMessageController {
  private Client client;

  /** Set model method. */
  public void setModel(Client client) {
    this.client = client;
  }

  /** Closing the error message on click. */
  public void closeErrorMessage(MouseEvent mouseEvent) { // close the error message
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.close();
  }
}
