package net.message;

import game.components.Tile;

/**
 * Message for toggling a tile on the board.
 *
 * @author ygarip
 */
public class PlaceTileMessage extends Message {

  private Tile tile;
  private int row;
  private int col;

  /**
   * Constructor for creating a new PlaceTileMessage.
   *
   * @param tile Requires Tile, which represents new tile on board
   * @param row Requires the row in board
   * @param col Requires the column in board
   */
  public PlaceTileMessage(Tile tile, int row, int col) {
    super(MessageType.PLACETILE);
    this.tile = tile;
    this.row = row;
    this.col = col;
  }

  /**
   * a method to set the Tile.
   *
   * @param tile Requires the Tile
   */
  public void setTile(Tile tile) {
    this.tile = tile;
  }

  /** Returns the Tile of the PlaceTileMessage. */
  public Tile getTile() {
    return tile;
  }

  /**  Returns the Row of the PlaceTileMessage. */
  public int getRow() {
    return row;
  }

  /**  Returns the Column of the PlaceTileMessage. */
  public int getCol() {
    return col;
  }
}
