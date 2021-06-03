package game;

import ft.Sound;
import game.components.Board;
import game.components.BoardException;
import game.components.BoardField;
import game.components.Tile;
import game.players.Player;
import game.players.RemotePlayer;
import game.players.aiplayers.AiPlayer;
import net.message.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Single instance running on the hosted server Handles game flow and logic, manages * every
 * interaction with board and game bag.
 *
 * @author yuzun
 */
public class Game {

  private final List<Player>
      players; // Players playing this game (in opposite order of their turns)   [0: last player, 1:
  // second last player, ...]
  private final Board board; // Game Board
  private final LinkedList<Tile> bag; // bag of tiles in the game
  private final Dictionary dictionary; // Dictionary this game relies on
  private int roundsSinceLastScore = 0; // last n amount of rounds without points
  private int roundNum = -1; // amount of total rounds since start
  private Player playerInTurn; // Player whose turn it is
  private List<BoardField> placementsInTurn =
      new LinkedList<>(); // Placements on the board in the last turn

  /**
   * A game instance is created by the server, when the host decided to start the game A game
   * expects a list of players, an array in which the nth element represents the amount of tiles of
   * the (n+1)th letter in the alphabet (26th element is the joker tile) an array in which the nth
   * element represents the score of the (n+1)th letter in the alphabet (26th element is the joker
   * tile) When a game is created it sets the status 'running' to true creates a board creates the
   * tiles (amount & score determined by the given arrays), shuffles them and distributes them to
   * the players.
   */
  public Game(List<Player> players, LinkedList<Tile> bag, Dictionary dictionary) {

    this.dictionary = dictionary;
    this.bag = bag;

    // Players participating
    this.players = players;
    players.forEach(player -> player.joinGame(this));

    // Set up a board
    this.board = new Board();

    // Shuffle bag for certainty
    Collections.shuffle(bag);

    // Give each player 7 tiles and remove them from the bag
    for (Player player : players) {
      LinkedList<Tile> tilesToDistribute = new LinkedList<>();
      for (int i = 0; i < 7; i++) {
        tilesToDistribute.add(bag.pop());
      }
      player.addTilesToRack(tilesToDistribute);
    }
  }

  /**
   * Called to start a new round increments round number assigns turn, if it is the turn of an AI
   * player, then trigger it to to think and make a move.
   */
  public void nextRound() {

    // increment round number
    roundNum++;
    if (roundsSinceLastScore >= 6) { // six turns without anyone scoring points
      notify(new EndableGameMessage());
    }

    // reassign turns
    players.get(Math.abs(roundNum - 1) % players.size()).setTurn(false);
    playerInTurn = players.get(roundNum % players.size());
    playerInTurn.setTurn(true);

    placementsInTurn = new LinkedList<>();

    // If player is Ai, then trigger it to think
    if (!playerInTurn.isHuman()) { // player is not human
      AiPlayer ai = (AiPlayer) playerInTurn;
      ai.think(board, dictionary);
    }
  }

  /**
   * Removes the tiles given to the game from the player's rack Gets equal amount of tiles out of
   * the bag and adds them to the rack of the player The new tiles from the player are put back in
   * the bag and the bag gets shuffled.
   *
   * @param tilesFromPlayer collection of tiles the player wants to exchange
   */
  public void exchange(Collection<Tile> tilesFromPlayer) {

    Collections.shuffle(bag);
    Collection<Tile> tilesFromBag = new LinkedList<>(); // tiles that are to be given back

    for (int i = 0; i < tilesFromPlayer.size(); i++) {
      tilesFromBag.add(bag.pop());
    }

    bag.addAll(tilesFromPlayer);
    playerInTurn.addTilesToRack(tilesFromBag);

    // Notify other about this event
    String message = playerInTurn.getProfile().getName() + " exchanged tiles!";
    notify(new ChatMessage(message, null));

    nextRound();
  }

  /**
   * Places give tile on field at given row and column if field is empty. Adds placement to the list
   * of placements this turn. Checks new board state.
   */
  public void placeTile(Tile tile, int row, int col) {

    if (board.isEmpty(row, col)) {
      if (playerInTurn.isHuman()) {
        Sound.playMusic(Sound.tileSet);
      }
      board.placeTile(tile, row, col);
      placementsInTurn.add(board.getField(row, col));
      notify(new PlaceTileMessage(tile, row, col));
    }
  }

  /**
   * Removes tile at given row and column. Removes placement from the list of placements in this
   * turn. Checks new board state. If field was empty, then nothing happens.
   */
  public void removeTile(int row, int col) {
    if (!board.isEmpty(row, col)) {
      board.placeTile(null, row, col);
      placementsInTurn.remove(board.getField(row, col));
      notify(new PlaceTileMessage(null, row, col));
    }
  }

