package game.components;

import game.Dictionary;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Checks board functionalities.
 *
 * @author yuzun
 */
public class BoardTest {

  private static final int BOARD_SIZE = 15;
  public static Dictionary dictionary;
  private LinkedList<BoardField>
      placements; // Simulates in-game list of placements in the last turn
  public Board board;

  @BeforeAll
  public static void setUp() {
    dictionary = new Dictionary();
  }

  @BeforeEach
  public void init() {
    board = new Board();
    placements = new LinkedList<>();
  }

  @Test
  @DisplayName("Correct board placement")
  public void checkTestSuccess() throws IOException, BoardException {
    configureBoard("boardStateCorrect1.txt");

    // RAIN -> TRAIN
    board.placeTile(new Tile('T', 0), 6, 4);
    placements.add(board.getField(6, 4));

    board.check(placements, dictionary, true); // passes if no error thrown
  }

  @Test
  @DisplayName("Start field not covered")
  public void checkTestFail1() {
    board.placeTile(new Tile('X', 42), 5, 9);
    placements.add(board.getField(5, 9));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals("Star Field is not covered", boardException.getMessage());
  }

  @Test
  @DisplayName("Placement not adjacent to any")
  public void checkTestFail2() throws IOException {
    configureBoard("boardStateCorrect1.txt");

    // Build word IT in the upper left corner
    board.placeTile(new Tile('I', 0), 0, 0);
    placements.add(board.getField(0, 0));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals("Placements cannot stand alone", boardException.getMessage());
  }

  @Test
  @DisplayName("Placements are a word but not adjacent to already placed")
  public void checkTestFail3() throws IOException {
    configureBoard("boardStateCorrect1.txt");

    // Build word IT in the upper left corner
    board.placeTile(new Tile('I', 0), 0, 0);
    board.placeTile(new Tile('T', 0), 0, 1);
    placements.add(board.getField(0, 0));
    placements.add(board.getField(0, 1));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals(
        "Placements must join together with tiles on board", boardException.getMessage());
  }

  @Test
  @DisplayName("2 Tiles form 'IT' not-adjacent to others + 1 is adjacent")
  public void checkTestFail4() throws IOException {
    configureBoard("boardStateCorrect1.txt");

    // RAIN -> TRAIN
    board.placeTile(new Tile('T', 0), 6, 4);
    placements.add(board.getField(6, 4));

    // IT
    board.placeTile(new Tile('I', 0), 0, 4);
    board.placeTile(new Tile('T', 0), 1, 4);
    placements.add(board.getField(0, 4));
    placements.add(board.getField(1, 4));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals(
        "Placements must join together with tiles on board", boardException.getMessage());
  }

  @Test
  @DisplayName("Placements not in a single row/column")
  public void checkTestFail5() throws IOException {
    configureBoard("boardStateCorrect1.txt");

    // RAIN -> TRAIN
    board.placeTile(new Tile('T', 0), 6, 4);
    placements.add(board.getField(6, 4));
    // CRUISE -> CRUISER
    board.placeTile(new Tile('R', 0), 9, 12);
    placements.add(board.getField(9, 12));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals(
        "Placements can only be in a single line or column", boardException.getMessage());
  }

  @Test
  @DisplayName("Placements correct, but formed words does not exist")
  public void checkTestFail6() throws IOException {
    configureBoard("boardStateCorrect1.txt");

    // RAIN -> QRAIN
    board.placeTile(new Tile('Q', 0), 6, 4);
    placements.add(board.getField(6, 4));

    Exception boardException =
        Assertions.assertThrows(
            BoardException.class, () -> board.check(placements, dictionary, false));
    Assertions.assertEquals("Word QRAIN was not recognized", boardException.getMessage());
  }

  /**
   * Builds board corresponding to the .txt files.
   *
   * @param filename path to file
   * @throws IOException if file at given path does not exist
   */
  private void configureBoard(String filename) throws IOException {
    BufferedReader reader =
        new BufferedReader(
            new FileReader(
                System.getProperty("user.dir") + "/src/test/resources/BoardTest/" + filename));

    for (int i = 0; i < BOARD_SIZE; i++) {
      String line = reader.readLine();
      String[] letters = line.split(" ");
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (letters[j].charAt(0) != '-') {
          Tile tile = new Tile(letters[j].charAt(0), 0);
          board.placeTile(tile, i, j);
        }
      }
    }
    placements.clear();
  }
}
