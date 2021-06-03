package net.message;

import game.components.Tile;

/**
 * a message class to request a tiles exchange with the server bag.
 *
 * @author vkaczmar
 */
public class ExchangeTileMessage extends Message {
  private Tile[] oldTiles;

  /**
   * a constructor to create a ExchangeTileMessage instance.
   *
   * @param oldTiles Requires tiles array with tiles you want to remove
   */
  public ExchangeTileMessage(Tile[] oldTiles) {
    super(MessageType.EXCHANGETILES);
    this.oldTiles = new Tile[oldTiles.length];
    for (int i = 0; i < this.oldTiles.length; i++) {
      this.oldTiles[i] = oldTiles[i];
    }
  }

  /** Returns the requested tiles to exchange. */
  public Tile[] getOldTiles() {
    return oldTiles;
  }
}
