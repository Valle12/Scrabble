package net.message;

import game.components.Tile;

/**
 * a message class to get a Tile from the server.
 *
 * @author vkaczmar
 */
public class GiveTileMessage extends Message {
  private Tile tile;

  /** a constructor for creating a GiveTileMessage instance. */
  public GiveTileMessage(Tile tile) {
    super(MessageType.GIVETILE);
    this.tile = tile;
  }

  /** Returns Tile. */
  public Tile getTile() {
    return this.tile;
  }
}
