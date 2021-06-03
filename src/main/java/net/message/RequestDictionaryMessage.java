package net.message;

/**
 * a message class to Request the dictionary from the Server.
 *
 * @author vkaczmar
 */
public class RequestDictionaryMessage extends Message {
  private String dictionary;

  /** a constructor to create a RequestDictionaryMessage. */
  public RequestDictionaryMessage() {
    super(MessageType.REQUESTDICTIONARY);
  }

  /**
   * Sets dictionary to dictionary handed over.
   *
   * @param dictionary Requires the dictionary as a String
   */
  public void setDictionary(String dictionary) {
    this.dictionary = dictionary;
  }

  /** Returns the dictionary in one String. */
  public String getDictionary() {
    return dictionary;
  }
}
