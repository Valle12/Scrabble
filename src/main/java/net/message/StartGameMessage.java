package net.message;

/**
 * a message to start the game.
 *
 * @author ygarip
 */
public class StartGameMessage extends Message {

  /** a constructor to create a StartGame Message. */
  public StartGameMessage() {
    super(MessageType.STARTGAME);
  }
}
