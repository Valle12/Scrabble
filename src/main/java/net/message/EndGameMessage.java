package net.message;

/**
 * We're in the Endgame now ~ Avengers. But still a class to communicate the EndGame.
 *
 * @author ygarip
 */
public class EndGameMessage extends Message {
  private int type;
  private boolean winner;
  private String[][] foundWords;
  private int score;

  /**
   * Constructor for creating EndGameMessage.
   *
   * @param type Requires the int type of EndGameMessage
   */
  public EndGameMessage(int type) {
    super(MessageType.ENDGAME);
    this.type = type;
  }

  /**
   * Constructor for creating EndGameMessage.
   *
   * @param type Requires type to be send
   * @param winner Requires if player is winner
   * @param foundWords Requires all foundWords in 2D-String array
   */
  public EndGameMessage(int type, boolean winner, int score, String[][] foundWords) {
    super(MessageType.ENDGAME);
    this.type = type;
    this.winner = winner;
    this.score = score;
    this.foundWords = foundWords;
  }

  /**  Returns type of message. */
  public int getType() {
    return this.type;
  }

  /** Returns winnerScore. */
  public boolean getWinner() {
    return winner;
  }

  /**  Returns score of player. */
  public int getScore() {
    return score;
  }
}
