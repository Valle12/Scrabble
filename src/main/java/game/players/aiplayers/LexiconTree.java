package game.players.aiplayers;

import game.Dictionary;
import game.components.Tile;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Directed-Acyclic Word Graph.
 *
 * @author yuzun
 */
public class LexiconTree {

  private final Node root;
  private Set<String> possibleWords; // Filled recursively by method calculatePossibleWords

  /** Build Tree based on given dictionary. */
  public LexiconTree(Dictionary dictionary) {
    String[] words = dictionary.getWordsAsArray();
    this.root = new Node(false); // Empty character literal for root

    // BUILD TREE
    for (String word : words) {

      Node helper = root; // traverses over nodes

      // iterate over every character of word
      for (int i = 0; i < word.length(); i++) {
        char c = word.charAt(i);
        Node child = helper.getChild(c);
        if (child == null) { // if no child --> create one
          helper.add(c, i == word.length() - 1);
        }
        helper = helper.getChild(c);
      }
    }
    // TODO REMOVE DUPLICATE SUBTREES --> Saving a lot of space
  }

  /**
   * Calls internal function to calculate possible words. Pattern must not be fully filled e.g. a
   * possible word for a#########xy would be apple.
   *
   * @param wordPattern pattern to search (# is wildcard)
   * @param tiles tiles on rack
   */
  public Set<String> calculatePossibleWords(String wordPattern, List<Tile> tiles) {
    possibleWords = new TreeSet<>();

    LinkedList<Character> rackCharacters = new LinkedList<>();
    tiles.forEach(tile -> rackCharacters.add(tile.getLetter())); // fill list of characters
    LinkedList<Character> pattern = new LinkedList<>();
    for (char c : wordPattern.toCharArray()) {
      pattern.add(c);
    }

    root.calculatePossibleWords(pattern, new LinkedList<>(), rackCharacters);
    return possibleWords;
  }

  /**
   * A tree built by the bot at the start of game (PREFIX-/SUFFIX-TREE). Only used here for this
   * bot.
   */
  private class Node {
    private final Map<Character, Node> children; // Children nodes
    private final boolean end; // true if word (can) end here

    Node(boolean end) {
      children = new HashMap<>();
      this.end = end;
    }

    /** Adds new node with given letter to children. */
    public void add(char letter, boolean end) {
      children.put(letter, new Node(end));
    }

    public Node getChild(char letter) {
      return children.get(letter);
    }

    /** true if word (can) end here. */
    public boolean isEnd() {
      return end;
    }

    /**
     * Recursive Method, checks which words are possible given a pattern. Pattern must not be
     * fully filled e.g. a possible word for a#########xy would be apple.
     *
     * @param pattern Given Pattern (# element if empty on board)
     * @param path list of characters already visited
     * @param characters characters of tiles on hand
     */
    private void calculatePossibleWords(
        LinkedList<Character> pattern,
        LinkedList<Character> path,
        LinkedList<Character> characters) {

      if (end) { // if end node --> add
        StringBuilder builder = new StringBuilder();
        path.forEach(builder::append);
        possibleWords.add(builder.toString());
      }

      if (pattern.size() == 0) { // if full add to possible words
        StringBuilder builder = new StringBuilder();
        path.forEach(builder::append);
        possibleWords.add(builder.toString());
        return;
      }

      char letter = pattern.removeFirst(); // next letter trying to fill
      if (letter != '#') { // if already placed
        path.addLast(letter);
        Node child = children.get(letter);
        if (child != null) { // if word exists
          child.calculatePossibleWords(pattern, path, characters);
        }
        path.removeLast();

      } else { // if to be placed try everything out

        for (int i = 0; i < characters.size(); i++) { // try out for every child
          Node child = children.get(characters.get(i));
          if (child != null) {
            char c = characters.get(i);
            path.addLast(c);
            characters.remove(i);
            child.calculatePossibleWords(pattern, path, characters);
            characters.add(c);
            path.removeLast();
          }
        }
      }
      pattern.addFirst(letter);
    }
  }
}
