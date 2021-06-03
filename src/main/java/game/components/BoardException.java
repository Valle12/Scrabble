package game.components;

/**
 * Thrown by the check method of class Board method. Specifics can be read from the given message.
 *
 * @author yuzun
 */
public class BoardException extends Exception {

  public BoardException(String message) {
    super(message);
  }
}
