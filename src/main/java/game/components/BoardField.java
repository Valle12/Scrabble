package game.components;

import java.io.Serializable;

/**
 * Single field on the board which can hold a tile.
 *
 * @author yuzun
 */
public class BoardField implements Serializable {

  private final FieldType type; // Type of field
  private Tile tile; // Tile placed on this field
  private boolean valid = true; // true if valid placement was made on this field
  private final int row; // Row on the board this field is located at
  private final int col; // Column on the board this field is located at

  /** Initialize the field with the type of field this instance represents. */
  public BoardField(FieldType type, int row, int col) {
    this.type = type;
    this.row = row;
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

  /** Returns the type of this field. */
  public FieldType getType() {
    return type;
  }

  /** Returns true if placement on board is valid. */
  public boolean isValid() {
    return valid;
  }

  /** Sets validity of field. */
  public void setValid(boolean valid) {
    this.valid = valid;
  }

  /** Returns row this field is located at on the board. */
  public int getRow() {
    return row;
  }

  /** Returns column this field is located at on the board. */
  public int getColumn() {
    return col;
  }

  /** Field Classification for different squares. */
  public enum FieldType {
    NORMAL,
    STAR,
    DWS,
    DLS,
    TLS,
    TWS
  }
}
