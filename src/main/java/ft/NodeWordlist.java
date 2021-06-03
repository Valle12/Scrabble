package ft;

/**
 * Class for data in BST
 *
 * @author vkaczmar
 */
public class NodeWordlist {
  private final String data;
  private final String meaning;
  private NodeWordlist left, right;

  /**
   * Constructor for creation of Node, data and both children get values.
   *
   * @author vkaczmar
   * @param data String value with data to save.
   */
  public NodeWordlist(String data, String meaning) {
    this.data = data;
    left = null;
    right = null;
    this.meaning = meaning;
  }

  /**
   * Returns String Data.
   *
   * @author vkaczmar
   */
  public String getData() {
    return data;
  }

  /**
   * Returns left child of Node.
   *
   * @author vkaczmar
   */
  public NodeWordlist getLeft() {
    return left;
  }

  /**
   * Sets left child to given Node.
   *
   * @author vkaczmar
   * @param left Value for left child
   */
  public void setLeft(NodeWordlist left) {
    this.left = left;
  }

  /**
   * Returns right child of Node.
   *
   * @author vkaczmar
   */
  public NodeWordlist getRight() {
    return right;
  }

  /**
   * Sets right child to given Node.
   *
   * @author vkaczmar
   * @param right Value for right child
   */
  public void setRight(NodeWordlist right) {
    this.right = right;
  }

  /** Returns Meaning */
  public String getMeaning() {
    return meaning;
  }
}
