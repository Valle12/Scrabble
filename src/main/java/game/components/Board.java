package game.components;

import game.Dictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The board is the main object in the game of scrabble. This class handles all interactions with
 * the board itself. -- Integrated Board Validity Check. -- Score evaluation. -- Saves all found
 * words.
 *
 * @author yuzun
 */
public class Board implements Serializable {

  public static final int BOARD_SIZE = 15; // width and height of board
  private final BoardField[][] fields; // 2D array to represent the board fields
  private final List<String> allFoundWords = new ArrayList<>(); // all found words
  private List<String> newFoundWords = new ArrayList<>(); // new found words since last check

  /** Initializes an empty board. */
  public Board() {
    fields = new BoardField[BOARD_SIZE][BOARD_SIZE];

    // Initializing one half of the board and then mirroring
    fields[7][7] = new BoardField(BoardField.FieldType.STAR, 7, 7);

    fields[0][0] = new BoardField(BoardField.FieldType.TWS, 0, 0);
    fields[0][7] = new BoardField(BoardField.FieldType.TWS, 0, 7);
    fields[7][0] = new BoardField(BoardField.FieldType.TWS, 7, 0);
    fields[14][0] = new BoardField(BoardField.FieldType.TWS, 14, 0);
    fields[14][7] = new BoardField(BoardField.FieldType.TWS, 14, 7);

    fields[1][1] = new BoardField(BoardField.FieldType.DWS, 1, 1);
    fields[13][1] = new BoardField(BoardField.FieldType.DWS, 13, 1);
    fields[2][2] = new BoardField(BoardField.FieldType.DWS, 2, 2);
    fields[12][2] = new BoardField(BoardField.FieldType.DWS, 12, 2);
    fields[3][3] = new BoardField(BoardField.FieldType.DWS, 3, 3);
    fields[11][3] = new BoardField(BoardField.FieldType.DWS, 11, 3);
    fields[4][4] = new BoardField(BoardField.FieldType.DWS, 4, 4);
    fields[10][4] = new BoardField(BoardField.FieldType.DWS, 10, 4);

    fields[1][5] = new BoardField(BoardField.FieldType.TLS, 1, 5);
    fields[5][1] = new BoardField(BoardField.FieldType.TLS, 5, 1);
    fields[5][5] = new BoardField(BoardField.FieldType.TLS, 5, 5);
    fields[9][1] = new BoardField(BoardField.FieldType.TLS, 9, 1);
    fields[9][5] = new BoardField(BoardField.FieldType.TLS, 9, 5);
    fields[13][5] = new BoardField(BoardField.FieldType.TLS, 13, 5);

    fields[0][3] = new BoardField(BoardField.FieldType.DLS, 0, 3);
    fields[2][6] = new BoardField(BoardField.FieldType.DLS, 2, 6);
    fields[3][0] = new BoardField(BoardField.FieldType.DLS, 3, 0);
    fields[3][7] = new BoardField(BoardField.FieldType.DLS, 3, 7);
    fields[6][2] = new BoardField(BoardField.FieldType.DLS, 6, 2);
    fields[6][6] = new BoardField(BoardField.FieldType.DLS, 6, 6);
    fields[7][3] = new BoardField(BoardField.FieldType.DLS, 7, 3);
    fields[8][2] = new BoardField(BoardField.FieldType.DLS, 8, 2);
    fields[8][6] = new BoardField(BoardField.FieldType.DLS, 8, 6);
    fields[11][0] = new BoardField(BoardField.FieldType.DLS, 11, 0);
    fields[11][7] = new BoardField(BoardField.FieldType.DLS, 11, 7);
    fields[12][6] = new BoardField(BoardField.FieldType.DLS, 12, 6);
    fields[14][3] = new BoardField(BoardField.FieldType.DLS, 14, 3);

    // Fill rest of fields with normal fields
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j <= 7; j++) {
        if (fields[i][j] == null) {
          fields[i][j] = new BoardField(BoardField.FieldType.NORMAL, i, j);
        }
      }
    }

    // Mirror
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < 7; j++) {
        fields[i][BOARD_SIZE - j - 1] =
            new BoardField(fields[i][j].getType(), i, BOARD_SIZE - j - 1);
      }
    }
  }

  /** Creates deep copy of board (Used by AiPlayer to test moves without interacting with game). */
  public Board(Board board) {
    fields = new BoardField[BOARD_SIZE][BOARD_SIZE];
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        this.fields[i][j] = new BoardField(board.getField(i, j).getType(), i, j);
        Tile tile = board.getTile(i, j);
        if (tile != null) {
          placeTile(new Tile(tile.getLetter(), tile.getScore()), i, j);
        }
      }
    }
  }

  /**
   * Set given tile at given row and column.
   *
   * @param tile Tile to place
   * @param row Row number of field on board (starting from 0)
   * @param col Column number of field on board (starting from 0)
   */
  public void placeTile(Tile tile, int row, int col) {
    fields[row][col].setTile(tile);
  }

  /**
   * Returns true if field is empty.
   *
   * @param row Row number of field on board (starting from 0)
   * @param col Column number of field on board (starting from 0)
   * @return true, if field at given row and col on board is empty
   */
  public boolean isEmpty(int row, int col) {
    return fields[row][col].isEmpty();
  }

  /**
   * Returns the tile at given row and column.
   *
   * @param row Row number of field on board (starting from 0)
   * @param col Column number of field on board (starting from 0)
   * @return tile at given row and column on board
   */
  public Tile getTile(int row, int col) {
    return fields[row][col].getTile();
  }

  /**
   * Returns the BoardField at given row and column counting from 0.
   *
   * @param row Row number of field on board (starting from 0)
   * @param col Column number of field on board (starting from 0)
   * @return field at given row and column on board
   */
  public BoardField getField(int row, int col) {
    return fields[row][col];
  }

  /**
   * Only Checks validity of placements (Prior board state is assumed to be true). Makes it
   * efficient! Checks validity of board state checks if STAR field is covered, checks that there
   * are no tiles not adjacent to others, checks that valid words are formed on the board based on
   * the dictionary, As it checks it sets fields which hold invalid tiles as invalid (field.valid =
   * false).
   *
   * @param placements placements in the last turn
   * @param dictionary Dictionary the game is based on
   * @param save if true, found words lists will be updated (important as to not call it with bots
   *     since iterating thousands of times)
   * @throws BoardException if board state is invalid with message as to why Board was invalid
   */
  public void check(List<BoardField> placements, Dictionary dictionary, boolean save)
      throws BoardException {

    // Every field is valid default
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        fields[i][j].setValid(true);
      }
    }

    // Check: Star field is covered
    //  --> mark every tile on board as invalid
    if (fields[7][7].isEmpty()) {
      placements.forEach(bf -> bf.setValid(false));
      throw new BoardException("Star Field is not covered");
    }

    // Check placements are all in a specific row or column
    Set<Integer> uniqueRows = new TreeSet<>();
    Set<Integer> uniqueColumns = new TreeSet<>();

    for (BoardField bf : placements) {
      uniqueRows.add(bf.getRow());
      uniqueColumns.add(bf.getColumn());
    }

    if (uniqueRows.size() > 1 && uniqueColumns.size() > 1) {
      throw new BoardException("Placements can only be in a single line or column");
    }

    // Check adjacency to ANY tile
    boolean placementAdjCheck = true;
    for (BoardField bf : placements) {

      int i = bf.getRow(); // row
      int j = bf.getColumn(); // column

      // Check if adjacent to other tiles
      if (i > 0 && !fields[i - 1][j].isEmpty() // up
          || i < BOARD_SIZE - 1 && !fields[i + 1][j].isEmpty() // down
          || j > 0 && !fields[i][j - 1].isEmpty() // left
          || j < BOARD_SIZE - 1 && !fields[i][j + 1].isEmpty()) { // right

        // placement is adjacent to other placements
        bf.setValid(true);
      } else {
        placementAdjCheck = false;
        bf.setValid(false);
      }
    }
    if (!placementAdjCheck) {
      throw new BoardException("Placements cannot stand alone");
    }

    // Check placements forming word with at least one tile NOT placed this turn IF NOT BOARD EMPTY
    // WITHOUT PLACEMENTS(ROUND 1)
    boolean firstPlacements = true;
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (!isEmpty(i, j)
            && !placements.contains(
                getField(i, j))) { // there is a placement on board excluding placements
          firstPlacements = false;
          break;
        }
      }
    }
    boolean placementCheckOther = true;
    for (BoardField bf : placements) {

      int i = bf.getRow();
      int j = bf.getColumn();

      boolean formsWordOther = false; // forms word with at least one tile NOT placed this turn

      // UP
      int k = 1;
      while (i - k >= 0) {
        if (fields[i - k][j].isEmpty()) {
          break;
        }
        if (!placements.contains(fields[i - k][j])) { // non-empty field was not placed this turn
          formsWordOther = true;
        }
        k++;
      }

      // DOWN
      k = 1;
      while (i + k < BOARD_SIZE
          && !formsWordOther) { // skip if already forms word with tile NOT placed this turn
        if (fields[i + k][j].isEmpty()) {
          break;
        }
        if (!placements.contains(fields[i + k][j])) { // non-empty field was not placed this turn
          formsWordOther = true;
        }
        k++;
      }

      // RIGHT
      k = 1;
      while (j + k < BOARD_SIZE
          && !formsWordOther) { // skip if already forms word with tile NOT placed this turn
        if (fields[i][j + k].isEmpty()) {
          break;
        }
        if (!placements.contains(fields[i][j + k])) { // non-empty field was not placed this turn
          formsWordOther = true;
        }
        k++;
      }

      // LEFT
      k = 1;
      while (j - k >= 0
          && !formsWordOther) { // skip if already forms word with tile NOT placed this turn
        if (fields[i][j - k].isEmpty()) {
          break;
        }
        if (!placements.contains(fields[i][j - k])) { // non-empty field was not placed this turn
          formsWordOther = true;
        }
        k++;
      }
      bf.setValid(formsWordOther);
      placementCheckOther = placementCheckOther && formsWordOther;
    }

    if (!placementCheckOther && !firstPlacements) { // if not adjacent to others AND others exist
      throw new BoardException("Placements must join together with tiles on board");
    }

    // Check: Valid words are formed (Dictionary
    List<String> allNewFoundWords = new ArrayList<>(); // all found words
    // Check horizontal
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE - 1; j++) {

        // if this field or field to the right is empty then skip
        if (isEmpty(i, j) || isEmpty(i, j + 1)) {
          continue;
        }

        // traverse to the right until field is empty or out of bounds
        String word = ""; // horizontal word
        int k = j; // column of first tile of the word
        while (k < BOARD_SIZE && !isEmpty(i, k)) {
          word += getTile(i, k++).getLetter(); // Append tile letter to found word
        }
        if (!dictionary.wordExists(word)) {
          throw new BoardException("Word " + word + " was not recognized");
        } else if (save) {
          allNewFoundWords.add(word);
        }
        j = k; // sets j to the field after the word (out of bounds or empty)
      }
    }

    // Check vertical
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE - 1; j++) {

        // if this field or field to the right is empty then skip
        if (isEmpty(j, i) || isEmpty(j + 1, i)) {
          continue;
        }

        // traverse to the right until field is empty or out of bounds
        String word = ""; // horizontal word
        int k = j; // row of first tile of the word
        while (k < BOARD_SIZE && !isEmpty(k, i)) {
          word += getTile(k++, i).getLetter(); // Append tile letter to found word
        }
        if (!dictionary.wordExists(word)) {
          throw new BoardException("Word " + word + " was not recognized");
        } else if (save) {
          allNewFoundWords.add(word);
        }
        j = k; // sets j to the field after the word (out of bounds or empty)
      }
    }

    // if reached here an no exception thrown --> valid !

    // get difference between all new found words and all found words --> new found words
    if (save) {
      this.allFoundWords.forEach(
          allNewFoundWords::remove); // remove old found words from whole list
      this.newFoundWords = allNewFoundWords; // list of newfound words
      allFoundWords.addAll(this.newFoundWords); // add new found list to whole list
    }
  }

  /**
   * Evaluates the score of the play in the last turn (Efficient). Iterates over placements in last
   * turn and only ever starts evaluating if placement is to the most top/left placement of all
   * placements in last turn of the specific word it is forming.
   *
   * @param placementsInTurn Placements which shall be evaluated
   * @return total score of placements
   */
  public int evaluateScore(List<BoardField> placementsInTurn) {
    int totalScore = 0;
    // Iterate over placements of last turn
    for (BoardField bf : placementsInTurn) {
      BoardField helper = bf;

      // check to the left if other placement exists there (would be evaluated in that spec.
      // iteration
      boolean formsWordHorizontal =
          (bf.getColumn() - 1 >= 0 && !this.isEmpty(bf.getRow(), bf.getColumn() - 1))
              || (bf.getColumn() + 1 < Board.BOARD_SIZE
                  && !this.isEmpty(
                      bf.getRow(),
                      bf.getColumn() + 1)); // true if left or right of placement exists

      boolean leftmostPlacement = true; // is true if only placement to the left
      // traverse left
      while (helper.getColumn() > 0
          && !getField(helper.getRow(), helper.getColumn() - 1).isEmpty()
          && formsWordHorizontal) {
        helper = this.getField(helper.getRow(), helper.getColumn() - 1);
      }
      // traverse back to our placement
      while (helper != bf) {
        if (placementsInTurn.contains(helper)) {
          leftmostPlacement = false;
          break;
        }
        helper = this.getField(helper.getRow(), helper.getColumn() + 1);
      }

      // check to the top if other placement this turn exists there (would be evaluated in that
      // spec. iteration)
      boolean formsWordVertical =
          (bf.getRow() - 1 >= 0 && !this.isEmpty(bf.getRow() - 1, bf.getColumn()))
              || (bf.getRow() + 1 < Board.BOARD_SIZE
                  && !this.isEmpty(
                      bf.getRow() + 1,
                      bf.getColumn())); // true if above or below of placement exists tile
      boolean topmostPlacement = true; // is true if top of placement in formed word
      helper = bf;
      // traverse up
      while (helper.getRow() > 0
          && !getField(helper.getRow() - 1, helper.getColumn()).isEmpty()
          && formsWordVertical) {
        helper = this.getField(helper.getRow() - 1, helper.getColumn());
      }
      // traverse back to our placement
      while (helper != bf) {
        if (placementsInTurn.contains(helper)) {
          topmostPlacement = false;
          break;
        }
        helper = this.getField(helper.getRow() + 1, helper.getColumn());
      }

      // if leftmost placement & to the left/right tiles exist--> evaluate horizontal
      if (leftmostPlacement && formsWordHorizontal) {
        int wordScore = 0;
        int wordMult = 1;

        // traverse to the left
        helper = bf;
        while (helper.getColumn() - 1 >= 0
            && !this.isEmpty(helper.getRow(), helper.getColumn() - 1)) {
          helper = this.getField(helper.getRow(), helper.getColumn() - 1);
        }

        // traverse to the right
        while (helper.getColumn() < Board.BOARD_SIZE && !helper.isEmpty()) {
          int letterScore = helper.getTile().getScore();
          int letterMult = 1;

          // check if tile was placed last turn
          if (!placementsInTurn.contains(helper)) { // tile was not placed last turn
            wordScore += letterScore;
          } else { // tile was placed last turn
            // check field type and evaluate multipliers
            switch (helper.getType()) {
              case STAR:
              case DWS:
                wordMult *= 2;
                break;
              case TWS:
                wordMult *= 3;
                break;
              case DLS:
                letterMult *= 2;
                break;
              case TLS:
                letterMult *= 3;
                break;
              default:
            }
            wordScore += (letterScore * letterMult);
          }
          if (helper.getColumn() >= Board.BOARD_SIZE - 1) { // break if last column
            break;
          }
          helper = this.getField(helper.getRow(), helper.getColumn() + 1);
        }
        totalScore += (wordScore * wordMult);
      }

      // if topmost placement & tiles exist above or below --> evaluate vertical
      if (topmostPlacement && formsWordVertical) {
        int wordScore = 0;
        int wordMult = 1;
        // traverse up
        helper = bf;
        while (helper.getRow() - 1 >= 0 && !this.isEmpty(helper.getRow() - 1, helper.getColumn())) {
          helper = this.getField(helper.getRow() - 1, helper.getColumn());
        }
        // traverse down
        while (helper.getRow() < Board.BOARD_SIZE && !helper.isEmpty()) {
          int letterScore = helper.getTile().getScore();
          int letterMult = 1;

          // check if tile was placed last turn
          if (!placementsInTurn.contains(helper)) { // tile was not placed last turn
            wordScore += letterScore;
          } else { // tile was placed last turn
            // check field type and evaluate multipliers
            switch (helper.getType()) {
              case STAR:
              case DWS:
                wordMult *= 2;
                break;
              case TWS:
                wordMult *= 3;
                break;
              case DLS:
                letterMult *= 2;
                break;
              case TLS:
                letterMult *= 3;
                break;
              default:
            }
            wordScore += (letterScore * letterMult);
          }
          if (helper.getRow() >= Board.BOARD_SIZE - 1) { // break if last row
            break;
          }
          helper = this.getField(helper.getRow() + 1, helper.getColumn());
        }
        totalScore += (wordScore * wordMult);
      }
    }

    if (placementsInTurn.size() == 7) { // if all tiles from rack were placed --> BONUS
      totalScore += 50;
    }
    return totalScore;
  }

  /** returns found words up till now (filled in check). */
  public List<String> getFoundWords() {
    return newFoundWords;
  }
}
