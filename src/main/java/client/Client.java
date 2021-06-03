package client;

import ft.Sound;
import ft.XmlHandler;
import game.players.LocalPlayer;
import gui.controllers.GameViewController;
import gui.controllers.WelcomeViewController;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.client.NetClient;

/**
 * Main class launches JavaFX window and manages navigation between scenes holds * application-scope
 * data.
 *
 * @author yuzun
 */
public class Client extends Application {

  private List<PlayerProfile> playerProfiles; // List of player profiles
  private PlayerProfile selectedProfile; // PlayerProfile instance for the selectedProfile
  private NetClient
      netClient; // netClient instance to create the connection between client and netclient
  private LocalPlayer localPlayer; // Player instance interacting with GUI and ClientProtocol

  private Stage primaryStage; // MAin window

  /**
   * Main method which is called from Launcher, starts gui.
   *
   * @param args Requires args to be sent over from Launcher
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Returns list of client's profiles.
   *
   * @return list of profiles
   */
  public List<PlayerProfile> getPlayerProfiles() {
    return playerProfiles;
  }

  /** Saves all players to profiles.xml */
  public void savePlayerProfiles() {
    XmlHandler.saveXml(playerProfiles);
  }

  /**
   * Sets selected profile to specified profile.
   *
   * @param profile Requires profile to be set as selected
   */
  public void setSelectedProfile(PlayerProfile profile) {
    this.selectedProfile = profile;
  }

  /**
   * Returns selected Profile.
   *
   * @return profile
   */
  public PlayerProfile getSelectedProfile() {
    return selectedProfile;
  }

  /**
   * Returns NetClient, creates new if none is existent.
   *
   * @return instance of NetClient (singleton)
   */
  public NetClient getNetClient() {
    if (netClient == null) {
      netClient = new NetClient(this);
    }
    return netClient;
  }

  /**
   * Initializes LocalPlayer instance.
   *
   * @param controller Requires GameViewController to be sent over to create LocalPlayer with it
   * @return Returns LocalPlayer, creates new one if none is existent
   */
  public LocalPlayer initLocalPlayer(GameViewController controller) {
    localPlayer = new LocalPlayer(this, controller);
    return localPlayer;
  }

  public LocalPlayer getLocalPlayer() {
    return localPlayer;
  }

  /**
   * Method for creating a custom Popup for different Use Cases.
   *
   * @param message Requires Message to be displayed
   */
  public void showPopUp(String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
    alert.setHeaderText(null);
    alert.setTitle("Information");
    DialogPane dialogPane = alert.getDialogPane();
    String style = getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm();
    if (style != null) {
      dialogPane.getStylesheets().add(style);
    }
    dialogPane.getStyleClass().add("dialog");
    alert.showAndWait();
  }

  /**
   * Method for creating a custom Popup for different Use Cases.
   *
   * @param message Requires Message to be displayed
   */
  public void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
    alert.setHeaderText(null);
    alert.setTitle("Error");
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane
        .getStylesheets()
        .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog");
    alert.showAndWait();
  }

  /**
   * Shows dialog.
   *
   * @return input (null string if cancelled)
   */
  public String showDialog(Dialog dialog) {
    Optional<String> result = dialog.showAndWait();
    return result.isPresent() ? result.get() : null;
  }

  /**
   * Returns main window of program.
   *
   * @return stage
   */
  public Stage getStage() {
    return primaryStage;
  }

  /**
   * FXML Method for showing Main Menu, also disconnects client, if you close window.
   *
   * @throws IOException if fxml is not found
   */
  public void showMainMenu() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(this.getClass().getResource("/views/welcomeView.fxml"));
    Parent root = loader.load();
    // Parent root = FXMLLoader.load(this.getClass().getResource("/views/playerLobbyView.fxml"));
    WelcomeViewController controller = loader.getController();
    controller.setModel(this);
    // sound from tile Set is not playing
    Sound.playMusic(Sound.titleMusic);
    primaryStage.setScene(new Scene(root));

    primaryStage.setOnCloseRequest(
        event -> {
          Platform.exit();
          if (netClient != null
              && netClient.getConnection() != null) { // disconnect if connection is open
            netClient.disconnect();
          }

          if (localPlayer != null) {
            localPlayer.setTurn(false); // stop countdown
          }
        });
  }

  /**
   * Open window with first fxml: start game.
   *
   * @author mnetzer
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;

    primaryStage.setTitle("Scrabble 13");
    showMainMenu();
    primaryStage.setMinHeight(700);
    primaryStage.setMinWidth(1000);
    Image logo = new Image(this.getClass().getResource("/pictures/ProgramLogo.png").toString());
    primaryStage.getIcons().add(logo);

    primaryStage.show();
    // load profiles
    this.playerProfiles = XmlHandler.loadProfiles();
    if (playerProfiles.size() > 0) {
      selectedProfile = playerProfiles.get(0);
    } else { // NO profiles created yet (First time)
      TextInputDialog dialog = new TextInputDialog("Mustermann");
      dialog.setTitle("Create your first profile!");
      dialog.setHeaderText(null);
      dialog.setContentText("Please enter your name:");
      DialogPane dialogPane = dialog.getDialogPane();
      dialogPane
          .getStylesheets()
          .add(getClass().getResource("/stylesheets/dialogstyle.css").toExternalForm());
      dialogPane.getStyleClass().add("dialog");

      // Traditional way to get the response value.
      Optional<String> result;
      do {
        result = dialog.showAndWait();
      } while (!result.isPresent());
      playerProfiles.add(
          new PlayerProfile(result.get(), 0, 0, 0, 0, LocalDate.now(), LocalDate.now()));
      selectedProfile = playerProfiles.get(0);
      savePlayerProfiles();
    }
  }
}
