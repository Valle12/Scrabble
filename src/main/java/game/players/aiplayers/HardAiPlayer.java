package game.players.aiplayers;

import game.Dictionary;
import game.Game;
import game.components.Board;
import game.components.BoardException;
import game.components.BoardField;
import game.components.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Hard AI Player actor interacting with game. Inspiration by - "The World's Fastest Scrabble
 * Program" by ANDREW W. APPEL AND GUY J. * JACOBSON.
 *
 * @author yuzun
 */
public class HardAiPlayer extends AiPlayer {

  private static LexiconTree tree;
  // Root of Word Directed-Acyclic Word Graph!
  // if multiple hard bots exist, only once created!
  // Only used for hard ai up till now , I dont know what Nico will use :)
  private List<Placement> bestPlacements; // best placement

  public HardAiPlayer() {
    super(Difficulty.HARD);
  }

  /**
   * Assigns the bot the game instance which it partakes in. if no lexicon tree was built by a bot
   * prior to this one, it will create one here.
   */
  @Override
  public void joinGame(Game game) {
    super.joinGame(game); // assign game to this player
    if (tree == null) { // create only first time
      tree = new LexiconTree(game.getDictionary()); // Build word tree
    }
  }
  /**
   * Main method which is triggered by the game instance All computations from start of round till
   * end need to be done in here! START COMPUTATION in THREAD as to not block any functionalities of
   * server.
   */
  @Override
  public void think(Board gameBoard, Dictionary dictionary) {
    new Thread(() -> thinkInternal(gameBoard, dictionary)).start();
  }

  /**
   * Main method which is triggered by the game instance. All computations from start of round till
   * end need to be done in here.
   */
  public void thinkInternal(Board gameBoard, Dictionary dictionary) {
    Board board = new Board(gameBoard); // Deep copy of game board
    bestPlacements = new ArrayList<>();

    // fill placements with best computed solution
    // Check if there are already placements on board (check by roundnum doesnt work since
    //  first roun can be passed too
    boolean priorPlacements = false;
    for (int i = 0; i < Board.BOARD_SIZE; i++) {
      for (int j = 0; j < Board.BOARD_SIZE; j++) {
        if (!board.isEmpty(i, j)) {
          priorPlacements = true;
          break;
        }
      }
    }

    if (!priorPlacements) { //
      computeFirstRound(board); // fills placements with best computed solution
    } else {
      compute(board);
    }

    // if placement could be found --> execute it
    if (bestPlacements.size() > 0) {

      // play placements (with a little time in between to simulate single placements)
      for (Placement placement : bestPlacements) {
        placeTile(placement.getTile(), placement.getRow(), placement.getColumn());
        rack.remove(placement.getTile());
      }

    } else { // if placement could not be found --> randomly exchange tiles
      List<Tile> tilesToExchange = new ArrayList<>();
      for (int i = 0; i < Math.min(game.getBagSize(), rack.size()); i++) {
        if (rack.get(i).getLetter() == '#') { // Exchange any joker tiles
          tilesToExchange.add(rack.get(i));
        } else if (Math.random() < 0.5) { // 50 % chance to be exchanged
          tilesToExchange.add(rack.get(i));
        }
      }
      if (tilesToExchange.size() != 0) {
        exchange(tilesToExchange); // exchange
      } else {
        submit(); // pass
      }
    }
    submit();
  }

  /** Resets the board by undoing the given placements. */
  private void resetBoard(Board board, List<Placement> placements) {
    placements.forEach(
        placement -> board.placeTile(null, placement.getRow(), placement.getColumn()));
  }

  /**
   * Called if first round. Tries to build word with tiles on hand.
   *
   * @param board copy of game board
   */
  private void computeFirstRound(Board board) {
    long startTime = System.currentTimeMillis();
    String wordPattern = "";
    for (int i = 0; i < rack.size(); i++) {
      wordPattern += '#';
    }
    Set<String> possibleWords = tree.calculatePossibleWords(wordPattern, rack);

    // For every possible word try placing and evaluate
    int maxScore = 0;
    for (String word : possibleWords) {
      List<Placement> placements = new ArrayList<>();

      for (int i = 0; i < word.length(); i++) {
        Tile tile = getTile(word.charAt(i));
        board.placeTile(tile, 7 + i, 7); // place vertically
        placements.add(new Placement(tile, 7 + i, 7));
      }

      int score = evaluatePlacement(board, placements); // evaluate placement
      if (maxScore < score) {
        bestPlacements = placements;
        maxScore = score;
      }
    }

    flex(
        super.getProfile().getName()
            + " computed "
            + possibleWords.size()
            + " possible words in "
            + (System.currentTimeMillis() - startTime)
            + "ms");
  }

