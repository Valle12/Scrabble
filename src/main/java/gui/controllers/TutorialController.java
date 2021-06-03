package gui.controllers;

import client.Client;
import ft.Sound;
import game.components.Board;
import game.components.BoardException;
import game.components.BoardField;
import game.components.Tile;
import game.Dictionary;
import gui.components.Rack;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller tor the Tutorial.
 *
 * @author vihofman
 */
public class TutorialController {
  @FXML private Label instructions;
  @FXML private Label stepOverview;
  @FXML private VBox tutorialWelcome;
  @FXML private GridPane board;
  @FXML private Label pointsPlayer1;
  @FXML private Label pointsPlayer2;
  @FXML private GridPane tiles;
  @FXML private Label errorMessage;
  @FXML private Label endTutorial;
  private Client client;
  private int counter = 0;
  private int draggedTileIndex = -1;
  private Rack rack;
  private final List<BoardField> placements = new ArrayList<>();
  private final Dictionary dictionary = new Dictionary();
  private Board gameBoard;

  private final int[] tileScores = {
    1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
  };

  /**
   * This array consists of all the advice given by the tutorial. The content of the indices will be
   * printed out onto the instructions label.
   */
  private final String[] steps = {
    "Place the tiles from your rack onto the board so they form a word."
        + "\n"
        + "You can place tiles either vertically or horizontally, always joining the cluster of tiles already placed."
        + "\n"
        + "When a correct word has been placed, click submit and earn points."
        + "\n"
        + "Start by placing 'HELLO' over the star icon in the middle."
        + "\n"
        + "To make a move, click and drag & drop the tile from your rack onto the board."
        + "\n"
        + "To undo a placement, simply click onto the tile.",
    "If a blank tile (joker) is played the player has to define for what letter the blank tile stands for and it remains "
        + "to stand for this letter throughout the game. "
        + "\n"
        + "\n"
        + "The blank tile scores zero points."
        + "\n"
        + "\n"
        + "Now please try to place 'FRIENDS' using the joker as the letter F and using the already placed R.",
    "To get some creativity boost, please click the Shuffle-Button to rearrange the order of tiles in your rack."
        + "\n"
        + "\n"
        + "Now place your word please. "
        + "\n"
        + "\n"
        + "Tip: It's the No 1 food students eat",
    "If you want to exchange your tiles randomly with the ones left in the bag, click the tiles you want to exchange and press the Exchange-Button."
        + "\n"
        + "\n"
        + "Please select all the tiles and press Exchange. New letters are added to your rack, NICE! Now your turn ends."
        + "\n"
        + "\n"
        + "If you can not find a valid word, click the Submit-Button to pass your turn."
        + "\n"
        + "\n"
        + "From here on please press 'Next step'",
    "Not only tiles have different values, but the board colors also increase your score."
        + "\n"
        + "\n"
        + "Light Blue: doubles letter value"
        + "\n"
        + "Dark Blue: triples letter value"
        + "\n"
        + "Pink: doubles the word value"
        + "\n"
        + "Red: triples the word value"
        + "\n"
        + "\n"
        + "Words crossing the middle tile are doubled in value",
    "Premium squares apply only when newly placed tiles cover them. Any subsequent plays do not count those premium squares."
        + "\n"
        + "\n"
        + "If a player covers both letter and word premium squares with a single word, the letter premium(s) is/are calculated first, followed by the word premium(s)."
        + "\n"
        + "\n"
        + "If seven tiles have been laid on the board in one turn after all of the words formed have been scored, 50 bonus points are added. (Bingo)",
    "The timer in the top left corner indicates the amount of time left for you to finish you turn."
        + "\n"
        + "\n"
        + "Every turn can last up to 10 minutes, if exceeded the game ends."
        + "\n"
        + "\n"
        + "The game finishes as well, if all the tiles from a players rack and the bag have been removed or "
        + "\n"
        + "\n"
        + "within 6 rounds nothing has been placed and one of the players decides to leave."
  };

  private boolean stageTwoUnlocked;
  private boolean stageThreeUnlocked;
  private boolean stageFourUnlocked;

  /**
   * Sets client in TutorialController.
   *
   * @param client Requires client to be set
   */
  public void setModel(Client client) {
    this.client = client;
    gameBoard = new Board();
    rack = new Rack();
    showTutorialWelcome(true);
    showErrorMessage(false);
    showEndTutorial(false);
    instructions.setVisible(false);
    stepOverview.setVisible(false);
  }

