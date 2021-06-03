package gui.controllers;

import client.Client;
import client.PlayerProfile;
import ft.Sound;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for the Game Results.
 *
 * @author mnetzer
 */
public class GameResultsController {
  @FXML private VBox chat;
  @FXML private TextArea textArea;
  @FXML private ScrollPane scrollPane;
  @FXML private Label player1;
  @FXML private Label player2;
  @FXML private Label player3;
  @FXML private Label player4;
  @FXML private Label pointsPlayer1;
  @FXML private Label pointsPlayer2;
  @FXML private Label pointsPlayer3;
  @FXML private Label pointsPlayer4;
  @FXML private ImageView image1;
  @FXML private ImageView image2;
  @FXML private ImageView image3;
  @FXML private ImageView image4;

  private Client client;
  public PlayerProfile player;
  private final Image defaultImage =
      new Image(getClass().getResourceAsStream("/pictures/ProfileIcon.png"));

  private PlayerProfile[] profiles;

  /** Initialize view with client and chat. */
  public void setModel(Client client) {
    this.client = client;
    this.client.getNetClient().setGameResultsController(this);
    updateChat();
  }

  /** Updates Scoreboard, called from NetClient if changes are made. */
  public void updateScoreboard(PlayerProfile[] profiles, int[] scores) {
    Label[] playerLabels = {player1, player2, player3, player4};
    Label[] pointsLabels = {pointsPlayer1, pointsPlayer2, pointsPlayer3, pointsPlayer4};
    ImageView[] images = {image1, image2, image3, image4};
    int tmpScore;

    // Sort Array from highest score to lowest
    for (int i = 0; i < profiles.length; i++) {
      for (int j = 0; j < profiles.length - 1; j++) {
        if (scores[j] < scores[j + 1]) {
          PlayerProfile tmpProf = profiles[j];
          tmpScore = scores[j];
          profiles[j] = profiles[j + 1];
          profiles[j + 1] = tmpProf;
          scores[j] = scores[j + 1];
          scores[j + 1] = tmpScore;
        }
      }
    }

    this.profiles = profiles;

    for (int i = 0; i < profiles.length; i++) {
      playerLabels[i].setText(profiles[i].getName());
      pointsLabels[i].setText(Integer.toString(scores[i]));
      if (profiles[i].getImage() != null) {
        images[i].setImage(profiles[i].getImage());
      } else {
        images[i].setImage(defaultImage);
      }
    }
    for (int i = profiles.length; i < 4; i++) {
      playerLabels[i].setText("---");
      pointsLabels[i].setText("-");
      images[i].setImage(defaultImage);
    }
  }

