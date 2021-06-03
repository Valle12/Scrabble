package game.players;

import client.PlayerProfile;
import game.components.Tile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Player test class for tests.
 * @author yuzun
 */
public class TestPlayer extends Player {

  private final ArrayList<Tile> rack = new ArrayList<>();

  public TestPlayer(String username) {
    super(new PlayerProfile(username, 0, 0, 0, 0, LocalDate.now(), LocalDate.now()), true);
  }

  /** Quit from game. Player is sent all relevant information. */
  @Override
  public void quit() {
    System.out.println("Ended");
  }


  /**
   * Add tile to player's rack.
   *
   * @param tiles tiles to add rack
   */
  @Override
  public void addTilesToRack(Collection<Tile> tiles) {
    tiles.addAll(tiles);
  }

  /**
   * Submission invalid. Logic on what to do if game rejects submission should be implement in
   * subclasses.
   *
   * @param reason reason for rejection
   */
  @Override
  public void rejectSubmission(String reason) {
    System.out.println("Move invalid because: " + reason);
  }
}
