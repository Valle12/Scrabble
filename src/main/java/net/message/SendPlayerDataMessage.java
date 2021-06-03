package net.message;

import client.PlayerProfile;

/**
 * a message class to send the data of the player.
 *
 * @author vkaczmar
 */
public class SendPlayerDataMessage extends Message {
  private PlayerProfile profile;
  private int id;

  /**
   * a constructor to create a SendPlayerDataMessage.
   *
   * @param id requires id
   */
  public SendPlayerDataMessage(int id) {
    super(MessageType.SENDPLAYERDATA);
    this.id = id;
  }

  /** Returns profile from Message. */
  public PlayerProfile getProfile() {
    return profile;
  }

  /**
   * Method for setting the playerProfile inside the message.
   *
   * @param profile requires the playerProfile
   */
  public void setProfile(PlayerProfile profile) {
    this.profile = profile;
  }

  /** Returns the id of the player message. */
  public int getId() {
    return id;
  }
}
