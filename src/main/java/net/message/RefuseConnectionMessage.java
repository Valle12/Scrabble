package net.message;

/**
 * Message Class for sending, if ip was wrong.
 *
 * @author ygarip
 */
public class RefuseConnectionMessage extends Message {
  private String message;

  /**
   * a constructor to create a RefuseConnectionMessage.
   *
   * @param message Requires the Message to be sent
   */
  public RefuseConnectionMessage(String message) {
    super(MessageType.REFUSECONNECTION);
    this.message = message;
  }

  /** Returns the Message. */
  public String getMessage() {
    return this.message;
  }
}
