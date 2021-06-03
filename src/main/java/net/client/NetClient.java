package net.client;

import client.Client;
import client.PlayerProfile;
import ft.Sound;
import game.Dictionary;
import game.components.Tile;
import gui.controllers.CreateGameController;
import gui.controllers.DictionaryController;
import gui.controllers.GameResultsController;
import gui.controllers.GameViewController;
import gui.controllers.JoinGameController;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.message.AddAiMessage;
import net.message.ChatMessage;
import net.message.EndGameMessage;
import net.message.ExchangeTileMessage;
import net.message.KickPlayerMessage;
import net.message.PlaceTileMessage;
import net.message.PlayerReadyMessage;
import net.message.RequestDictionaryMessage;
import net.message.RequestDistributionsMessage;
import net.message.RequestValuesMessage;
import net.message.StartGameMessage;
import net.message.SubmitMoveMessage;
import net.message.UpdateGameSettingsMessage;
import net.server.Server;

/**
 * a client class to create a client and connect with the server.
 *
 * @author vkaczmar
 */
public class NetClient {
  private final Client client;
  private ClientProtocol connection;
  private String ipAdr;
  private Dictionary dictionary;
  private Server server;
  private CreateGameController createGameController; // controls GUI
  private GameViewController gameViewController; // controls GUI Game
  private JoinGameController joinGameController;
  private GameResultsController gameResultsController;
  private PlayerProfile[] coPlayers; // players in lobby (with local player himself)
  private int[] coPlayerScores; // scores of players in lobby

