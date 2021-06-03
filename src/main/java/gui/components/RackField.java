package gui.components;

import game.components.Tile;

import java.io.Serializable;

/**
 * Single tile on the rack which can hold a tile.
 *
 * @author mnetzer
 */
public class RackField implements Serializable {

  private Tile tile; // Tile placed in this spot
  private boolean selected = false; // true if player selects this field
  private int col; // Column on the rack this field is located at

  /** Initialize the field with the type of field this instance represents. */
  public RackField(int col) {
    this.col = col;
  }

  /** Returns true if field is empty. */
  public boolean isEmpty() {
    return tile == null;
  }

  /** Returns the tile this field holds (null if none). */
  public Tile getTile() {
    return tile;
  }

  /** Places the given tile on this field. */
  public void setTile(Tile tile) {
    this.tile = tile;
  }

  /** Returns true if placement on board is valid. */
  public boolean isSelected() {
    return selected;
  }

  /** Sets validity of field. */
  public void setSelected(boolean bool) {
    this.selected = bool;
  }

  /** Returns column this field is located at on the board. */
  public int getColumn() {
    return col;
  }
}
