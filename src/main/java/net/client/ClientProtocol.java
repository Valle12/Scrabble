package net.client;

import client.PlayerProfile;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import javafx.application.Platform;
import net.message.ChatMessage;
import net.message.ConnectMessage;
import net.message.DisconnectMessage;
import net.message.EndGameMessage;
import net.message.GiveTileMessage;
import net.message.Message;
import net.message.PlaceTileMessage;
import net.message.PlayerReadyMessage;
import net.message.RefuseConnectionMessage;
import net.message.RequestDictionaryMessage;
import net.message.RequestDistributionsMessage;
import net.message.RequestValuesMessage;
import net.message.TurnMessage;

/**
 * Handles all interactions from the server to the client.
 *
 * @author vkaczmar
 */
public class ClientProtocol extends Thread {
  private final NetClient client;
  private final Socket clientSocket;
  private final ObjectOutputStream out;
  private final ObjectInputStream in;
  private boolean running = true;

  /**
   * Constructor for creating streams and connecting a client.
   *
   * @param ip Requires the ip, the server runs on
   * @param netClient Requires username for current profile
   */
  public ClientProtocol(String ip, NetClient netClient) throws IOException {
    this.client = netClient;
    this.clientSocket = new Socket(ip, 12975);
    this.out = new ObjectOutputStream(clientSocket.getOutputStream());
    this.in = new ObjectInputStream(clientSocket.getInputStream());
    this.out.writeObject(new ConnectMessage(client.getPlayerProfile()));
    this.out.flush();
  }

  /** Method for writing a DisconnectMessage Object to the server. */
  public void disconnect() {
    running = false;
    try {
      if (!clientSocket.isClosed()) {
        this.out.writeObject(new DisconnectMessage(client.getClient().getSelectedProfile(), ""));
        clientSocket.close();
      }
    } catch (IOException e) {
      client.getClient().showError("Failed to disconnect properly");
    }
  }

  /**
   * Sends all types of messages.
   *
   * @param m Message to be send
   */
  public void sendMessage(Message m) {
    try {
      if (!clientSocket.isClosed()) {
        this.out.writeObject(m);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Overwritten run method from Thread. Accepts and works through incoming messages from the server
   */
  public void run() {
    while (running) {
      try {
        Message m = (Message) in.readObject();
        switch (m.getMessageType()) {
          case CONNECT:
            PlayerProfile[] lobbyProfiles = ((ConnectMessage) m).getProfiles();

            // sets list of coplayers and updates respective views
            Platform.runLater(
                () -> {
                  if (!client.isHost()) {
                    client.loadGameView(); // Load Game view only if connected
                  }
                  // set Data
                  client.setLobbyState(lobbyProfiles, new int[lobbyProfiles.length]);
                });
            client.setPlayerReady(false);
            break;
          case REFUSECONNECTION:
            Platform.runLater(
                () -> {
                  client.getClient().showPopUp(((RefuseConnectionMessage) m).getMessage());
                });
            running = false;
            break;
          case UPDATEGAMESETTINGS:
            break;
          case STARTGAME:
            client.initializeGame(); // load Game view
            break;
          case CHATMESSAGE:
            String username = ((ChatMessage) m).getUsername();
            String message = ((ChatMessage) m).getMsg();
            Platform.runLater(
                () -> {
                  client.updateChat(username, message);
                });
            break;
          case PLAYERREADY:
            PlayerReadyMessage prm = (PlayerReadyMessage) m;
            Platform.runLater(
                () -> {
                  client.setReadies(prm.getValues());
                });
            break;
          case SENDPLAYERDATA:
            break;
          case GIVETILE:
            Platform.runLater(
                () -> {
                  client
                      .getClient()
                      .getLocalPlayer()
                      .addTilesToRack(((GiveTileMessage) m).getTile());
                });
            break;
          case TURN:
            Platform.runLater(
                () -> {
                  client.setTurns(((TurnMessage) m).getTurn(), ((TurnMessage) m).getTurns());
                  client.setBagSize(((TurnMessage) m).getBagSize());
                  client.setCoPlayerScores(((TurnMessage) m).getPoints());
                });
            break;
          case DISCONNECT:
            Platform.runLater(
                () -> {
                  try {
                    client
                        .getClient()
                        .showPopUp(
                            ((DisconnectMessage) m)
                                .getDisconnectMessage()); // show reason for forced disconnection
                    client.getClient().showMainMenu();
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                });
            break;
          case REQUESTDICTIONARY:
            String dictionary = ((RequestDictionaryMessage) m).getDictionary();
            client.updateGameSettings(null, null, dictionary);
            break;
          case REQUESTVALUES:
            int[] values = ((RequestValuesMessage) m).getValues();
            client.updateGameSettings(values, null, null);
            break;
          case REQUESTDISTRIBUTIONS:
            int[] distributions = ((RequestDistributionsMessage) m).getDistributions();
            client.updateGameSettings(null, distributions, null);
            break;
          case PLACETILE:
            PlaceTileMessage ptm = (PlaceTileMessage) m;
            Platform.runLater(
                () -> {
                  client.placeIncomingTile(ptm.getTile(), ptm.getRow(), ptm.getCol());
                });
            break;
          case ENDGAME:
            EndGameMessage egm = (EndGameMessage) m;
            String tmp;
            if (egm.getType() == 0) {
              tmp =
                  "The game ended, because the bag is empty and at least one player has no Tiles left in his rack.";
            } else if (egm.getType() == 1) {
              tmp = "The game ended, because at least one player clicked on end game";
            } else {
              tmp =
                  "The game ended, because at least one player used up more than 10 minutes of playtime.";
            }

            PlayerProfile tmp2 = client.getPlayerProfile();
            tmp2.setHighScore(Math.max(tmp2.getHighScore(), egm.getScore()));
            tmp2.setLastLogged(LocalDate.now());
            tmp2.setTotalScore(Math.min(tmp2.getHighScore() + egm.getScore(), Integer.MAX_VALUE));
            if (egm.getWinner()) {
              tmp2.setWins(tmp2.getWins() + 1);
            } else {
              tmp2.setLosses(tmp2.getLosses() + 1);
            }
            client.getClient().savePlayerProfiles();

            Platform.runLater(
                () -> {
                  client.updateChat(null, tmp);
                  client.changeToResultView();
                });
            break;
          case ENDABLE:
            client.enableEndGameButton();
            break;
          default:
            break;
        }
      } catch (IOException | ClassNotFoundException e) {
        break;
      }
    }
  }
}
