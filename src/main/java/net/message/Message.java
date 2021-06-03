package net.message;

import java.io.Serializable;

/**
 * a abstract message class that represents communication of server and client.
 *
 * @author ygarip
 */
public abstract class Message implements Serializable {
  private static final long serialVersionUID = 1L;
  private MessageType messageType;

  /**
   * an abstract constructor for creating a new message.
   *
   * @param type an enumtype of messageType
   */
  public Message(MessageType type) {
    this.messageType = type;
  }

  /**
   * a method to get the MessageType of the message.
   *
   * @return returns the MessageType
   */
  public MessageType getMessageType() {
    return this.messageType;
  }
}
