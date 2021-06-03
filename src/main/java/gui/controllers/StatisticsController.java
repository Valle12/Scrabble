package gui.controllers;

import client.PlayerProfile;
import ft.Sound;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the statistics.
 *
 * @author vihofman
 */
public class StatisticsController {
  // setup for all the statistics
  @FXML private Text name;
  @FXML private Text totalPoints;
  @FXML private Text currentPoints;
  @FXML private Text playedGames;
  @FXML private Text wins;
  @FXML private Text losses;
  @FXML private Text scrabblerSince;

  private PlayerProfile profile;

  /**
   * Method for setting the model.
   *
   * @param profile Player profile
   */
  public void setModel(PlayerProfile profile) {
    this.profile = profile;
    initData(profile);
  }

  /** Method for initializing the statistic profile with data. */
  public void initData(PlayerProfile player) {
    name.setText(player.getName());
    totalPoints.setText(Integer.toString(player.getTotalScore()));
    currentPoints.setText(Integer.toString(player.getTotalScore()));
    playedGames.setText(Integer.toString(player.getPlayedGames()));
    wins.setText(Integer.toString(player.getLosses()));
    losses.setText(Integer.toString(player.getLosses()));
    scrabblerSince.setText(player.getCreation());
  }

  /** Method for closing the statistics when triggered. */
  public void closeStatistics(MouseEvent mouseEvent) { // close the statistics
    Sound.playMusic(Sound.tileSet);
    Stage window = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
    window.close();
  }
}