  /**
   * Computes best possible placement. Reduces dimensions to one by first checking only horizontal,
   * then vertical.
   *
   * @param board copy of game board
   */
  private void compute(Board board) {
    final long startTime = System.currentTimeMillis(); // track time
    List<List<Placement>> possiblePlacements = new ArrayList<>();
    // Traverse through every row
    for (int row = 0; row < Board.BOARD_SIZE; row++) {
      int col = 0;
      while (col < Board.BOARD_SIZE) {
        if (board.isEmpty(row, col)) { // skip if empty
          col++;
          continue;
        }

        // Count left starting point from where we would start trying to place tiles
        int leftStartingPoint = col;
        int counter = rack.size();
        while (leftStartingPoint > 0 && counter > 0) {
          if (!board.isEmpty(row, leftStartingPoint)) {
            counter--;
          }
          leftStartingPoint--;
        }

        // Build patterns & compute
        for (int i = leftStartingPoint; i <= col; i++) { // i places left
          String pattern = "";
          for (int j = i; j < Board.BOARD_SIZE; j++) {
            pattern += !board.isEmpty(row, j) ? board.getTile(row, j).getLetter() : '#';
          }
          Set<String> possibleWords = tree.calculatePossibleWords(pattern, rack); // COMPUTE

          // Calculate placements from word
          for (String word : possibleWords) {
            List<Placement> placements = new ArrayList<>();
            for (int j = 0; j < word.length(); j++) {
              if (board.isEmpty(row, i + j)) { // if empty place tile
                placements.add(new Placement(getTile(word.charAt(j)), row, i + j));
              }
            }
            possiblePlacements.add(placements);
          }
        }

        // skip till empty field again
        while (col < Board.BOARD_SIZE && !board.isEmpty(row, col)) {
          col++;
        }
      }
    }

    // Traverse through every column
    for (int col = 0; col < Board.BOARD_SIZE; col++) {
      int row = 0;
      while (row < Board.BOARD_SIZE) {
        if (board.isEmpty(row, col)) { // skip if empty
          row++;
          continue;
        }

        // Calculate top starting point from where we try to build our words
        int topStartingPoint = row;
        int counter = rack.size();
        while (topStartingPoint > 0 && counter > 0) {
          if (!board.isEmpty(topStartingPoint, col)) {
            counter--;
          }
          topStartingPoint--;
        }

        // Build patterns and compute
        for (int i = topStartingPoint; i <= row; i++) {
          String pattern = "";
          for (int j = i; j < Board.BOARD_SIZE; j++) {
            pattern += !board.isEmpty(j, col) ? board.getTile(j, col).getLetter() : '#';
          }
          Set<String> possibleWords = tree.calculatePossibleWords(pattern, rack); // COMPUTE

          // Calculate placements from word
          for (String word : possibleWords) {
            List<Placement> placements = new ArrayList<>();
            for (int j = 0; j < word.length(); j++) {
              if (board.isEmpty(i + j, col)) { // if empty place tile from hand
                placements.add(new Placement(getTile(word.charAt(j)), i + j, col));
              }
            }
            possiblePlacements.add(placements);
          }
        }

        // skip till empty field again
        while (row < Board.BOARD_SIZE && !board.isEmpty(row, col)) {
          row++;
        }
      }
    }
    // Try out placements and get best one
    int maxScore = 0;
    int count = 0; // valid placements
    for (List<Placement> placements : possiblePlacements) {
      if (placements.size() == 0) {
        continue;
      }
      int score = evaluatePlacement(board, placements);
      if (score > 0) {
        count++;
      }
      if (maxScore < score) {
        maxScore = score;
        bestPlacements = placements;
      }
    }
    flex(
        super.getProfile().getName()
            + " computed "
            + possiblePlacements.size()
            + " possible words and calculated best one out of "
            + count
            + " valid placements in "
            + (System.currentTimeMillis() - startTime)
            + "ms");
  }

  /** Returns a tile with given letter. */
  private Tile getTile(char letter) {
    for (Tile tile : rack) {
      if (tile.getLetter() == letter) {
        return tile;
      }
    }
    // should never be reached since method only called with values known to be in hand
    return null;
  }

  /**
   * Simulates placements on copy of board, evaluates score, resets board back to state prior to
   * placements.
   *
   * @param board board object
   * @param placements placements to be simulated and scored
   * @return score of placements (-1 if board was in an invalid state)
   */
  private int evaluatePlacement(Board board, List<Placement> placements) {
    List<BoardField> boardPlacements = new ArrayList<>();
    placements.forEach(
        placement -> {
          board.placeTile(placement.getTile(), placement.getRow(), placement.getColumn());
          boardPlacements.add(board.getField(placement.getRow(), placement.getColumn()));
        });
    try {
      board.check(boardPlacements, game.getDictionary(), false);
    } catch (BoardException be) {
      resetBoard(board, placements);
      return -1;
    }
    int score = board.evaluateScore(boardPlacements);
    resetBoard(board, placements);
    return score;
  }

  /** Class for single placement. */
  private class Placement {

    private final int row;
    private final int column;
    private final Tile tile;

    Placement(Tile tile, int row, int column) {
      this.tile = tile;
      this.row = row;
      this.column = column;
    }

    public int getRow() {
      return row;
    }

    public int getColumn() {
      return column;
    }

    public Tile getTile() {
      return tile;
    }
  }
}