  /** Learns how to place tiles. */
  public void loadStageOne() {
    showErrorMessage(false);
    instructions.setVisible(true);
    stepOverview.setVisible(true);
    // give player tiles HELLO
    rack.add(new Tile('L', tileScores['L' - 65]));
    rack.add(new Tile('L', tileScores['L' - 65]));
    rack.add(new Tile('O', tileScores['O' - 65]));
    gameBoard.placeTile(new Tile('H', tileScores['H' - 65]), 6, 7);
    gameBoard.placeTile(new Tile('E', tileScores['E' - 65]), 7, 7);
    instructions.setText(steps[0]);

    updateBoard();
    updateRack();
  }

  /** Learns how to use the joker. */
  public void loadStageTwo() {
    showErrorMessage(false);
    Sound.playMusic(Sound.rightWord);
    if (!stageTwoUnlocked) {
      showErrorMessage(true);
      return;
    }
    counter++;
    updateScoreboard(1, 16); // 1 für player 2 für bot
    updateScoreboard(2, 5);

    for (int i = 0; i < Rack.RACK_SIZE; i++) {
      rack.remove(i);
    }
    rack.add(new Tile('#', 0));
    rack.add(new Tile('I', tileScores['I' - 65]));
    rack.add(new Tile('E', tileScores['E' - 65]));
    rack.add(new Tile('D', tileScores['D' - 65]));
    rack.add(new Tile('N', tileScores['N' - 65]));
    rack.add(new Tile('S', tileScores['S' - 65]));

    gameBoard.placeTile(new Tile('D', tileScores['D' - 65]), 7, 6);
    gameBoard.placeTile(new Tile('A', tileScores['A' - 65]), 7, 8);
    gameBoard.placeTile(new Tile('R', tileScores['R' - 65]), 7, 9);

    placements.clear();
    placements.add(gameBoard.getField(7, 6));
    try {
      gameBoard.check(placements, dictionary, true);
    } catch (BoardException e) {
      e.printStackTrace();
    }
    placements.clear();

    instructions.setText(steps[1]);
    stepOverview.setText(counter + 1 + "/7");

    updateBoard();
    updateRack();
  }

  /** Learns how to Shuffle the tiles. */
  public void loadStageThree() {
    showErrorMessage(false);
    Sound.playMusic(Sound.rightWord);
    if (!stageThreeUnlocked) {
      showErrorMessage(true);
      return;
    }
    counter++;
    updateScoreboard(1, 25); // 1 für player 2 für bot
    updateScoreboard(2, 25);

    for (int i = 0; i < Rack.RACK_SIZE; i++) {
      rack.remove(i);
    }
    rack.add(new Tile('O', tileScores['O' - 65]));
    rack.add(new Tile('D', tileScores['D' - 65]));
    rack.add(new Tile('L', tileScores['L' - 65]));
    rack.add(new Tile('O', tileScores['O' - 65]));
    rack.add(new Tile('E', tileScores['E' - 65]));

    gameBoard.placeTile(new Tile('H', tileScores['H' - 65]), 12, 10);
    gameBoard.placeTile(new Tile('O', tileScores['O' - 65]), 12, 11);
    gameBoard.placeTile(new Tile('W', tileScores['W' - 65]), 12, 12);

    placements.clear();
    placements.add(gameBoard.getField(12, 10));
    try {
      gameBoard.check(placements, dictionary, true);
    } catch (BoardException e) {
      e.printStackTrace();
    }
    placements.clear();

    instructions.setText(steps[2]);
    stepOverview.setText(counter + 1 + "/7");

    updateBoard();
    updateRack();
  }

  /** Learns how to exchange the tiles the player has selected. */
  public void loadStageFour() {
    showErrorMessage(false);
    Sound.playMusic(Sound.rightWord);
    if (!stageFourUnlocked) {
      showErrorMessage(true);
      return;
    }
    counter++;
    updateScoreboard(1, 39); // 1 für player 2 für bot
    updateScoreboard(2, 29);

    for (int i = 0; i < Rack.RACK_SIZE; i++) {
      rack.remove(i);
    }
    rack.add(new Tile('Q', tileScores['Q' - 65]));
    rack.add(new Tile('H', tileScores['H' - 65]));
    rack.add(new Tile('Z', tileScores['Z' - 65]));

    gameBoard.placeTile(new Tile('I', tileScores['I' - 65]), 6, 10);
    gameBoard.placeTile(new Tile('R', tileScores['R' - 65]), 6, 11);
    gameBoard.placeTile(new Tile('E', tileScores['E' - 65]), 6, 12);

    placements.clear();
    placements.add(gameBoard.getField(12, 10));
    try {
      gameBoard.check(placements, dictionary, true);
    } catch (BoardException e) {
      e.printStackTrace();
    }
    placements.clear();

    instructions.setText(steps[3]);
    stepOverview.setText(counter + 1 + "/7");

    updateBoard();
    updateRack();
  }

