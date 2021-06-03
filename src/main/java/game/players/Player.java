package game.players;

import client.PlayerProfile;
import game.Game;
import game.components.Tile;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Actor interacting with game (exists only on the server).
 *
 * @author yuzun
 */
public abstract class Player {

  private final boolean human;
  private final ArrayList<String> foundWords = new ArrayList<>();
  protected Game game;
  private PlayerProfile profile;
  private boolean turn;
  private ArrayList<Tile> rack;

  private int score;

  public Player(PlayerProfile profile, boolean human) {
    this.profile = profile;
    this.human = human;
    rack = new ArrayList<>();
  }

  public Player(boolean human) {
    this.profile = null;
    this.human = human;
  }

  // Starter

  /** Called by game when game is instantiated to bind both objects. */
  public void joinGame(Game game) {
    this.game = game;
  }

  // ========= interactions TO game ===================

  /** Places tile on game board (only called if it is player's turn). */
  public void placeTile(Tile tile, int row, int col) {
    if (turn) {
      game.placeTile(tile, row, col);
    }
  }

  /** Removes tile from game board if turn. */
  public void removeTile(int row, int col) {
    if (turn) {
      game.removeTile(row, col);
    }
  }

  /** Submit game turn if turn. */
  public void submit() {
    if (turn) {
      game.submit();
    }
  }

  /**
   * Exchange tiles. Game calls addTilesToRack method and adds tiles on its own.
   *
   * @param tiles tiles to be exchanged
   */
  public void exchange(Collection<Tile> tiles) {
    if (turn) {
      game.exchange(tiles);
    }
  }

  /** Quit from game. Player is sent all relevant information. */
  public abstract void quit();

  // ======== interactions FROM game ==================

  /** Returns true if its player's turn. */
  public boolean isTurn() {
    return turn;
  }

  /** Sets turn of player. */
  public void setTurn(boolean turn) {
    this.turn = turn;
  }

  /** Add tile to player's rack. */
  public abstract void addTilesToRack(Collection<Tile> tiles);

  /** Adds score. */
  public void addScore(int score) {
    this.score += score;
  }

  /** Returns score. */
  public int getScore() {
    return score;
  }

  /** Add to found words. If list is size 0, this operation is skipped. */
  public void addFoundWords(Collection<String> words) {
    if (words.size() > 0) {
      foundWords.addAll(words);
    }
  }

  /** Get list of found words. */
  public ArrayList<String> getFoundWords() {
    return foundWords;
  }

  /** Returns true if player is human. */
  public boolean isHuman() {
    return human;
  }

  /** Returns profile. */
  public PlayerProfile getProfile() {
    return profile;
  }

  /** Sets profile of player. */
  public void setPlayerProfile(PlayerProfile profile) {
    this.profile = profile;
  }

  /**
   * Submission invalid. Logic on what to do if game rejects submission should be implement in
   * subclasses.
   */
  public abstract void rejectSubmission(String reason);
}