  /** Creates a listener for the TextArea so Enter can be pressed. */
  public void updateChat() {
    textArea.setOnKeyPressed(
        keyEvent -> {
          if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            textArea.deletePreviousChar();
            sendMessage();
            keyEvent.consume();
          }
        });
  }

  /** Load chat into current view. */
  public void loadChat(VBox messages) {
    chat.getChildren().add(messages);
  }

  /** Creates a Box/Label when player sends a message. Necessary to fill the ChatField. */
  public void sendMessage() {
    // falls leer --> nix
    if (textArea.getText().equals("")) {
      return;
    }

    HBox box = new HBox();
    box.setPrefHeight(Region.USE_COMPUTED_SIZE);
    box.setPrefWidth(Region.USE_COMPUTED_SIZE);
    box.setAlignment(Pos.BOTTOM_RIGHT);

    Text name = new Text(client.getSelectedProfile().getName());
    name.setFont(Font.font("Chalkboard", 14));
    name.setFill(Color.web("#170871"));

    Text message = new Text(textArea.getText());
    message.setFont(Font.font("Chalkboard", 14));
    message.setFill(Color.WHITE);

    TextFlow flowTemp = new TextFlow(name, new Text(System.lineSeparator()), message);

    Label text = new Label(null, flowTemp);
    text.setWrapText(true);
    text.setPadding(new Insets(2, 10, 2, 2));
    text.getStylesheets().add("stylesheets/chatstyle.css");
    text.getStyleClass().add("textBubble");

    box.getChildren().add(text);
    chat.setSpacing(20);
    chat.getChildren().add(box);
    chat.heightProperty().addListener(observer -> scrollPane.setVvalue(1.0));
    client.getNetClient().sendChatMessage(textArea.getText());

    textArea.clear();
  }

  /** Creates a Box/Label when player gets a message. Necessary to fill the ChatField. */
  public void getMessage(String username, String text) {
    HBox box = new HBox();
    box.setPrefHeight(Region.USE_COMPUTED_SIZE);
    box.setPrefWidth(Region.USE_COMPUTED_SIZE);
    box.setAlignment(Pos.BOTTOM_LEFT);

    Text name = new Text(username);
    name.setFont(Font.font("Chalkboard", 14));
    name.setFill(Color.BLACK);

    Text message = new Text(text);
    message.setFont(Font.font("Chalkboard", 14));
    message.setFill(Color.DARKGREY);

    TextFlow flowTemp = new TextFlow(name, new Text(System.lineSeparator()), message);

    Label label = new Label(null, flowTemp);
    label.setWrapText(true);
    label.setPadding(new Insets(2, 10, 2, 2));
    label.getStylesheets().add("stylesheets/chatstyle.css");
    label.getStyleClass().add("textBubbleFlipped");

    box.getChildren().add(label);
    chat.setSpacing(20);
    chat.getChildren().add(box);
    chat.heightProperty().addListener(observer -> scrollPane.setVvalue(1.0));
  }

  /** Creates a Box/Label to display system messages. Necessary to fill the ChatField. */
  public void createSystemMessage(String message) {
    HBox box = new HBox();
    box.setPrefHeight(Region.USE_COMPUTED_SIZE);
    box.setPrefWidth(Region.USE_COMPUTED_SIZE);
    box.setAlignment(Pos.BOTTOM_CENTER);

    Text text = new Text(message);
    text.setFont(Font.font("Chalkboard", 14));
    text.setFill(Color.DARKGREY);
    TextFlow flowTemp = new TextFlow(text);
    flowTemp.setTextAlignment(TextAlignment.CENTER);

    Label label = new Label(null, flowTemp);
    label.setWrapText(true);
    label.setPrefWidth(chat.getPrefWidth() * 0.8);
    label.setPadding(new Insets(1, 2, 1, 2));
    label.getStylesheets().add("stylesheets/chatstyle.css");
    label.getStyleClass().add("textBubbleSystem");

    box.getChildren().add(label);
    chat.setSpacing(20);
    chat.getChildren().add(box);
    chat.heightProperty().addListener(observer -> scrollPane.setVvalue(1.0));
  }

  /**
   * Method to open the exit Screen in a new window.
   *
   * @throws IOException fxml path not found.
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
   * Method to get back to the playerLobby Screen.
   *
   * @param mouseEvent to detect the current Stage.
   * @throws IOException fxml file not found.
   */
  public void backToPlayerLobby(MouseEvent mouseEvent) throws IOException {
    client.getNetClient().disconnect();
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/playerLobbyView.fxml"));
    Parent playerLobbyView = loader.load();
    PlayerLobbyController controller = loader.getController();
    Sound.playMusic(Sound.titleMusic);
    controller.setModel(client);

    Scene profileControllerScene = new Scene(playerLobbyView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(profileControllerScene);
    window.show();
  }

  /**
   * Open statistics of a player.
   *
   * @param profile player profile.
   * @throws IOException fxml file not found.
   */
  public void openStatistics(PlayerProfile profile) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/statistics.fxml"));
    Parent openStatistics = loader.load();
    StatisticsController controller = loader.getController();
    controller.setModel(profile);

    Scene openStatisticsScene = new Scene(openStatistics);
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("");
    window.setScene(openStatisticsScene);
    window.setWidth(300);
    window.setHeight(500);
    window.show();
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    double x = bounds.getMinX() + (bounds.getWidth() - window.getWidth()) * 0.2;
    double y = bounds.getMinY() + (bounds.getHeight() - window.getHeight()) * 0.45;
    window.setX(x);
    window.setY(y);
    window.show();
  }

  /**
   * Open statistics of a player.
   *
   * @throws IOException fxml file not found.
   */
  public void playerOne() throws IOException {
    openStatistics(profiles[0]);
  }

  /**
   * Open statistics of a player.
   *
   * @throws IOException fxml file not found.
   */
  public void playerTwo() throws IOException {
    if (profiles.length > 1) {
      openStatistics(profiles[1]);
    }
  }

  /**
   * Open statistics of a player.
   *
   * @throws IOException fxml file not found.
   */
  public void playerThree() throws IOException {
    if (profiles.length > 2) {
      openStatistics(profiles[2]);
    }
  }

  /**
   * Open statistics of a player.
   *
   * @throws IOException fxml file not found.
   */
  public void playerFour() throws IOException {
    if (profiles.length > 3) {
      openStatistics(profiles[3]);
    }
  }
}
