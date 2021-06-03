package ft;

import client.PlayerProfile;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test class of XmlHandler. Uses profiles.xml since its final in XmlHandler.
 *
 * @author yuzun
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class XmlHandlerTest {

  private final String sep = System.getProperty("file.separator");
  private String originalContent; // to restore file contents
  private List<PlayerProfile> profiles;

  public static List<PlayerProfile> getMockProfiles() {
    ArrayList<PlayerProfile> profiles = new ArrayList<>();

    for (int i = 1; i <= 20; i++) {
      String name = "Profile " + i + "";
      Random rand = new Random();
      int highscore = rand.nextInt(1000);
      long playtime = rand.nextLong();
      int wins = rand.nextInt(200);
      int losses = rand.nextInt(200);
      int totalScore = rand.nextInt(1000000);
      LocalDate create = LocalDate.now();
      LocalDate lastLogged = LocalDate.now();
      profiles.add(
          new PlayerProfile(name, highscore, wins, losses, totalScore, create, lastLogged));
    }
    return profiles;
  }

  /** Save prior state of file content into a string to restore later. */
  @BeforeAll
  void setUp() throws Exception {
    String line;
    File xmlFile = new File(XmlHandler.XML_PATH);
    if (xmlFile.exists()) {
      BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
      StringBuilder sb = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      originalContent = sb.toString();
      reader.close();
      // empty Xml
      FileWriter writer = new FileWriter(xmlFile);
      writer.write(
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
              + "\n<PlayerProfiles></PlayerProfiles>"); // Write new XML
      writer.flush();
      writer.close();
    } else {
      originalContent = "";
    }
    profiles = getMockProfiles();
  }

  @Test
  @Order(1)
  @DisplayName("Save profiles to XML")
  void saveXmlTest() throws InterruptedException {
    XmlHandler.saveXml(profiles);
    Assertions.assertTrue(new File(XmlHandler.XML_PATH).exists());
  }

  @Test
  @Order(2)
  @DisplayName("Load profiles")
  void loadProfilesTest() {

    List<PlayerProfile> loadedProfiles = XmlHandler.loadProfiles();
    // size
    if (loadedProfiles == null) {
      Assertions.fail("No profiles loaded (list is null)");
    }

    if (loadedProfiles.size() != profiles.size()) {
      Assertions.fail("Wrong amount of profiles");
    }

    for (int i = 0; i < profiles.size(); i++) {
      Assertions.assertEquals(profiles.get(i).getName(), loadedProfiles.get(i).getName());
      Assertions.assertEquals(profiles.get(i).getHighScore(), loadedProfiles.get(i).getHighScore());
      Assertions.assertEquals(profiles.get(i).getWins(), loadedProfiles.get(i).getWins());
      Assertions.assertEquals(profiles.get(i).getLosses(), loadedProfiles.get(i).getLosses());
      Assertions.assertEquals(
          profiles.get(i).getTotalScore(), loadedProfiles.get(i).getTotalScore());
      Assertions.assertEquals(profiles.get(i).getCreation(), loadedProfiles.get(i).getCreation());
      Assertions.assertEquals(
          profiles.get(i).getLastLogged(), loadedProfiles.get(i).getLastLogged());
    }
    System.out.println("Tested to save and load " + profiles.size() + " different profiles");
  }

  @AfterAll
  public void restore() throws IOException {
    if (!originalContent.equals("")) {
      File resDir = new File(System.getProperty("user.dir") + sep + "res");
      if (!resDir.exists()) {
        resDir.mkdir();
      }
      File xmlFile = new File(XmlHandler.XML_PATH);
      if (!xmlFile.exists()) {
        xmlFile.createNewFile();
      }
      FileWriter writer = new FileWriter(xmlFile);
      writer.write(originalContent);
      writer.flush();
      writer.close();
    } else {
      new File(XmlHandler.XML_PATH).delete();
      new File(System.getProperty("user.dir") + sep + "res").delete();
    }
    System.out.println("Restored prior state");
  }
}
