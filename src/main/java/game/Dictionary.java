package game;

import ft.NodeWordlist;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class that is able to load, read and manage all words from the wordlist in a BST.
 *
 * @author vkaczmar
 */
public class Dictionary {
  private BufferedReader br;
  private final ArrayList<String> uneditedLines;
  private final ArrayList<String> words;
  private final ArrayList<String> meanings;
  private final NodeWordlist root;

  /** Default Dictionary when called without specifying a path (dictionary is given in resources). */
  public Dictionary() {
    String defaultDictionaryPath = "/data/Collins Scrabble Words (2019) with definitions.txt";
    uneditedLines = new ArrayList<>();

    InputStream in = getClass().getResourceAsStream(defaultDictionaryPath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        uneditedLines.add(line);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    words = new ArrayList<>();
    meanings = new ArrayList<>();
    getWords();
    root = createBinaryTreeFromArrayList(words, meanings, 0, words.size() - 1);
  }

  /**
   * Constructor with parameter to the wordlist.txt file. Does everything up to the creation of the
   * binary search tree.
   *
   * @param absolutePath Requires the absolute Path to the wordlist itself
   */
  public Dictionary(String absolutePath) {
    File f = new File(absolutePath);
    try {
      br = new BufferedReader(new FileReader(f));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    uneditedLines = new ArrayList<>();
    getUneditedLines();
    words = new ArrayList<>();
    meanings = new ArrayList<>();
    getWords();
    root = createBinaryTreeFromArrayList(words, meanings, 0, words.size() - 1);
  }

  /**
   * CreatesBinarySearchTree.
   *
   * @param words requires ArrayList with Strings
   * @param start start index
   * @param end end index
   * @return returns root of BST
   */
  private NodeWordlist createBinaryTreeFromArrayList(
      ArrayList<String> words, ArrayList<String> meanings, int start, int end) {
    if (start > end) {
      return null;
    }
    int middle = (start + end) / 2;
    NodeWordlist node = new NodeWordlist(words.get(middle), meanings.get(middle));
    node.setLeft(createBinaryTreeFromArrayList(words, meanings, start, middle - 1));
    node.setRight(createBinaryTreeFromArrayList(words, meanings, middle + 1, end));
    return node;
  }

  /**
   * Private method to get all lines from the wordlist, which are neither empty nor an introduction
   * line. These lines get added to uneditedLines.
   */
  private void getUneditedLines() {
    String line;
    try {
      while ((line = br.readLine()) != null) {
        if (line.matches("[A-z][A-Z].*")) {
          uneditedLines.add(line);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** Private method to get words from uneditedLines. Words get added to ArrayList words */
  private void getWords() {
    String[] splitLine;
    Iterator<String> it = uneditedLines.iterator();
    while (it.hasNext()) {
      String word = it.next();
      splitLine = word.split("\\s");
      words.add(splitLine[0]);
      word = word.replaceAll("\\s+", " ").trim();
      meanings.add(word.substring(word.indexOf(" ") + 1));
    }
  }

  /**
   * Checks if word exists.
   *
   * @param word requires word to be searched for
   * @return returns true, if word exists
   */
  public boolean wordExists(String word) {
    return wordExists(root, word);
  }

  /**
   * Checks wether a certain word exists in wordlist.
   *
   * @param node Requires node to start searching with
   * @param word Requires word/ REGEX, in a non case sensitive way
   * @return Returns true, if word exists
   */
  private boolean wordExists(NodeWordlist node, String word) {
    if (node == null) {
      return false;
    } else if (node.getData().compareTo(word.toUpperCase()) == 0) {
      return true;
    } else if (node.getData().compareTo(word.toUpperCase()) > 0) {
      return wordExists(node.getLeft(), word);
    } else if (node.getData().compareTo(word.toUpperCase()) < 0) {
      return wordExists(node.getRight(), word);
    } else {
      return false;
    }
  }

  /** returns a String array of all the words. */
  public String[] getWordsAsArray() {
    String[] s = new String[words.size()];
    for (int i = 0; i < words.size(); i++) {
      s[i] = words.get(i);
    }
    return s;
  }

  /** returns the whole dictionary as one String. */
  public String getDictionary() {
    StringBuffer sb = new StringBuffer();
    for (String s : uneditedLines) {
      sb.append(s + "\n");
    }
    return sb.toString();
  }

  /** meaning of word. */
  public String getMeaning(String word) {
    NodeWordlist node = getNode(root, word);
    return node.getMeaning();
  }

  /**
   * Returns Node, associated with word.
   *
   * @author yuzun
   * @param node Node to start searching with (root)
   * @param word word to serach node for
   * @return Returns node, associated with word
   */
  private NodeWordlist getNode(NodeWordlist node, String word) {
    if (node == null) {
      return null;
    } else if (node.getData().compareTo(word.toUpperCase()) == 0) {
      return node;
    } else if (node.getData().compareTo(word.toUpperCase()) > 0) {
      return getNode(node.getLeft(), word);
    } else if (node.getData().compareTo(word.toUpperCase()) < 0) {
      return getNode(node.getRight(), word);
    } else {
      return null;
    }
  }
}
