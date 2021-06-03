package ft;

import client.PlayerProfile;
import javafx.scene.image.Image;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @author nsiebler This is the class that contains the Methods to Handle the XML files With that i
 *     mean create new files and update files that are already existing as well as read the values
 *     out of the data. This is made by the playertoElement and elementToPlayer functions Get Player
 *     Functionality is made by the ArrayList
 *     <p>This class creates the XML Document based on the NewPlayer Class We want a method that
 *     just creates the Document here. The deletion and change of values is done on another Class.
 *     We just wanna make sure that we can create the doc that we created here
 */
public class XmlHandler {
  private static final String sep = System.getProperty("file.separator");
  public static final String XML_PATH =
      System.getProperty("user.dir") + sep + "res" + sep + "profiles.xml";

  public static Document xmlDoc;

  private static void initDocument() {
    if (xmlDoc == null) {
      // Prepare SAXBuilder
      SAXBuilder builder = new SAXBuilder();
      try {
        // Make sure the XML file exists
        File resDir = new File(System.getProperty("user.dir") + sep + "res");
        if (!resDir.exists()) {
          if (!resDir.mkdir()) { // Create directory
            System.out.println("ERROR: Directory 'res' could not be created.");
          }
        }
        File xmlFile = new File(XML_PATH);
        if (!xmlFile.exists()) {
          if (!xmlFile.createNewFile()) { // Create new file
            System.out.println("ERROR: File 'profiles.xml' could not be created.");
          } else {
            FileWriter writer = new FileWriter(xmlFile);
            writer.write(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "\n<PlayerProfiles></PlayerProfiles>"); // Write new XML
            writer.flush();
            writer.close();
          }
        }
        xmlDoc = builder.build(xmlFile);
      } catch (JDOMException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Method saves the document given by the attribute xmlDoc in XML format to a file (defined in
   * Parameters class)
   */
  public static void saveXml(List<PlayerProfile> profiles) {
    // Prepare XMLOutPutter and it's format
    if (xmlDoc == null) {
      initDocument();
    }
    // delete
    xmlDoc.getRootElement().removeContent();
    // write profiles
    for (PlayerProfile profile : profiles) {
      xmlDoc.getRootElement().addContent(playerToElement(profile));
    }
    XMLOutputter xmlOutput = new XMLOutputter();
    xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));
    try {
      // Save XML
      xmlOutput.output(xmlDoc, new FileOutputStream(XML_PATH));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This Method creates a New Player Instance, which can be changed based on new Values for example
   */
  public static PlayerProfile elementToPlayer(Element player) {
    PlayerProfile nPlayer = null;
    try {

      // Local Date logged is 0/0/0
      // And String from creation needs to be changed into a date
      // Create the LocalDate Values from the String
      String[] datumStr = player.getChild("creationDate").getText().split("\\.");
      LocalDate creation =
          LocalDate.of(
              Integer.parseInt(datumStr[2]),
              Integer.parseInt(datumStr[1]),
              Integer.parseInt(datumStr[0]));

      String[] lastLoggedStr = player.getChild("lastLogged").getText().split("\\.");
      LocalDate lastLogged =
          LocalDate.of(
              Integer.parseInt(lastLoggedStr[2]),
              Integer.parseInt(lastLoggedStr[1]),
              Integer.parseInt(lastLoggedStr[0]));

      String imageUrl = player.getChildText("image");
      Image image = null;
      if (imageUrl != null && imageUrl != "") {
        if (Files.exists(Paths.get(imageUrl.substring(6)))) {
          image = new Image(imageUrl);
        }
      }
      nPlayer =
          new PlayerProfile(
              player.getChildText("name"),
              Integer.parseInt(player.getChildText("highscore")),
              Integer.parseInt(player.getChildText("wins")),
              Integer.parseInt(player.getChildText("losses")),
              Integer.parseInt(player.getChildText("totalScore")),
              creation,
              lastLogged,
              image);

      return nPlayer;
    } catch (Exception exp) {
      System.exit(0);
    }

    return nPlayer;
  }

  /**
   * This Method creates an Element which can be added to the xml file
   *
   * @param nPlayer Player class instance
   * @return Element that can be added to the xml
   */
  public static Element playerToElement(PlayerProfile nPlayer) {
    // Root Element
    Element newPlayer = new Element("profile");

    Element name = new Element("name");
    name.addContent(nPlayer.getName());
    // For xml we treat the Integers as Strings to avoid using
    // Attributes and make the work easy by treating everything as Strings

    Element highscore = new Element("highscore");
    highscore.addContent(Integer.toString(nPlayer.getHighScore()));

    Element wins = new Element("wins");
    wins.addContent(Integer.toString(nPlayer.getWins()));

    Element losses = new Element("losses");
    losses.addContent(Integer.toString(nPlayer.getLosses()));

    Element totalScore = new Element("totalScore");
    totalScore.addContent(Integer.toString(nPlayer.getTotalScore()));

    // last Value
    Element creationDate = new Element("creationDate");
    creationDate.addContent(nPlayer.getCreation());

    Element lastLogged = new Element("lastLogged");
    lastLogged.addContent(nPlayer.getCreation());

    Element image64 = new Element("image");
    String base64Image = "";
    if (nPlayer.getImage() != null) {
      Image image = nPlayer.getImage();
      base64Image = image.getUrl();
    }
    image64.addContent(base64Image);

    // add all the content, also the content that will follow
    newPlayer.addContent(name);
    newPlayer.addContent(highscore);
    newPlayer.addContent(wins);
    newPlayer.addContent(losses);
    newPlayer.addContent(totalScore);
    newPlayer.addContent(creationDate);
    newPlayer.addContent(lastLogged);
    newPlayer.addContent(image64);

    return newPlayer;
  }

  /**
   * Worked Sav Method which generates a updated XML-file the normal save method still has to be
   * their because this method saves the xml document itself
   */
  public static List<PlayerProfile> loadProfiles() {
    // Have to reset the List every time
    List<PlayerProfile> players = new ArrayList<>();

    if (xmlDoc == null) {
      initDocument();
    }
    Element root = xmlDoc.getRootElement();

    List<Element> all = root.getChildren();
    Iterator<Element> it = all.iterator();

    while (it.hasNext()) {
      Element ele = it.next();
      players.add(elementToPlayer(ele));
    }

    return players;
  }
}
