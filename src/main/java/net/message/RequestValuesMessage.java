package net.message;

/**
 * Message for requesting TileValues.
 *
 * @author ygarip
 */
public class RequestValuesMessage extends Message {
  private int[] values;

  public RequestValuesMessage() {
    super(MessageType.REQUESTVALUES);
  }

  /**
   * a method to set the values.
   *
   * @param values Requires the int array of values
   */
  public void setValues(int[] values) {
    this.values = values;
  }

  /**  Returns the values. */
  public int[] getValues() {
    return this.values;
  }
}
