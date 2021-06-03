package net.message;

import client.PlayerProfile;

/**
 * An message that handles the player's connect Request to the server.
 *
 * @author ygarip
 */
public class ConnectMessage extends Message {
  private PlayerProfile profile;
  private PlayerProfile[] profiles;

  /**
   * a constructor to create a ConnectMessage with a PlayerProfile.
   *
   * @param profile Requires a PlayerProfile
   */
  public ConnectMessage(PlayerProfile profile) {
    super(MessageType.CONNECT);
    this.profile = profile;
  }

  /** Returns the PlayerProfile. */
  public PlayerProfile getProfile() {
    return profile;
  }

  /**
   * sets profile array in message.
   *
   * @param profiles requires profiles array to be set
   */
  public void setProfiles(PlayerProfile[] profiles) {
    this.profiles = profiles;
  }

  /** Returns to the connected player all connected PlayerProfiles. */
  public PlayerProfile[] getProfiles() {
    return this.profiles;
  }
}
