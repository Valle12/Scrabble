package net.server;

import client.PlayerProfile;
import game.components.Tile;
import game.players.Player;
import game.players.RemotePlayer;
import game.players.aiplayers.AiPlayer;
import game.players.aiplayers.EasyAiPlayer;
import game.players.aiplayers.HardAiPlayer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import net.message.AddAiMessage;
import net.message.ChatMessage;
import net.message.ConnectMessage;
import net.message.DisconnectMessage;
import net.message.EndGameMessage;
import net.message.ExchangeTileMessage;
import net.message.KickPlayerMessage;
import net.message.Message;
import net.message.PlaceTileMessage;
import net.message.PlayerReadyMessage;
import net.message.RequestDictionaryMessage;
import net.message.RequestDistributionsMessage;
import net.message.RequestValuesMessage;
import net.message.SendPlayerDataMessage;
import net.message.TurnMessage;
import net.message.UpdateGameSettingsMessage;

/**
 * A ServerProtocol class to handle serverside messages.
 *
 * @author ygarip
 */
public class ServerProtocol extends Thread {
  private final Socket socket;
  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final Server server;

  private boolean running = true;

  private RemotePlayer player; // Controlled player

  /**
   * constructor for creating a new ServerProtocol and connecting the server to the clients and the
   * input and output streams.
   *
   * @param client the client which is connected to the server
   * @param server the server which is providing the game
   */
  public ServerProtocol(Socket client, Server server) throws IOException {
    this.socket = client;
    this.server = server;
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
  }

