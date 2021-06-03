package net.server;

import client.PlayerProfile;
import game.Dictionary;
import game.Game;
import game.components.Tile;
import game.players.Player;
import game.players.RemotePlayer;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import net.message.Message;
import net.message.RefuseConnectionMessage;

/**
 * a server class to setup the server.
 *
 * @author ygarip
 */
public class Server extends Thread {
  private ServerSocket serverSocket;
  private boolean running;
  public static final int PORT = 12975;
  private static String serverIp;
  private final List<Player> players = new LinkedList<>();
  private Game game;

  private int[] tileScores = {
    1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
  };
  private int[] tileDistributions = {
    9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2
  };
  private Dictionary dictionary = new Dictionary();

  /** Constructor to create server, sets serverIP. */
  public Server() {
    try {
      serverIp = getLocalHostIp4Address();
      serverIp = "25.93.29.50";
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  /** a method to run the server. */
  public void run() {
    this.listen();
  }

  /** returns the playerList in an array. */
  public synchronized List<Player> getPlayers() {
    return players;
  }

  /** Starts game from server. */
  public void startGame() {
    // set game bag
    LinkedList<Tile> gameBag = new LinkedList<>();
    for (int i = 0; i < tileDistributions.length; i++) {
      for (int j = 0; j < tileDistributions[i]; j++) {
        if (i != tileDistributions.length - 1) { // Not joker
          gameBag.add(new Tile(((char) ('A' + i)), tileScores[i]));
        } else { // joker
          gameBag.add(new Tile('#', 0));
        }
      }
    }

    game = new Game(players, gameBag, dictionary);
    game.nextRound(); // Start first round
  }

  /** returns if the game is running. */
  public boolean gameIsRunning() {
    return game != null;
  }

  /**
   * a method to get the IPAddress of the Local User.
   * @return Returns ip from localhost
   * @throws UnknownHostException
   * @author from
   *     https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
   */
  public static String getLocalHostIp4Address() throws UnknownHostException {

    try {

      InetAddress candidateAddress = null;

      for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
          ifaces.hasMoreElements(); ) {

        NetworkInterface iface = ifaces.nextElement();

        for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
            inetAddrs.hasMoreElements(); ) {

          InetAddress inetAddr = inetAddrs.nextElement();

          if (!inetAddr.isLoopbackAddress() && inetAddr instanceof Inet4Address) {

            if (inetAddr.isSiteLocalAddress()) {

              return inetAddr.getHostAddress();

            } else if (candidateAddress == null) {

              candidateAddress = inetAddr;
            }
          }
        }
      }

      if (candidateAddress != null) {

        return candidateAddress.getHostAddress();
      }

      InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();

      if (jdkSuppliedAddress == null) {

        throw new UnknownHostException(
            "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
      }

      return jdkSuppliedAddress.getHostAddress();

    } catch (Exception e) {

      UnknownHostException unknownHostException =
          new UnknownHostException("Failed to determine LAN address: " + e);

      unknownHostException.initCause(e);

      throw unknownHostException;
    }
  }

  /**
   * starts the serverSocket and listens for incoming clients which want to connect to the server.
   */
  public void listen() {
    running = true;
    try {
      serverSocket = new ServerSocket(Server.PORT);
      while (running) {
        Socket clientSocket = serverSocket.accept();
        ServerProtocol clientThread = new ServerProtocol(clientSocket, this);

        // if full send ConnectionRefusedMessage back
        if (players.size() >= 4) {
          clientThread.sendToClient(new RefuseConnectionMessage("Lobby is full"));
        } else if (game != null) {
          clientThread.sendToClient(
              new RefuseConnectionMessage(
                  "Connection Refused. Game is already running.\n\nSorry, " +
                          "your friends started the game without you :("));
        } else {
          RemotePlayer newPlayer = new RemotePlayer(clientThread, players.size() == 0);
          clientThread.setPlayer(newPlayer);
          players.add(newPlayer);

          clientThread.start();
        }
      }
    } catch (IOException e) {
      if (serverSocket != null && serverSocket.isClosed()) {
      } else {
        e.printStackTrace();
      }
    }
  }

  /**
   * method to add a player to the server's playerList.
   *
   * @param player Requires the player
   */
  public synchronized void addPlayer(Player player) {
    players.add(player);
  }

  /**
   * method to remove a player from the server's playerList.
   *
   * @param player Requires the player
   */
  public synchronized void removePlayer(Player player) {
    players.remove(player);
  }

  /** a method for stopping the server immediately. */
  public synchronized void stopServer() {
    running = false;
    if (!serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * a method for sending a message to all clients.
   *
   * @param m requires the message
   */
  public synchronized void sendToAll(Message m) {
    int index = 0;
    for (Player player : players) {
      if (!player.isHuman()) {
        continue;
      }
      ((RemotePlayer) player).getConnection().sendToClient(m);
    }
  }

  /**
   * a method for sending a message to all client but not the given Player.
   *
   * @param m requires the message
   */
  public synchronized void sendToOthers(ServerProtocol protocol, Message m) {
    players.forEach(
        player -> {
          if (player.isHuman()
              && ((RemotePlayer) player).getConnection()
                  != protocol) { // if not protocol who initiated the call
            ((RemotePlayer) player).getConnection().sendToClient(m);
          }
        });
  }

  // send this one back to the server protocol

  /**  Returns a Tile from the bag. */
  public synchronized Tile getTile() {
    return new Tile('A', 2);
  }

  /**
   * a method to update the GameSettings of the server.
   *
   * @param tileScores Requires the tileScores array
   * @param tileDistributions Requires the tileDistribution array
   * @param dictionary Requires the dictionaryString's txt file
   */
  public synchronized void updateGameSettings(
      int[] tileScores, int[] tileDistributions, String dictionary) {
    this.tileScores = tileScores;
    this.tileDistributions = tileDistributions;
    if (dictionary != null) {
      if (dictionary.equals("")) {
        this.dictionary = new Dictionary();
      } else {
        this.dictionary = new Dictionary(dictionary);
      }
    }
  }

  /** Returns if server is still running. */
  public boolean isRunning() {
    return running;
  }

  /** Returns array with all playerProfiles. */
  public PlayerProfile[] getPlayerProfilesArray() {
    PlayerProfile[] temp = new PlayerProfile[players.size()];
    for (int i = 0; i < players.size(); i++) {
      temp[i] = players.get(i).getProfile();
    }
    return temp;
  }

  /** Returns TileValues. */
  public int[] getTileScores() {
    return tileScores;
  }

  /**  Returns TileDistributions. */
  public int[] getTileDistributions() {
    return tileDistributions;
  }

  /**  Returns dictionaryString. */
  public String getDictionaryString() {
    return dictionary.getDictionary();
  }

  /**  Returns the game instance. */
  public Game getGame() {
    return game;
  }
}