  /**
   * Constructor to create a NetClient with client.
   *
   * @param client Requires client to connect from
   */
  public NetClient(Client client) {
    this.client = client;
    try {
      this.ipAdr = Server.getLocalHostIp4Address();
      coPlayers = new PlayerProfile[] {client.getSelectedProfile()};
      coPlayerScores = new int[1];
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Creates and starts server. */
  public void createServer() {
    server = new Server();
    server.start();
  }

  /**
   * a method to connect the client to the server.
   *
   * @author ygarip
   */
  public void connect() {
    try {
      this.connection = new ClientProtocol(this.ipAdr, this);
      this.connection.start();
    } catch (IOException ioe) {
      client.showPopUp("Could not establish connection to a server on " + this.ipAdr);
    }
  }

  /** Returns ClientProtocol connected to NetClient. */
  public ClientProtocol getConnection() {
    return connection;
  }

  /** Returns up of NetClient. */
  public String getIp() {
    return ipAdr;
  }

  /**
   * method to set the ip.
   *
   * @param ip requires the ip
   */
  public void setIp(String ip) {
    this.ipAdr = ip;
  }

  /** Returns server */
  public Server getServer() {
    return this.server;
  }

  /**
   * a method to disconnect the client from the server.
   *
   * @throws IOException exception occurs if disconnect didn't work
   * @author ygarip
   */
  public void disconnect() {
    try {
      connection.disconnect();
      connection = null;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Calls method, whenever the host changed anything in the gamesettings.
   *
   * @param tileScores Requires tileScores
   * @param tileDistributions requires tileDistribution
   * @param dictionary requires dictionary path
   */
  public void sendGameSettings(int[] tileScores, int[] tileDistributions, String dictionary) {
    connection.sendMessage(
        new UpdateGameSettingsMessage(tileScores, tileDistributions, dictionary));
  }

  /**
   * Initiates sending of Players readiness to server.
   *
   * @param ready Requires ready value to be sent
   */
  public void setPlayerReady(boolean ready) {
    connection.sendMessage(new PlayerReadyMessage(ready));
  }

  /**
   * a method to start the game.
   *
   * @author ygarip
   */
  public void startGame() {
    connection.sendMessage(new StartGameMessage());
  }

  /**
   * sends text message to all other clients.
   *
   * @author vkaczmar
   * @param chatMessage Requires chatMessage to be sent to other players
   */
  public void sendChatMessage(String chatMessage) {
    connection.sendMessage(new ChatMessage(chatMessage, client.getSelectedProfile().getName()));
  }

  /**
   * checks if move is valid.
   *
   * @author ygarip
   */
  public void submitMove() {
    connection.sendMessage(new SubmitMoveMessage());
  }

  /**
   * a method to get the username of client.
   *
   * @return returns username of client
   * @author vkaczmar
   */
  public String getUsername() {
    return client.getSelectedProfile().getName();
  }

  /**
   * a method to set the wished dictionary.
   *
   * @param dictionary requires the dictionary to be set
   * @author ygarip
   */
  public void setDictionary(Dictionary dictionary) {
    this.dictionary = dictionary;
  }

  /**
   * a method to return the player profile.
   *
   * @return returns the players profile
   * @author vkaczmar
   */
  public PlayerProfile getPlayerProfile() {
    return client.getSelectedProfile();
  }

  /** Returns client */
  public Client getClient() {
    return client;
  }

  /**
   * Method for initiating ExchangeTileMessage.
   *
   * @author vkaczmar a method to request the bag amount
   */
  public void exchangeTiles(Tile[] oldTiles) {
    connection.sendMessage(new ExchangeTileMessage(oldTiles));
  }

  /**
   * Sets create game Controller.
   *
   * @param createGameController Requires createGameController to be set
   */
  public void setCreateGameController(CreateGameController createGameController) {
    this.createGameController = createGameController;
  }

  /**
   * Set players to update view.
   *
   * @param profiles Requires PlayerProfiles array
   */
  public void setLobbyState(PlayerProfile[] profiles, int[] scores) {
    this.coPlayers = profiles;
    this.coPlayerScores = scores;
    if (isHost() && !server.gameIsRunning()) { // player is host and game is not run
      createGameController.fillLobby(profiles);
    } else { // player is not host
      gameViewController.updateScoreboard(profiles, scores);
    }
  }

  /**
   * Updates chat in different controllers.
   *
   * @param user Requires user as sender of message
   * @param message Requires message to display
   */
  public void updateChat(String user, String message) {
    if (user != null) { // not received from system
      if (isHost()
          && !server.gameIsRunning()
          && gameResultsController == null) { // user is host --> sees CreateGameScene
        createGameController.getMessage(user, message);
      } else if (gameResultsController != null) { // user is not host --> sees GameView
        gameResultsController.getMessage(user, message);
      } else {
        gameViewController.getMessage(user, message);
      }
    } else {
      if (isHost() && !server.gameIsRunning()) {
        createGameController.createSystemMessage(message);
      } else {
        if (gameViewController != null) {
          gameViewController.createSystemMessage(message);
        }
        if (gameResultsController != null) {
          gameResultsController.createSystemMessage(message);
        }
      }
    }
  }

  /**
   * Show Requested content.
   *
   * @param scores if score != null display score
   * @param distributions if distributions != null display distributions
   * @param dictionaryContent if dictionaryContent != null display dictionary
   */
  public void updateGameSettings(int[] scores, int[] distributions, String dictionaryContent) {
    Platform.runLater(
        () -> {
          if (scores != null) {
            String content = "";
            for (int i = 0; i < scores.length; i++) {
              content += ((char) (i + 'A')) + "\t" + scores[i] + "\n";
            }
            client.showPopUp(content);
          }
          if (distributions != null) {
            String content = "";
            for (int i = 0; i < distributions.length - 1; i++) {
              content += ((char) (i + 'A')) + "\t" + distributions[i] + "\n";
            }
            content += "#\t" + distributions[26] + "\n";
            client.showPopUp(content);
          }
          if (dictionaryContent != null) {
            FXMLLoader loader = new FXMLLoader();
            Sound.playMusic(Sound.tileSet);
            loader.setLocation(this.getClass().getResource("/views/dictionary.fxml"));
            Parent dictionary = null;
            try {
              dictionary = loader.load();
            } catch (IOException e) {
              e.printStackTrace();
            }
            DictionaryController controller = loader.getController();
            controller.setModel(client);
            controller.showDictionary(dictionaryContent);

            Scene dictionaryScene = new Scene(dictionary);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Dictionary");
            window.setResizable(false);
            window.setScene(dictionaryScene);
            window.showAndWait();
          }
        });
  }

  /**
   * Sets joinGameController.
   *
   * @param joinController Requires join game Controller to be set
   */
  public void setJoinGameController(JoinGameController joinController) {
    this.joinGameController = joinController;
  }

  /**
   * Sets gameViewController.
   *
   * @param gameController Requires gameViewController to be set
   */
  public void setGameViewController(GameViewController gameController) {
    this.gameViewController = gameController;
  }

  /** Load game view. */
  public void loadGameView() {
    try {
      joinGameController.loadGameView();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Returns if client is host or not. */
  public boolean isHost() {
    return server != null && server.isRunning();
  }

  /** Returns playerProfiles as an Array. */
  public PlayerProfile[] getCoPlayers() {
    return coPlayers;
  }

  /** Returns coPlayerScores as Array. */
  public int[] getCoPlayerScores() {
    return coPlayerScores;
  }

  /**
   * Sets coPlayerScores.
   *
   * @param scores Requires scores array to be set
   */
  public void setCoPlayerScores(int[] scores) {
    this.coPlayerScores = scores;
    gameViewController.updateScoreboard(coPlayers, scores);
  }

  /**
   * Initiates sending of difficulty for AI Creation.
   *
   * @param difficult Requires difficulty, true = hard, false = easy
   */
  public void addAiPlayer(boolean difficult) {
    connection.sendMessage(new AddAiMessage(difficult));
  }

  /**
   * Kicks player out of the game.
   *
   * @param index Requires index, which player you want to kick
   */
  public void kickPlayer(int index) {
    connection.sendMessage(new KickPlayerMessage(index));
  }

  /** Initiates RequestValuesMessage. */
  public void requestValues() {
    connection.sendMessage(new RequestValuesMessage());
  }

  /** Request distributions. */
  public void requestDistributions() {
    connection.sendMessage(new RequestDistributionsMessage());
  }

  /** Request dictionary. */
  public void requestDictionary() {
    connection.sendMessage(new RequestDictionaryMessage());
  }

  /** sends placeTile Message. */
  public void placeTile(Tile tile, int row, int col) {
    connection.sendMessage(new PlaceTileMessage(tile, row, col));
  }

  /** Place tile on GUI. */
  public void placeIncomingTile(Tile tile, int row, int col) {
    gameViewController.placeTile(tile, row, col);
  }

  /** Initializes game in GameView Controller. */
  public void initializeGame() {
    if (!isHost()) {
      gameViewController.showAgreements(false);
    }
  }

  /** Triggered by incoming PLAYERREADYMESSAGE. */
  public void setReadies(boolean[] readies) {
    if (isHost()) {
      createGameController.updatePlayerReadies(readies);
    }
  }

  /**
   * Triggered by incoming TURN MESSAGE.
   *
   * @param turn true if it is this client's turn
   * @param turns array of turn state (to be able to show whose turn it is)
   */
  public void setTurns(boolean turn, boolean[] turns) {
    client.getLocalPlayer().setTurn(turn); // set turn of local player -> true
    gameViewController.updateTurns(turns); // show whose turn it is
  }

  /**
   * changes bag size to given int value.
   *
   * @param bagSize Requires value to set to
   */
  public void setBagSize(int bagSize) {
    gameViewController.setBagSize(bagSize);
  }

  /**
   * Send End Message (type 0 bag empty, type 2 overtime exceeded).
   *
   * @param type Requires the EndGameMessage int type
   */
  public void sendEndMessage(int type) {
    connection.sendMessage(new EndGameMessage(type));
  }

  /** Enable EndGameButton in gameView. */
  public void enableEndGameButton() {
    gameViewController.showEndButton();
  }

  /** Change View to result. */
  public void changeToResultView() {
    try {
      gameViewController.changeToResultView();
      gameResultsController.updateScoreboard(coPlayers, coPlayerScores);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets Game Result Controller.
   *
   * @param gameResultsController Requires controller to be set
   */
  public void setGameResultsController(GameResultsController gameResultsController) {
    this.gameResultsController = gameResultsController;
  }
}
