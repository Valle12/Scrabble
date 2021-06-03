package net.message;

/**
 * a message class to send chat messages.
 *
 * @author ygarip
 */
public class ChatMessage extends Message {
  private final String msg;
  private final String username;

  /**
   * a constructor for creating a new ChatMessage.
   *
   * @param message the chatmessage that should be send
   */
  public ChatMessage(String message, String username) {
    super(MessageType.CHATMESSAGE);
    this.msg = message;
    this.username = username;
  }

  /**
   * a getter method.
   *
   * @return returns the msg
   */
  public String getMsg() {
    return this.msg;
  }

  /**
   * a getter method.
   *
   * @return Returns PlayerProfile attached to ChatMessage
   */
  public String getUsername() {
    return this.username;
  }
}
