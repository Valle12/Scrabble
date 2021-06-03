package net.message;

/**
 * Message class for indicating whether it is your turn or not.
 *
 * @author vkaczmar
 */
public class TurnMessage extends Message {
  private final boolean turn;
  private final boolean[] turns;
  private final int bagSize;
  private final int[] points;

  /**
   * Constructor for Creating TurnMessage.
   *
   * @param turn Requires the boolean value for turn
   */
  public TurnMessage(boolean turn, boolean[] turns, int bagSize, int[] points) {
    super(MessageType.TURN);
    this.turn = turn;
    this.turns = turns;
    this.bagSize = bagSize;
    this.points = points;
  }

  /** Returns the boolean value of turn. */
  public boolean getTurn() {
    return this.turn;
  }

  /** Returns in the boolean array the player's turns. */
  public boolean[] getTurns() {
    return this.turns;
  }

  /** Returns amount of tiles in game bag. */
  public int getBagSize() {
    return bagSize;
  }

  /** Returns points. */
  public int[] getPoints() {
    return points;
  }
}