  /** Method for getting to the next tutorial s. */
  public void nextStep() {
    Sound.playMusic(Sound.tileSet);
    if (counter < 3) {
      if ((stageTwoUnlocked && counter == 0)
          || (stageThreeUnlocked && counter == 1)
          || (stageFourUnlocked && counter == 2)) {
        showErrorMessage(false);
        counter++;
        instructions.setText(steps[counter]);
        stepOverview.setText(counter + 1 + "/7");
      } else {
        showErrorMessage(true);
      }
    }
    if (counter
        == 3) { // from here only information on the instructions are shown, no more tasks to
      // complete
      showErrorMessage(false);
      counter++;
      instructions.setText(steps[counter]);
      stepOverview.setText(counter + 1 + "/7");
      return;
    }
    if (counter == 4) {
      showErrorMessage(false);
      counter++;
      instructions.setText(steps[counter]);
      stepOverview.setText(counter + 1 + "/7");
      return;
    }
    if (counter == 5) {
      showErrorMessage(false);
      counter++;
      instructions.setText(steps[counter]);
      stepOverview.setText(counter + 1 + "/7");
      return;
    }
    if (counter >= 6) {
      instructions.setText(steps[counter]);
      stepOverview.setText(counter + 1 + "/7");
      showErrorMessage(false);
      showEndTutorial(true);
    } else {
      showErrorMessage(true);
    }
  }

  /** Method for getting to the previous tutorial */
  public void previousStep() {
    Sound.playMusic(Sound.tileSet);
    if (counter > 0) {
      showErrorMessage(false);
      counter--;
      instructions.setText(steps[counter]);
      stepOverview.setText(counter + 1 + "/7");
    }
  }

  /** Method for allowing to submit and load the next stage with new tasks. */
  public void submit() {
    Sound.playMusic(Sound.tileSet);
    if (counter == 0) { // Stage 1
      try {
        gameBoard.check(placements, dictionary, true);
        if (gameBoard.getFoundWords().size() == 0) {
          client.showPopUp("Try to place a word first!");
        } else if (gameBoard.getFoundWords().get(0).equals("HELLO")) {
          stageTwoUnlocked = true;
          loadStageTwo();
          return;
        } else {
          client.showPopUp("Please try again...");
        }
      } catch (BoardException be) {
        client.showPopUp(be.getMessage());
      }
    }
    if (counter == 1) { // Stage 2
      try {
        gameBoard.check(placements, dictionary, true);
        if (gameBoard.getFoundWords().size() == 0) {
          client.showPopUp("Try to place a word first!");
        } else if (gameBoard.getFoundWords().get(0).equals("FRIENDS")) {
          stageThreeUnlocked = true;
          loadStageThree();
          return;
        } else {
          client.showPopUp(
              "Great!-.. but"
                  + gameBoard.getFoundWords().get(0)
                  + " is not the word we are looking for here...Please try again!");
        }
      } catch (BoardException be) {
        client.showPopUp(be.getMessage());
      }
    }
    if (counter == 2) { // Stage 3
      try {
        gameBoard.check(placements, dictionary, true);
        if (gameBoard.getFoundWords().size() == 0) {
          client.showPopUp("Try to place a word first!");
        } else if (gameBoard.getFoundWords().get(0).equals("NOODLE")) {
          stageFourUnlocked = true;
          loadStageFour();
          return;
        } else {
          client.showPopUp(
              "Great!-.. but"
                  + gameBoard.getFoundWords().get(0)
                  + " is not the word we are looking for here...Please try again!");
        }
      } catch (BoardException be) {
        client.showPopUp(be.getMessage());
      }
    }
    if (counter == 3) { // Stage 4
      try {
        gameBoard.check(placements, dictionary, true);
        if (gameBoard.getFoundWords().size() == 0) {
          errorMessage.setText("Try to place a word first");
          showErrorMessage(true);
        } else {
          showErrorMessage(false);
          stepOverview.setText((++counter + 1) + "/7");
          instructions.setText(steps[counter]);
          placements.clear();
          updateBoard();
        }
      } catch (BoardException be) {
        client.showPopUp(be.getMessage());
      }
    }
  }

