package game.players;

import client.PlayerProfile;
import game.components.Tile;
import java.util.Collection;
import net.message.ChatMessage;
import net.message.GiveTileMessage;
import net.message.SubmitMoveMessage;
import net.server.ServerProtocol;

/**
 * Remote player instance, which is controlled by ServerProtocol and sends messages back.
 *
 * @author vkazmar | ygarip
 */
public class RemotePlayer extends Player {

  private final ServerProtocol connection;
  private boolean isReady;
  private boolean host;

  /**
   * Creates new RemotePlayer instance.
   *
   * @param connection requires the serverProtocol
   * @param host Requires boolean, if Player is host
   */
  public RemotePlayer(ServerProtocol connection, boolean host) {
    super(true);
    this.host = host;
    this.connection = connection;
    this.host = host;
  }

  /**
   * Sets turn for player.
   *
   * @param turn Requires value for turn
   */
  @Override
  public void setTurn(boolean turn) {
    super.setTurn(turn);
    if (turn) {
      connection.sendTurnMessage(turn);
    }
  }

  /**
   * Sets ready value for each player.
   *
   * @param value Requires value to be set
   */
  public void setIsReady(boolean value) {
    isReady = value;
  }

  /** Returns the player's readiness. */
  public boolean getReady() {
    return isReady;
  }

  /** Returns the serverprotocol connection. */
  public ServerProtocol getConnection() {
    return connection;
  }

  /** a method to set the Player.
   *  @param profile Requires PlayerProfile. */
  public void setPlayerProfile(PlayerProfile profile) {
    super.setPlayerProfile(profile);
  }

  /** Quit from game. Player is sent all relevant information. */
  @Override
  public void quit() {
    if (game != null) {
      game.resetBoard();
      game.notify(new ChatMessage(getProfile().getName() + " left", null)); // notify others
    }
  }

  /** Add tile to player's rack. */
  @Override
  public void addTilesToRack(Collection<Tile> tiles) {
    tiles.forEach(
        tile -> {
          connection.sendToClient(new GiveTileMessage(tile));
        });
  }

  /** Returns if this player is host of the game. */
  public boolean isHost() {
    return host;
  }

  /**
   * Sends remote player a system message with given reason Sends remote player the latest board
   * after check (after validity of fields were checked and updated).
   */
  public void rejectSubmission(String reason) {
    connection.sendToClient(new ChatMessage(reason, null)); // Send System Message
    connection.sendToClient(
        new SubmitMoveMessage(
            game.getBoard())); // Send Board state With 'validities' of every and each field
  }
}
