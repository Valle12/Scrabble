package game.players.aiplayers;

import client.PlayerProfile;
import game.Dictionary;
import game.components.Board;
import game.components.Tile;
import game.players.Player;
import net.message.ChatMessage;
import net.message.TurnMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/***
 * AI player/actor in game. Provides method 'think' which is the main feature.
 * @author yuzun
 */

public abstract class AiPlayer extends Player {

  private static final List<String> botNames =
      new LinkedList<>(
          Arrays.asList(
              "Yilmaz",
              "Valentin",
              "Vincent",
              "Yasin",
              "Max",
              "Nicolas")); // bots get random names which is removed from collection then (no
  private final String name;
  // duplicates)
  protected ArrayList<Tile> rack = new ArrayList<>();
  private final Difficulty difficulty;

  /** Initializes Bot with random name of collection and removes it from list of available names. */
  public AiPlayer(Difficulty difficulty) {
    super(new PlayerProfile("", 0, 0, 0, 0, LocalDate.now(), LocalDate.now()), false);

    this.difficulty = difficulty;

    // Get random name
    int randomIdx = (int) (Math.random() * botNames.size()); // random number 0-5
    name = botNames.get(randomIdx);

    // Set name of profile to 'Bot <name>' and remove name from list
    super.getProfile()
        .setName("Bot " + name + " (" + (difficulty == Difficulty.EASY ? "Easy" : "Hard") + ")");
    botNames.remove(randomIdx);
  }

  /**
   * Main method which is triggered by the game instance. All computations from start of round till
   * end need to be done in here.
   */
  public abstract void think(Board board, Dictionary dictionary);

  /** Adds given tiles to rack. */
  @Override
  public void addTilesToRack(Collection<Tile> tiles) {
    rack.addAll(tiles);
  }

  /** Quit from game. Set username back to list of available bot names. */
  public void quit() {
    // Adds botName back to 'pool' of available names
    botNames.add(name);
  }

  /**
   * Something must have gone veeeereeeeey wrong in the code, since we checked before if it is
   * valid.
   */
  @Override
  public void rejectSubmission(String reason) {
    System.out.println("CRITICAL ERROR");
  }

  /** Sends a TurnMessage. */
  @Override
  public void setTurn(boolean turn) {
    super.setTurn(turn);
    if (turn) {
      boolean[] turns = new boolean[game.getPlayers().size()];
      int[] scores = new int[game.getPlayers().size()];
      for (int i = 0; i < turns.length; i++) {
        turns[i] = game.getPlayers().get(i).isTurn();
        scores[i] = game.getPlayers().get(i).getScore();
      }
      game.notify(new TurnMessage(false, turns, game.getBagSize(), scores));
    }
  }

  /** Flex on 'em with stats. */
  public void flex(String message) {
    game.notify(new ChatMessage(message, null));
  }

  /** Difficulty of ai player. */
  public enum Difficulty {
    EASY,
    HARD
  }
}