  /**
   * Update the graphics of the board with instances of PlayerProfiles/Board. Called after each
   * move. AnchorPane as graphical container for the tiles are created.
   */
  public void updateBoard() {
    board.getChildren().removeAll();
    for (int i = 0; i < 15; i++) {
      for (int j = 0; j < 15; j++) {
        AnchorPane anchorPane = createTile(gameBoard.getField(j, i));
        board.add(anchorPane, i, j);
      }
    }
  }

  /** Updates Scoreboard, called from NetClient if changes are made. */
  public void updateScoreboard(int idx, int score) {
    if (idx == 1) {
      pointsPlayer1.setText(Integer.toString(score));
    } else {
      pointsPlayer2.setText(Integer.toString(score));
    }
  }

  /** Updates Rack, called after each move the LocalPlayer makes. */
  public void updateRack() {
    tiles.getChildren().clear();
    for (int i = 0; i < Rack.RACK_SIZE; i++) {
      if (rack.isEmpty(i)) {
        continue;
      }
      AnchorPane anchorPane =
          createBottomTile(rack.getTile(i).getLetter(), rack.getTile(i).getScore(), i);
      tiles.add(anchorPane, i, 0);
    }
  }

  /**
   * Method to create the Containers for the tiles on the Rack includes graphical components and
   * adds the Drag and Drop feature.
   *
   * @param letter char
   * @param value int
   * @param position int
   * @return AnchorPane
   */
  public AnchorPane createBottomTile(char letter, int value, int position) {
    AnchorPane pane = new AnchorPane();
    Label label = new Label();
    Label points = new Label();

    label.setText("" + letter);

    // Tooltip if Joker
    if (letter == '#') {
      Tooltip tip = new Tooltip("This is a joker tile");
      tip.setShowDelay(Duration.millis(1500));
      tip.setShowDuration(Duration.millis(3000));
    }

    AnchorPane.setTopAnchor(label, 1.0);
    AnchorPane.setLeftAnchor(label, 1.0);
    AnchorPane.setRightAnchor(label, 1.0);
    AnchorPane.setBottomAnchor(label, 1.0);
    label.setAlignment(Pos.CENTER);
    label.getStylesheets().add("/stylesheets/labelstyle.css");

    if (rack.getField(position).isSelected()) {
      label.getStyleClass().add("tileBottomSelected");
    } else {
      label.getStyleClass().add("tileBottom");
    }

    points.setText(Integer.toString(value));
    AnchorPane.setTopAnchor(points, 1.0);
    AnchorPane.setLeftAnchor(points, 5.0);
    AnchorPane.setRightAnchor(points, 1.0);
    AnchorPane.setBottomAnchor(points, 1.0);
    points.setAlignment(Pos.BOTTOM_LEFT);
    points.getStylesheets().add("/stylesheets/labelstyle.css");
    points.getStyleClass().add("digitRack");

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
          rack.getField(position).setSelected(!rack.isSelected(position));
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
   * @param boardField board Field
   * @return AnchorPane
   */
  public AnchorPane createTile(BoardField boardField) {
    AnchorPane pane = new AnchorPane();
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
      if (placements.contains(
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

    pane.getChildren().add(label);
    pane.getChildren().add(points);

    pane.setOnDragOver(
        event -> {
          /* data is dragged over the target */

          /* accept it only if it is  not dragged from the same node
           * and if it has a string data */
          if (event.getGestureSource() != pane && event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
          }

          event.consume();
        });

    pane.setOnDragDropped(
        event -> {
          /* data dropped */
          /* if there is a string data on dragboard, read it and use it */
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
            int row = boardField.getRow();
            int col = boardField.getColumn();
            if (placements.contains(gameBoard.getField(row, col))) {
              if (gameBoard
                  .getTile(row, col)
                  .isJoker()) { // if it was a joker --> back to being a joker
                gameBoard.getTile(row, col).setLetter('#');
              }
              addTilesToRack(gameBoard.getTile(row, col));
              gameBoard.getField(row, col).setTile(null);
              placements.remove(gameBoard.getField(row, col));
            }
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

  /** Method for adding tiles to the rack. */
  public void addTilesToRack(Tile tile) {
    rack.add(tile);
    updateRack();
  }

  /** Method to update the graphical container of a Tile on the board. */
  public void updateTile(char letter, int points, int row, int col) {
    if (!gameBoard.isEmpty(row, col)) {

    } else {
      placeTile(draggedTileIndex, row, col);
      AnchorPane pane = createTile(gameBoard.getField(row, col));
      board.add(pane, col, row);
    }
    updateBoard();
    updateRack();
  }

  /**
   * Place Tile on board when player himself.
   *
   * @param position Requires which tile from rack you want to set
   * @param row Requires which row you placed the tile in
   * @param col Requires which column you placed the tile in
   */
  public void placeTile(int position, int row, int col) {
    if (gameBoard.isEmpty(row, col)) {
      Sound.playMusic(Sound.tileSet);
      if (rack.getTile(position).isJoker()) { // Joker -> Change letter
        // Build Choice Dialog
        List<String> choices = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
          choices.add(Character.toString(c));
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("A", choices);
        dialog.setTitle("Joker Tile");
        dialog.setHeaderText(null);
        dialog.setContentText("Choose your letter:");
        String choice = client.showDialog(dialog);
        if (choice == null) { // Cancel --> do nothing
          return;
        }
        rack.getTile(position).setLetter(choice.charAt(0));
      }
      rack.getField(position).setSelected(false);
      placements.add(gameBoard.getField(row, col));
      gameBoard.placeTile(rack.getField(position).getTile(), row, col);
      rack.remove(position);
    }
  }

  /** Method to update the graphical container of a Tile on the rack. */
  public void updateBottomTile(char letter, int points, int col) {
    AnchorPane pane = createBottomTile(letter, points, col);
    tiles.add(pane, col, 0);
  }

  /** Method to update the graphical containers of he board after a move. */
  public void placeTile(Tile tile, int row, int col) {
    gameBoard.placeTile(tile, row, col);
    updateBoard();
  }

  /**
   * method for showing the welcome screen.
   *
   * @param show Welcoming screen
   */
  public void showTutorialWelcome(boolean show) {
    tutorialWelcome.setVisible(show);
  }

  /**
   * Method for showing that steps have not been completed while trying to click submit or next step
   * button.
   *
   * @param show error Message
   */
  public void showErrorMessage(boolean show) {
    errorMessage.setVisible(show);
  }

  /**
   * Method for indicating the tutorial is finished and allowing the player to finish it and
   * navigate back to play scrabble.
   *
   * @param show end Tutorial button
   */
  public void showEndTutorial(boolean show) {
    endTutorial.setVisible(show);
  }

  /** Method for initializing the starting interface. */
  public void initialize() {

    instructions.setText(steps[0]);
    tutorialWelcome.managedProperty().bind(tutorialWelcome.visibleProperty());
    endTutorial.managedProperty().bind(errorMessage.visibleProperty());
    errorMessage.managedProperty().bind(errorMessage.visibleProperty());
  }

  /** Method for getting back to the play Scrabble Screen after finishing the tut. */
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

  /** Does nothing. Placeholder. */
  public void playerOne() {}

  /** Does nothing. Placeholder. */
  public void bot() {}

  /** Shuffles the tiles on the rack. */
  public void shuffle() {
    Sound.playMusic(Sound.tileSet);
    if (counter == 2) {
      for (int i = 0; i < 7; i++) {
        if (!rack.isEmpty(i)) {
          rack.remove(i);
        }
      }
      rack.add(new Tile('O', tileScores['O' - 65]));
      rack.add(new Tile('O', tileScores['O' - 65]));
      rack.add(new Tile('D', tileScores['D' - 65]));
      rack.add(new Tile('L', tileScores['L' - 65]));
      rack.add(new Tile('E', tileScores['E' - 65]));
    } else {
      rack.shuffleRack();
    }
    updateRack();
  }

  /** Triggered when Shuffle button is being pressed. */
  public void tiles() {

    if (!(rack.isSelected(0) && rack.isSelected(1) && rack.isSelected(2))) {
      errorMessage.setText("Select all tiles on your rack first!");
      showErrorMessage(true);
      return;
    } else {
      errorMessage.setText("Please complete this step first before advancing");
      showErrorMessage(false);
    }

    if (counter == 3) {
      for (int i = 0; i < 7; i++) {
        if (!rack.isEmpty(i)) {
          rack.remove(i);
        }
      }
      rack.add(new Tile('N', tileScores['N' - 65]));
      rack.add(new Tile('I', tileScores['I' - 65]));
      rack.add(new Tile('C', tileScores['C' - 65]));
      updateRack();
    }
  }

  /** Triggered when start button on welcoming page is being pressed. */
  public void start() throws IOException {
    Sound.playMusic(Sound.tileSet);
    showTutorialWelcome(false);
    loadStageOne();
  }
}
