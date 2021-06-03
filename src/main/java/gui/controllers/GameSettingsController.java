package gui.controllers;

import client.Client;
import ft.Sound;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the Game Settings.
 *
 * @author vihofman
 */
public class GameSettingsController {

  @FXML private TextField letter;
  @FXML private TextField value;
  @FXML private TextField distribution;
  @FXML private Label dictionary;
  @FXML private TextField joker;

  private Client client;
  private CreateGameController createController;
  private char letterId; // represents the current letter chosen
  private int valueId; // represents the current value of chosen letter
  private int distributionId; // represents the current distribution of chosen letter

  private String dictionaryId; // current dictionary

  private int[] values; // array storing values of letters
  private int[] distributions; // array storing distribution of letters

  /** Set model method. */
  public void setModel(Client client, CreateGameController createController) {
    this.client = client;
    this.createController = createController;

    values = new int[createController.getValues().length];
    for (int i = 0; i < createController.getValues().length; i++) {
      values[i] = createController.getValues()[i];
    }

    distributions = new int[createController.getDistributions().length];
    for (int i = 0; i < createController.getDistributions().length; i++) {
      distributions[i] = createController.getDistributions()[i];
    }
    initData();
    joker.setText(Integer.toString(distributions[26]));

    dictionary.setText(createController.getDictionaryPath());
  }

  /** Method which initializes all the fields in the GUI. */
  public void initData() {
    for (int i = 65; i <= 90; i++) {
      char test = (char) i;
      if (String.valueOf(test).equals(letter.getText())) {
        value.setText(Integer.toString(this.values[i - 65]));
        distribution.setText(Integer.toString(this.distributions[i - 65]));
        break;
      }
    }
  }

  /** Applies the Settings and closes the GameSettingView through the CreateGameController. */
  public void apply() {
    Sound.playMusic(Sound.tileSet);
    createController.closeSettings();
    createController.setValues(values);
    createController.setDistributions(distributions);
    createController.setDictionaryPath(this.dictionary.getText());
    client
        .getNetClient()
        .sendGameSettings(values, distributions, createController.getDictionaryPath());
  }

  /** Closes the GameSettings Screen through the CreateGameController. */
  public void cancel() {
    Sound.playMusic(Sound.tileSet);
    createController.closeSettings();
  }

  /**
   * Opens a FileChooser to choose the fxml File which is supposed to be the dictionary. If File is
   * not a txt File or nothing has been selected, then show Error Message.
   */
  public void selectDictionary() throws IOException {
    FileChooser fc = new FileChooser();
    // Filter .txt files
    Sound.playMusic(Sound.tileSet);
    FileChooser.ExtensionFilter extFilter =
        new FileChooser.ExtensionFilter("Dict TXT files (*.txt)", "*.txt");
    fc.getExtensionFilters().add(extFilter);
    fc.setInitialDirectory(
        new File(System.getProperty("user.dir")).exists()
            ? new File(System.getProperty("user.dir"))
            : null);
    File selectedFile = fc.showOpenDialog(null);
    if (selectedFile != null) {
      if (selectedFile.getName().matches("/*.*.txt")) {
        dictionary.setText(selectedFile.getPath());
        dictionaryId = dictionary.getText();
      } else {
        openDictionaryError();
      }
    }
  }

  /** Decreasing the Joker tile. */
  public void decreaseJoker() {
    Sound.playMusic(Sound.tileSet);
    int jokers = distributions[26];
    jokers--;
    joker.setText(Integer.toString(jokers));
    distributions[26] = jokers;
  }

  /** Increasing the Joker tile. */
  public void increaseJoker() {
    Sound.playMusic(Sound.tileSet);
    int jokers = distributions[26];
    jokers++;
    joker.setText(Integer.toString(jokers));
    distributions[26] = jokers;
  }

  /** Increasing the letter. */
  public void increaseLetter() { // to swap to next letter
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    if (letterId <= 89) {
      letterId++;
      letter.setText(String.valueOf(letterId));
      initData();
    }
  }

  /** Decreasing the letter. */
  public void decreaseLetter() { // to swap to letter before
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    if (letterId >= 66) {
      letterId--;
      letter.setText(String.valueOf(letterId));
    }
    initData();
  }

  /** increasing the value. */
  public void increaseValue() { // to increase selected letter above
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    valueId = Integer.parseInt(value.getText());
    if (valueId > 0) {
      valueId++;
      this.values[letterId - 65] = valueId;
      initData();
    }
  }

  /** Decreasing the value. */
  public void decreaseValue() { // to decrease selected letter above
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    valueId = Integer.parseInt(value.getText());
    if (valueId > 0) {
      valueId--;
      this.values[letterId - 65] = valueId;
    }
    initData();
  }

  /** Increasing the distribution. */
  public void increaseDistribution() { // to increase distribution of selected letter above
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    distributionId = Integer.parseInt(distribution.getText());
    if (distributionId >= 0) {
      distributionId++;
      this.distributions[letterId - 65] = distributionId;
    }
    initData();
  }

  /** Decreasing the distribution. */
  public void decreaseDistribution() { // to decrease distribution of selected letter above
    Sound.playMusic(Sound.tileSet);
    letterId = letter.getText().charAt(0);
    distributionId = Integer.parseInt(distribution.getText());
    if (distributionId > 0) {
      distributionId--;
      this.distributions[letterId - 65] = distributionId;
    }
    initData();
  }

  /** Opens the dictionary error. */
  public void openDictionaryError() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/dictionaryError.fxml"));
    Parent errorMessage = loader.load();

    Scene exitGameScene = new Scene(errorMessage);
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("Exit Game");
    window.setScene(exitGameScene);
    window.setWidth(200);
    window.setHeight(200);
    window.showAndWait();
  }

  /** Exits the game. */
  public void exitGame() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    Sound.playMusic(Sound.tileSet);
    loader.setLocation(this.getClass().getResource("/views/exitGame.fxml"));
    Parent exitGameView = loader.load();
    Scene exitGameScene = new Scene(exitGameView);
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("Exit Game");
    window.setScene(exitGameScene);
    window.setWidth(300);
    window.setHeight(200);
    window.showAndWait();
  }
}