  /** a method to disconnect the client from the server. */
  public void disconnect() {
    running = false;
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * a method to send a Message to the client.
   *
   * @param m the Message of a MessageType which should be send to the Client
   * @throws IOException exception by sending the message to the client
   */
  public void sendToClient(Message m) {
    try {
      this.out.writeObject(m);
      out.flush();
    } catch (IOException ioe) {
      server.getPlayers().remove(player);
      ConnectMessage cm = new ConnectMessage(null);
      cm.setProfiles(server.getPlayerProfilesArray());
      server.sendToAll(cm);
    }
  }

  /** Sends to all others. */
  public void sendTurnMessage(boolean turn) {
    boolean[] turns = new boolean[server.getPlayers().size()];
    int[] scores = new int[server.getPlayers().size()];
    for (int i = 0; i < turns.length; i++) {
      turns[i] = server.getPlayers().get(i).isTurn();
      scores[i] = server.getPlayers().get(i).getScore();
    }
    for (int i = 0; i < turns.length; i++) {
      if (server.getPlayers().get(i).isHuman()) {
        Message m = new TurnMessage(turns[i], turns, server.getGame().getBagSize(), scores);
        ((RemotePlayer) server.getPlayers().get(i)).getConnection().sendToClient(m);
      }
    }
  }

  /**
   * Sets remote player in ServerProtocol.
   *
   * @param player Requires player to be set
   */
  public void setPlayer(RemotePlayer player) {
    this.player = player;
  }

  /** the run method of ServerProtocol to handle the incoming messages of the server. */
  public void run() {
    try {

      while (running) {
        Message m = (Message) in.readObject();
        switch (m.getMessageType()) {
          case CONNECT:
            PlayerProfile profile = ((ConnectMessage) m).getProfile();
            player.setPlayerProfile(profile);
            ConnectMessage cm = (ConnectMessage) m;
            if (!player.isHost()) { // if not host --> joined us message
              server.sendToAll(
                  new ChatMessage(
                      profile.getName()
                          + " joined our round!"
                          + (profile.getName().split(" ")[0].equals("Bot") ? "*Beep-Boop*" : ""),
                      null)); // send system Message to all
            }

            // send new lobby list to all
            cm.setProfiles(server.getPlayerProfilesArray());
            server.sendToAll(cm);
            break;
          case DISCONNECT:
            profile = ((DisconnectMessage) m).getProfile();
            server.removePlayer(player); // Remove Player from server
            if (player.isHost()) {
              server.sendToAll(
                  new DisconnectMessage(
                      null,
                      "The server is closed.\n\nThe Host sadly doesn't want to play any longer"));
              server.stopServer();
            } else { // take player out of player list and send new ConnectMessage with all player
              player.quit(); // quit

              // profiles connected
              cm = new ConnectMessage(null);
              cm.setProfiles(server.getPlayerProfilesArray());
              server.sendToAll(cm);

              if (player.isTurn()) {
                server.getGame().nextRound(); // next round
              }
            }
            disconnect();
            break;
          case CHATMESSAGE: // Send chat message to all other connections
            server.sendToOthers(this, m);
            break;
          case UPDATEGAMESETTINGS:
            UpdateGameSettingsMessage ugsm = (UpdateGameSettingsMessage) m;
            int[] tileScores = ugsm.getTileScores();
            int[] tileDistributions = ugsm.getTileDistributions();
            String dictionary = ugsm.getDictionary();
            server.updateGameSettings(tileScores, tileDistributions, dictionary);
            break;
          case PLAYERREADY:
            PlayerReadyMessage prm = (PlayerReadyMessage) m;
            player.setIsReady(prm.getReady());
            List<Player> players = server.getPlayers();
            boolean[] playerReady = new boolean[players.size()];
            boolean ready = true;
            for (int i = 1; i < playerReady.length; i++) {
              playerReady[i] =
                  (!players.get(i).isHuman()
                      || ((RemotePlayer) players.get(i))
                          .getReady()); // bots are always ready / remote player if set
              ready = ready && playerReady[i];
            }
            prm.setValues(playerReady);
            prm.setReady(ready);
            server.sendToAll(prm);
            break;
          case STARTGAME:
            server.sendToAll(m);
            server.startGame();
            break;
          case SUBMITMOVE:
            player.submit();
            break;
          case SENDPLAYERDATA:
            SendPlayerDataMessage spdm = (SendPlayerDataMessage) m;
            spdm.setProfile(server.getPlayers().get(spdm.getId()).getProfile());
            sendToClient(spdm);
            break;
          case EXCHANGETILES:
            ExchangeTileMessage etm = (ExchangeTileMessage) m;
            Tile[] oldTiles = ((ExchangeTileMessage) m).getOldTiles();
            ArrayList<Tile> tiles = new ArrayList<>();

            for (int i = 0; i < oldTiles.length; i++) {
              tiles.add(oldTiles[i]);
            }

            player.exchange(tiles);

            break;
          case ADDAI:
            // Check if server has space for a player
            if (server.getPlayers().size() >= 4) {
              break;
            }
            AddAiMessage ai = (AddAiMessage) m;
            AiPlayer aiPlayer;
            if (ai.getDifficulty()) {
              aiPlayer = new HardAiPlayer();
            } else {
              aiPlayer = new EasyAiPlayer();
            }
            ChatMessage c1 =
                new ChatMessage(
                    aiPlayer.getProfile().getName() + " joined our Round. *Beep-Boop*", null);
            server.sendToAll(c1);
            server.addPlayer(aiPlayer);

            ConnectMessage cm1 = new ConnectMessage(null);
            cm1.setProfiles(server.getPlayerProfilesArray());
            server.sendToAll(cm1);

            break;
          case KICKPLAYER:
            DisconnectMessage dm =
                new DisconnectMessage(
                    null,
                    "You were kicked out of the lobby!\n\nThe host doesn't like you anymore!");

            // Send message to kicked player

            Player rp1 = server.getPlayers().get(((KickPlayerMessage) m).getIndex());
            if (rp1.isHuman()) {
              RemotePlayer p1 = (RemotePlayer) rp1;
              p1.getConnection().sendToClient(dm);
            } else {
              AiPlayer p1 = (AiPlayer) rp1;
              p1.quit();
            }

            // Remove Player from server
            server.removePlayer(rp1);

            // Send system message
            server.sendToAll(
                new ChatMessage(rp1.getProfile().getName() + " was kicked by the host", null));

            // update lobby profiles
            cm = new ConnectMessage(null);
            cm.setProfiles(server.getPlayerProfilesArray());
            server.sendToAll(cm);
            break;
          case REQUESTVALUES:
            RequestValuesMessage rvm = (RequestValuesMessage) m;
            rvm.setValues(server.getTileScores());
            sendToClient(rvm);
            break;
          case REQUESTDISTRIBUTIONS:
            RequestDistributionsMessage rdm = (RequestDistributionsMessage) m;
            rdm.setDistributions(server.getTileDistributions());
            sendToClient(rdm);
            break;
          case REQUESTDICTIONARY:
            RequestDictionaryMessage rdm2 = (RequestDictionaryMessage) m;
            rdm2.setDictionary(server.getDictionaryString());
            sendToClient(rdm2);
            break;
          case PLACETILE:
            if (((PlaceTileMessage) m).getTile() != null) {
              player.placeTile(
                  ((PlaceTileMessage) m).getTile(),
                  ((PlaceTileMessage) m).getRow(),
                  ((PlaceTileMessage) m).getCol());
            } else {
              player.removeTile(((PlaceTileMessage) m).getRow(), ((PlaceTileMessage) m).getCol());
            }
            break;
          case ENDABLE:
            break;
          case ENDGAME:
            server.getGame().end(((EndGameMessage) m).getType());
            break;
          default:
            break;
        }
      }
    } catch (IOException e) {
      running = false;
      if (socket.isClosed()) {
        System.out.println();
      } else {
        try {
          socket.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
    }
  }
}
