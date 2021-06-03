package net.message;

/**
 * Message class for kicking the player from Server.
 *
 * @author ygarip
 */
public class KickPlayerMessage extends Message {
  private int index;

  /**
   * a constructor to create a KickPlayerMessage for disconnecting an unwanted player.
   * @param index Requires the index of the player which should be kicked from the lobby
   */
  public KickPlayerMessage(int index) {
    super(MessageType.KICKPLAYER);
    this.index = index;
  }

  /**
   *
   * Returns index, of which player you want to kick.
   */
  public int getIndex() {
    return index;
  }
}
