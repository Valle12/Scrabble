package gui.controllers;

import client.Client;
import client.PlayerProfile;
import ft.Sound;
import game.components.BoardField;
import game.components.Tile;
import game.players.LocalPlayer;
import gui.components.Rack;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
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
import javafx.util.Duration;

/**
 * Controller for the GameView.
 *
 * @author mnetzer
 */
public class GameViewController implements Initializable {

  @FXML private GridPane board;
  @FXML private GridPane tiles;
  @FXML private Label player1;
  @FXML private Label player2;
  @FXML private Label player3;
  @FXML private Label player4;
  @FXML private Label pointsPlayer1;
  @FXML private Label pointsPlayer2;
  @FXML private Label pointsPlayer3;
  @FXML private Label pointsPlayer4;
  @FXML private Label bag;
  @FXML private Label turn1;
  @FXML private Label turn2;
  @FXML private Label turn3;
  @FXML private Label turn4;
  @FXML private ImageView image1;
  @FXML private ImageView image2;
  @FXML private ImageView image3;
  @FXML private ImageView image4;
  @FXML private Label time;
  @FXML private TextArea textArea;
  @FXML private ScrollPane scrollPane;
  @FXML private VBox chat;
  @FXML private CheckBox ready;
  @FXML private VBox agreements;
  @FXML private Button finishGame;
  @FXML private Button submitButton;

  private final Image defaultImage =
      new Image(getClass().getResourceAsStream("/pictures/ProfileIcon.png"));