  /**
   * Ends game (can also be called by incoming EXCEEDED_TIME_MESSAGE or * HOST_DISCONNECT_MESSAGE)
   * If ended abruptly, then because of a user running out of time --> * removing pending placements
   * from the board Else just end normally and show every human * player scoreboard. No checks here.
   *
   * @author ygarip
   */
  public void end(int type) {
    for (Player player : players) {
      player.setTurn(false);
    }

    int winnerScore = 0;
    for (Player p : players) {
      winnerScore = Math.max(winnerScore, p.getScore());
    }

    // Found words list 2D Array
    String[][] foundWords = new String[players.size()][];
    for (int i = 0; i < foundWords.length; i++) {
      List<String> words = players.get(i).getFoundWords();
      foundWords[i] = new String[words.size()];
      for (int j = 0; j < foundWords[i].length; j++) {
        foundWords[i][j] = words.get(j);
      }
    }

    for (Player p : players) {
      if (p.isHuman()) {
        ((RemotePlayer) p)
            .getConnection()
            .sendToClient(
                new EndGameMessage(type, p.getScore() == winnerScore, p.getScore(), foundWords));
      }
    }
  }

  /**
   * Called when player submits/passes turn. Checks if submission was valid, calculates scores new
   * found words, assigns them and gives back tiles from game bag to player.
   */
  public void submit() {

    try { // Try checking board which throws BoardException if any checks fail
      if (placementsInTurn.size() != 0) {
        board.check(placementsInTurn, dictionary, true); // dont check if no placements
      }

      // add new found words to player
      List<String> foundWords = board.getFoundWords();
      playerInTurn.addFoundWords(foundWords);

      // if not thrown error by now then board state valid
      int score = evaluateScore();
      if (score == 0) {
        roundsSinceLastScore++;
      } else {
        roundsSinceLastScore = 0;
      }

      // Notify which words were found & Score
      if (score > 0) { // if words found
        if (playerInTurn.isHuman()) {
          Sound.playMusic(Sound.rightWord);
        }
        String message = playerInTurn.getProfile().getName() + " found: ";
        for (String word : foundWords) {
          message += "\n- " + word + ": " + dictionary.getMeaning(word);
        }
        message += "\n --> scored " + score + " points!";
        notify(new ChatMessage(message, null));
      } else { // Passed
        String message = playerInTurn.getProfile().getName() + " passed";
        notify(new ChatMessage(message, null));
      }

      List<Tile> tileRefill = new LinkedList<>();
      for (int i = 0; i < Math.min(placementsInTurn.size(), bag.size()); i++) {
        tileRefill.add(bag.pop());
      }
      playerInTurn.addTilesToRack(tileRefill);
      playerInTurn.addScore(score);

      nextRound();
    } catch (BoardException e) {
      // Send SubmitMoveMessage back, acts as RejectMessage
      playerInTurn.rejectSubmission(
          e.getMessage()); // Reject player's submission with reason of exception
    }
  }

  /**
   * Resets board to last valid state and puts tiles into game bag Use only if player suddenly quit.
   */
  public void resetBoard() {
    placementsInTurn.forEach(
        field -> {
          bag.add(field.getTile()); // put back to bag
          removeTile(field.getRow(), field.getColumn()); // remove
        });
  }

  /***
   * Evaluates the score of the play in the last turn. Iterates over placements in last turn and
   * only ever starts evaluating if placement is to the most top/left placement of all placements
   * in last turn of the specific word it is forming.
   * @return score of play in last turn
   */
  public int evaluateScore() {
    return board.evaluateScore(placementsInTurn);
  }

  /** Returns amount of tiles int game bag. */
  public int getBagSize() {
    return bag.size();
  }

  /** returns game board the game is based on. */
  public Board getBoard() {
    return board;
  }

  /** returns dictionary the game is based on. */
  public Dictionary getDictionary() {
    return this.dictionary;
  }

  /** returns round number. */
  public int getRoundNumber() {
    return roundNum + 1;
  }

  /**
   * Creates System message This method is only used by the AI to flex with possible placements,
   * processing time, etc. ;).
   */
  public void notify(Message m) {
    for (Player player : players) {
      if (player.isHuman()) {
        RemotePlayer remotePlayer = (RemotePlayer) player;
        remotePlayer.getConnection().sendToClient(m);
      }
    }
  }

  /**
   * Returns list of player taking part in game.
   *
   * @return Returns list of players
   */
  public List<Player> getPlayers() {
    return players;
  }
}
