package net.message;

/**
 * Message Class for signaling, that you are ready when you are in a lobby.
 *
 * @author vkaczmar
 */
public class PlayerReadyMessage extends Message {
  private boolean ready = false;
  private boolean[] values;

  /**
   * Constructor for creation.
   *
   * @param ready true, if player is read
   */
  public PlayerReadyMessage(boolean ready) {
    super(MessageType.PLAYERREADY);
    this.ready = ready;
  }

  /** Returns readyState. */
  public boolean getReady() {
    return this.ready;
  }

  /**
   * a method to set the ready state of the player.
   *
   * @param ready Requires the boolean value of ready
   */
  public void setReady(boolean ready) {
    this.ready = ready;
  }

  /**
   * method to set the values that should be sent with the PlayerReadyMessage.
   *
   * @param values Requires the boolean values
   */
  public void setValues(boolean[] values) {
    this.values = values;
  }

  /** Returns boolean[] with all ready values for all players. */
  public boolean[] getValues() {
    return this.values;
  }
}