  private Client client;
  private LocalPlayer player;
  private int draggedTileIndex = -1;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    Tooltip tip = new Tooltip();
    tip.setText("There are 0 tiles in the bag");
    tip.setShowDelay(Duration.seconds(0.1));
    tip.setFont(Font.font("Chalkboard"));
    bag.setTooltip(tip);
    agreements.managedProperty().bind(agreements.visibleProperty());
    finishGame.setVisible(false);
    finishGame.managedProperty().bind(finishGame.visibleProperty());
  }

  /**
   * Sets client instance in GameViewController, builds graphics of board, rack, chat and time.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
    this.player = client.initLocalPlayer(this);
    client.getNetClient().setGameViewController(this);
    updateBoard();
    updateRack();
    updateChat();
    updateTime(600000L);
    showAgreements(!client.getNetClient().isHost()); // show agreements if not host
  }

  /** Getter method to get the client. */
  public Client getClient() {
    return client;
  }

  /**
   * Update the graphics of the board with instances of PlayerProfiles/Board. Called after each
   * move. AnchorPane as graphical container for the tiles are created.
   */
  public void updateBoard() {
    board.getChildren().clear();
    for (int i = 0; i < 15; i++) {
      for (int j = 0; j < 15; j++) {
        AnchorPane anchorPane = createTile(player.getBoard().getField(j, i));
        board.add(anchorPane, i, j);
      }
    }
  }

  /** Updates Scoreboard, called from NetClient if changes are made. */
  public void updateScoreboard(PlayerProfile[] profiles, int[] scores) {
    Label[] playerLabels = {player1, player2, player3, player4};
    Label[] pointsLabels = {pointsPlayer1, pointsPlayer2, pointsPlayer3, pointsPlayer4};
    ImageView[] images = {image1, image2, image3, image4};

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
    submitButton.setText("Pass");
  }

  /** Updates Rack, called after each move the LocalPlayer makes. */
  public void updateRack() {
    tiles.getChildren().clear();
    for (int i = 0; i < Rack.RACK_SIZE; i++) {
      if (player.getRack().isEmpty(i)) {
        continue;
      }
      AnchorPane anchorPane =
          createBottomTile(
              player.getRack().getTile(i).getLetter(), player.getRack().getTile(i).getScore(), i);
      tiles.add(anchorPane, i, 0);
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

  /** Copy existing chat messages from CreateGameView to the GameView. */
  public void loadChatFromHost(VBox messages) {
    chat.getChildren().add(messages);
  }

  /** Update Remaining Time for the current move. */
  public void updateTime(long milliseconds) {
    String minutes = String.format("%02d", milliseconds / 1000 / 60);
    String seconds = String.format("%02d", (milliseconds / 1000) % 60);
    time.setText(minutes + ":" + seconds);
    if (milliseconds < 60000) { // less than one minute left
      time.setStyle("-fx-font-family: Chalkboard; -fx-text-fill: red; -fx-font-weight: bold");
    }
  }

  /** Shows and hides the agreement pane. */
  public void showAgreements(boolean show) {
    agreements.setVisible(show);
  }

  /** Shows the Finish game button. */
  public void showEndButton() {
    finishGame.setVisible(true);
  }

  /**
   * Sets bag size.
   *
   * @param bagSize Requires size bag should have
   */
  public void setBagSize(int bagSize) {
    player.setBagSize(bagSize);
    Tooltip tip = bag.getTooltip();
    tip.setText("There are " + bagSize + " tiles in the game bag");
    tip.setFont(Font.font("Chalkboard"));
  }

  /**
   * Open Statistics.
   *
   * @author vihofman
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

    window.setTitle(profile.getName());
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

  /** Show statistics of player one. */
  public void playerOne() throws IOException {
    openStatistics(client.getNetClient().getCoPlayers()[0]);
  }

  /** Show statistics of player two. */
  public void playerTwo() throws IOException {
    if (client.getNetClient().getCoPlayers().length >= 2) {
      openStatistics(client.getNetClient().getCoPlayers()[1]);
    }
  }

  /** Show statistics of player three. */
  public void playerThree() throws IOException {
    if (client.getNetClient().getCoPlayers().length >= 3) {
      openStatistics(client.getNetClient().getCoPlayers()[2]);
    }
  }

  /** Show statistics of player four. */
  public void playerFour() throws IOException {
    if (client.getNetClient().getCoPlayers().length >= 4) {
      openStatistics(client.getNetClient().getCoPlayers()[3]);
    }
  }

  /**
   * Returns to player Lobby and disconnects client.
   *
   * @param mouseEvent Requires MouseEvent generated on Click
   * @throws IOException fxml file could not be loaded
   */
  public void backToPlayerLobby(MouseEvent mouseEvent) throws IOException {
    Sound.playMusic(Sound.tileSet);
    String alertMessage =
        "Are you sure you want to quit a running game?\n\nYour Co-Players would be very disappointed...";
    Alert alert =
        new Alert(Alert.AlertType.CONFIRMATION, alertMessage, ButtonType.YES, ButtonType.CANCEL);
    alert.setHeaderText(null);
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog");
    alert.showAndWait();

    if (alert.getResult() != ButtonType.YES) { // if not yes then leave method
      return;
    }
    client.getNetClient().disconnect();
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.titleMusic);
    loader.setLocation(this.getClass().getResource("/views/playerLobbyView.fxml"));
    Parent playerLobbyView = loader.load();
    PlayerLobbyController controller = loader.getController();
    controller.setModel(client);

    Scene profileControllerScene = new Scene(playerLobbyView);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.setScene(profileControllerScene);
    window.show();
  }

  /** Finishes the game and redirects the players to the ResultView. */
  public void finishGame() {

    Alert alert =
        new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to finish the game?\n",
            ButtonType.YES,
            ButtonType.CANCEL);
    alert.setHeaderText(null);
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog");
    alert.showAndWait();

    if (alert.getResult() != ButtonType.YES) { // if not yes then leave method
      Sound.playMusic(Sound.finishSound);
      return;
    }
    client.getNetClient().sendEndMessage(1);
  }

  /**
   * Redirects to the ResultView.
   *
   * @throws IOException fxml file not found
   */
  public void changeToResultView() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.finishSound);
    loader.setLocation(this.getClass().getResource("/views/gameResults.fxml"));
    Parent gameResults = loader.load();
    GameResultsController controller = loader.getController();
    controller.setModel(client);
    controller.loadChat(chat);

    Scene gameResultScene = new Scene(gameResults);
    Stage window = client.getStage();
    window.setScene(gameResultScene);
    window.show();
  }

  /** Exchange selected tiles. */
  public void tiles() {
    player.exchange();
  }

  /** Shuffles the tiles on the rack. */
  public void shuffle() {
    Sound.playMusic(Sound.tileSet);
    player.getRack().shuffleRack();
    updateRack();
  }

  /** Submit moved tiles. */
  @FXML
  public void submit() {
    Sound.playMusic(Sound.tileSet);
    player.submit();
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
   * Method to create the Containers for the tiles on the Rack includes graphical components and
   * adds the Drag and Drop feature.
   *
   * @param letter,value,position Used letter with value and position
   */
  public AnchorPane createBottomTile(char letter, int value, int position) {
    AnchorPane pane;
    Label label = new Label();
    Label points;

    label.setText("" + letter);

    // Tooltip if Joker
    if (letter == '#') {
      Tooltip tip = new Tooltip("This is a joker tile");
      tip.setFont(Font.font("Chalkboard"));
      tip.setShowDelay(Duration.millis(1500));
      tip.setShowDuration(Duration.millis(3000));
    }

    AnchorPane.setTopAnchor(label, 1.0);
    AnchorPane.setLeftAnchor(label, 1.0);
    AnchorPane.setRightAnchor(label, 1.0);
    AnchorPane.setBottomAnchor(label, 1.0);
    label.setAlignment(Pos.CENTER);
    label.getStylesheets().add("/stylesheets/labelstyle.css");
    if (player.tileSelected(position)) {
      label.getStyleClass().add("tileBottomSelected");
    } else {
      label.getStyleClass().add("tileBottom");
    }

    points = new Label();
    points.setText(Integer.toString(value));
    AnchorPane.setTopAnchor(points, 1.0);
    AnchorPane.setLeftAnchor(points, 5.0);
    AnchorPane.setRightAnchor(points, 1.0);
    AnchorPane.setBottomAnchor(points, 1.0);
    points.setAlignment(Pos.BOTTOM_LEFT);
    points.getStylesheets().add("/stylesheets/labelstyle.css");
    points.getStyleClass().add("digitRack");

    pane = new AnchorPane();
    pane.getChildren().add(label);
    pane.getChildren().add(points);

    pane.setOnDragDetected(
        mouseEvent -> {
          String exchange = label.getText() + points.getText();
          Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
          draggedTileIndex = position;
          ClipboardContent content = new ClipboardContent();
          content.putString(exchange);
          db.setContent(content);
          mouseEvent.consume();
        });

    pane.setOnDragDone(
        event -> {
          /* the drag-and-drop gesture ended */
          if (event.isDropCompleted()) {
            draggedTileIndex = -1;
          }

          event.consume();
        });

    pane.setOnMouseClicked(
        event -> {
          player.selectTile(position);
          updateBottomTile(letter, value, position);
          event.consume();
        });

    pane.setStyle("-fx-cursor: hand");

    return pane;
  }

  /**
   * Method to create the Containers for the tiles on the Board includes graphical components and
   * adds the Drag and Drop feature.
   *
   * @param boardField with the information about the tile
   * @return AnchorPane
   */
  public AnchorPane createTile(BoardField boardField) {
    AnchorPane pane;
    Label label = new Label();
    Label points = new Label();

    if (!boardField.isEmpty()) {
      label.setText(String.valueOf(boardField.getTile().getLetter()));
    } else {
      label.setText("");
    }
    AnchorPane.setTopAnchor(label, 1.0);
    AnchorPane.setLeftAnchor(label, 1.0);
    AnchorPane.setRightAnchor(label, 1.0);
    AnchorPane.setBottomAnchor(label, 1.0);
    label.setAlignment(Pos.CENTER);
    label.getStylesheets().add("/stylesheets/labelstyle.css");
    label.getStyleClass().add("tile");

    if (!boardField.isEmpty()) {
      points.setText(Integer.toString(boardField.getTile().getScore()));
    } else {
      points.setText("");
    }
    AnchorPane.setTopAnchor(points, 1.0);
    AnchorPane.setLeftAnchor(points, 5.0);
    AnchorPane.setRightAnchor(points, 1.0);
    AnchorPane.setBottomAnchor(points, 1.0);
    points.setAlignment(Pos.BOTTOM_LEFT);
    points.getStylesheets().add("/stylesheets/labelstyle.css");
    points.getStyleClass().add("digitTile");

    // Assignment of different styles of the field to the labels
    if (!boardField.isEmpty()) {
      label.getStyleClass().add("tileWithLetter");
      if (player
          .getPlacements()
          .contains(
              boardField)) { // tile is in placements of player this turn --> change border color
        label.setBorder(
            new Border(
                new BorderStroke(
                    Color.YELLOW,
                    BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY,
                    BorderWidths.DEFAULT)));
      }
    } else {
      if (boardField.getType() == BoardField.FieldType.DWS) {
        label.getStyleClass().add("tileDWS");
      }
      if (boardField.getType() == BoardField.FieldType.TWS) {
        label.getStyleClass().add("tileTWS");
      }
      if (boardField.getType() == BoardField.FieldType.DLS) {
        label.getStyleClass().add("tileDLS");
      }
      if (boardField.getType() == BoardField.FieldType.TLS) {
        label.getStyleClass().add("tileTLS");
      }
      if (boardField.getType() == BoardField.FieldType.STAR) {
        label.getStyleClass().add("tileStar");
      }
    }

    pane = new AnchorPane();
    pane.getChildren().add(label);
    pane.getChildren().add(points);

    pane.setOnDragOver(
        event -> {
          /* accept it only if it is  not dragged from the same node
           * and if it has a string data */
          if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
          }

          event.consume();
        });

    pane.setOnDragDropped(
        event -> {
          Dragboard db = event.getDragboard();
          boolean success = false;
          if (db.hasString()) {
            updateTile(
                db.getString().charAt(0),
                Integer.parseInt(db.getString().substring(1)),
                boardField.getRow(),
                boardField.getColumn());
            success = true;
          }
          /* let the source know whether the string was successfully
           * transferred and used */
          event.setDropCompleted(success);

          event.consume();
        });

    pane.setOnMouseClicked(
        event -> {
          if (!boardField.isEmpty()) {
            player.removePlacement(boardField.getRow(), boardField.getColumn());
            updateBoard();
            updateRack();
          }
        });

    // Color border yellow when entered
    pane.setOnDragEntered(
        event ->
            pane.setBorder(
                new Border(
                    new BorderStroke(
                        Color.YELLOW,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        BorderWidths.DEFAULT))));

    // Remove border
    pane.setOnDragExited(event -> pane.setBorder(null));

    return pane;
  }

  /** Method to update the graphical container of a Tile on the board. */
  public void updateTile(char letter, int points, int row, int col) {
    if (!player.getBoard().isEmpty(row, col)) {

    } else {
      player.placeTile(draggedTileIndex, row, col);
      AnchorPane pane = createTile(player.getBoard().getField(row, col));
      board.add(pane, col, row);
    }
    updateBoard();
    updateRack();
  }

  /** Method to update the graphical container of a Tile on the rack. */
  public void updateBottomTile(char letter, int points, int col) {
    AnchorPane pane = createBottomTile(letter, points, col);
    tiles.add(pane, col, 0);
  }

  /** Method to update the graphical containers of he board after a move. */
  public void placeTile(Tile tile, int row, int col) {
    player.getBoard().placeTile(tile, row, col);
    updateBoard();
    if (player.isTurn()) {
      if (tile != null) {
        submitButton.setText("Submit");
      } else {
        if (player.getPlacements().size() == 0) {
          submitButton.setText("Pass");
        }
      }
    }
  }

  /**
   * Shows a Popup with a given message.
   *
   * @param message content of Popup
   */
  public void showPopup(String message) {
    client.showPopUp(message);
  }

  /** Requests tile distribution from server. */
  @FXML
  public void showTileDistribution() {
    client.getNetClient().requestDistributions();
  }

  /** Requests tile distribution from server. */
  @FXML
  public void showTileValues() {
    client.getNetClient().requestValues();
  }

  /** Requests tile distribution from server. */
  @FXML
  public void showDictionary() {
    client.getNetClient().requestDictionary();
  }

  /** Triggered when checkBox 'I am ready' is selected/unselected. */
  @FXML
  public void toggleReadyState() {
    client.getNetClient().setPlayerReady(ready.isSelected());
  }

  /** Triggered by incoming TURNMESSAGE Makes turn labels (green circle) visible/invisible. */
  @FXML
  public void updateTurns(boolean[] turns) {
    Label[] turnLabels = {turn1, turn2, turn3, turn4};
    for (int i = 0; i < turnLabels.length; i++) {
      turnLabels[i].setVisible((i < turns.length) && turns[i]);
    }

    updateBoard(); // reload board to remove self-placed tiles
  }
}
