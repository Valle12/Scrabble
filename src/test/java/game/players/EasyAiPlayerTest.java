package game.players;

import game.Dictionary;
import game.Game;
import game.components.Board;
import game.components.Tile;
import game.players.aiplayers.EasyAiPlayer;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author nsiebler This class initally build by @author yuzun, used for the easy ai version too
 *     <p>Simple Ai Player Only implements think method
 */
class EasyAiPlayerTest extends EasyAiPlayer {

  private final int[] tileScores = {
    1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10
  };
  private final int[] tileDistributions = {
    9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2
  };
  private final Dictionary dictionary = new Dictionary();

  @Test
  void playGameTest() { // Play till end
    // set game bag
    List<Player> players = new LinkedList<>();
    players.add(new EasyAiPlayerTest());
    players.add(new EasyAiPlayerTest());
    players.add(new EasyAiPlayerTest());

    LinkedList<Tile> gameBag = new LinkedList<>();
    for (int i = 0; i < tileDistributions.length; i++) {
      for (int j = 0; j < tileDistributions[i]; j++) {
        if (i != tileDistributions.length - 1) { // Not joker
          gameBag.add(new Tile(((char) ('A' + i)), tileScores[i]));
        } else { // joker
          gameBag.add(new Tile('#', 0));
        }
      }
    }
    new Game(players, gameBag, dictionary).nextRound();
    players.forEach(player -> player.quit());
  }

  @Override
  public void think(Board gameBoard, Dictionary dictionary) {
    if (game.getRoundNumber() == 100) {
      game.end(0);
      return;
    }

    long startTime = System.currentTimeMillis();
    System.out.println("Before AI Move: ");
    printRack();
    printBoard(gameBoard);
    System.out.println("Start calculating...");

    super.thinkInternal(
        gameBoard, dictionary); // NOT A THREAD since JUNIT does not work well with it

    printRack();
    printBoard(gameBoard);
    System.out.println();
  }

  @Override
  public void addScore(int score) {
    super.addScore(score);
    System.out.println("--> scored " + score + " points");
    System.out.println();
  }

  @Override
  public void exchange(Collection<Tile> tiles) {
    System.out.print(super.getProfile().getName() + " wants to exchange Tiles: ");
    tiles.forEach(tile -> System.out.print(tile.getLetter() + " "));
    System.out.println();
    super.exchange(tiles);
  }

  private void printBoard(Board board) {
    for (int i = 0; i < Board.BOARD_SIZE; i++) {
      for (int j = 0; j < Board.BOARD_SIZE; j++) {
        System.out.print(
            board.getTile(i, j) == null ? " - " : " " + board.getTile(i, j).getLetter() + " ");
      }
      System.out.println();
    }
  }

  private void printRack() {
    rack.forEach(tile -> System.out.print(tile.getLetter()));
    System.out.println();
  }
}
