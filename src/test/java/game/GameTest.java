package game;

import game.components.Board;
import game.components.BoardField;
import game.components.Tile;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

/**
 * @author nsiebler
 * the test is about the words hallo, world and middle, and test the various cases
 *     that can occur while playing
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameTest {

  Tile hLetter;
  Tile lLetter;
  Tile oLetter;
  Tile wLetter;
  Tile rLetter;
  Tile dLetter;
  Tile mLetter;
  Tile iLetter;
  Tile eLetter;
  Tile nLetter;
  Tile pLetter;
  Tile gLetter;
  Tile tLetter;
  Tile aLetter;
  Tile sLetter;
  Board board = new Board();
  List<BoardField> placements = new ArrayList<>();

  @BeforeAll
  void setUp() throws Exception {
    // Initialize all the tiles for the test words
    hLetter = new Tile('H', 4);
    lLetter = new Tile('L', 1);
    oLetter = new Tile('O', 1);
    wLetter = new Tile('W', 4);
    rLetter = new Tile('R', 1);
    dLetter = new Tile('D', 2);
    mLetter = new Tile('M', 3);
    iLetter = new Tile('I', 1);
    eLetter = new Tile('E', 1);
    nLetter = new Tile('N', 1);
    pLetter = new Tile('P', 3);
    gLetter = new Tile('G', 2);
    tLetter = new Tile('T', 1);
    aLetter = new Tile('A', 1);
    sLetter = new Tile('S', 1);
  }

  @Test
  @DisplayName("Placing 'HELLO' (H on DWS, O on DLS)")
  void evaluateScoreTest1() {
    // First word
    place(hLetter, 7, 7);
    place(eLetter, 7, 8);
    place(lLetter, 7, 9);
    place(lLetter, 7, 10);
    place(oLetter, 7, 11);

    Assertions.assertEquals(18, board.evaluateScore(placements));
    placements.clear();
  }

  @Test
  @DisplayName("Placing 'ING' (Placements: HELLOING)")
  void evaluateScoreTest2() {
    // second word
    place(iLetter, 7, 12);
    place(nLetter, 7, 13);
    place(gLetter, 7, 14);
    Assertions.assertEquals(36, board.evaluateScore(placements));
    placements.clear();
  }

  @Test
  @DisplayName("Placing 'ELMETED' (Placements: HELMETED)")
  void evaluateScoreTest3() {
    // third word
    place(eLetter, 8, 7);
    place(lLetter, 9, 7);
    place(mLetter, 10, 7);
    place(eLetter, 11, 7);
    place(tLetter, 12, 7);
    place(eLetter, 13, 7);
    place(dLetter, 14, 7);
    Assertions.assertEquals(95, board.evaluateScore(placements));
    placements.clear();
  }

  @Test
  @DisplayName("Placing 'AT' (Placements: EAT)")
  void evaluateScoreTest4() {
    place(aLetter, 11, 8);
    place(tLetter, 11, 9);

    Assertions.assertEquals(3, board.evaluateScore(placements));
    placements.clear();
  }

  @Test
  @DisplayName("Placing 'OW' (Placements: LOW)")
  void evaluateScoreTest5() {
    place(oLetter, 8, 9);
    place(wLetter, 9, 9);

    Assertions.assertEquals(14, board.evaluateScore(placements));
    placements.clear();
  }

  @Test
  @DisplayName("Placing 'EST' (Placements: LOWEST)")
  void evaluateScoreTest6() {
    place(eLetter, 10, 9);
    place(sLetter, 11, 9);
    place(tLetter, 12, 9);
    Assertions.assertEquals(12, board.evaluateScore(placements));
    placements.clear();
  }

  private void place(Tile tile, int row, int col) {
    board.placeTile(tile, row, col);
    placements.add(board.getField(row, col));
  }
}
