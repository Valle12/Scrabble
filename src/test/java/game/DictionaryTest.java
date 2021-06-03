package game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/** @author nsiebler for Dictionary test
 * this test checks the functionality of the dictionary and its word as well as the meaning of
 * the words
 * */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DictionaryTest {
  Dictionary dictionary;

  @BeforeAll
  void setUp() throws Exception {
    dictionary = new Dictionary();
  }

  @Test
  // Test for checking if a word exists or not
  public void checkWordTest() {
    Assertions.assertEquals(true, dictionary.wordExists("HALLO"));
    Assertions.assertEquals(true, dictionary.wordExists("WORLD"));
    Assertions.assertEquals(true, dictionary.wordExists("AA"));

    Assertions.assertEquals(false, dictionary.wordExists("DISKUS"));
    Assertions.assertEquals(false, dictionary.wordExists("POPEL"));
    Assertions.assertEquals(false, dictionary.wordExists("KANINCHEN"));
  }

  @Test
  // Test for checking if words array is right
  public void getWordsArray() {
    String a[] = dictionary.getWordsAsArray();

    int size = 279498;
    Assertions.assertEquals(true, a.length > 0);
    Assertions.assertEquals(true, a.length == size);
  }

  @Test
  // Test for checking if a getting dict works
  public void getDictionary() {
    String dict = dictionary.getDictionary();

    Assertions.assertEquals(true, dict.contains("HELLO"));
    Assertions.assertEquals(true, dict.contains("WORLD"));
    Assertions.assertEquals(true, dict.contains("AA"));
  }

  @Test
  // Test for checking meaning of word
  public void getMeaning() {
    String aaMeaning =
        "(Hawaiian) a volcanic rock consisting of angular blocks of"
            + " lava with a very rough surface [n -S]";
    String housingsMeaning = "HOUSING, any dwelling house [n]";
    String villadomMeaning = "villas collectively [n -S]";
    Assertions.assertEquals(true, dictionary.getMeaning("AA").equals(aaMeaning));
    Assertions.assertEquals(true, dictionary.getMeaning("HOUSINGS").equals(housingsMeaning));
    Assertions.assertEquals(true, dictionary.getMeaning("VILLADOM").equals(villadomMeaning));
  }
}
