package ft;

import javafx.scene.media.*;
/**
 * @author nsiebler This class contains the sounds in the system. Each sound has a method and
 *     refers to a sound in a folder. The sound are called by a Method. This class also contains a
 *     method to mute all sounds The state of the mute variable is saved in a XML Document and
 *     read out of it in order to save preferences
 * This variable contains the soundPath. The regarding sound file name will be added to the path
 * and than the sound will be played
 */
public class Sound {

  public static String playerCreated = "PlayerCreated.mp3";
  public static String rightWord = "RightWord.mp3";
  public static String tileSet = "TileSet.mp3";
  public static String titleMusic = "TitleMusic.mp3";
  public static String finishSound = "FinishSound.mp3";
  public static boolean muteStatus = false;
  static MediaPlayer mediaPlayer;
  static MediaPlayer mediaPlayer2;

  // Mute function
  public static void mute() {
    muteStatus = true;
    if (mediaPlayer.isAutoPlay()) {
      mediaPlayer2.stop();
      mediaPlayer2.dispose();
    }
  }

  // UnMute function
  public static void unMute() {
    muteStatus = false;
    playMusic(titleMusic);
  }

  /**
   * All the Functions that play the sound are the same, but all the functions have a name with the
   * certain sound file as a variable, to access the certain sound file
   */

  // change all the function into one function and save the string names of the files
  // as class Variables TO DO
  public static void playMusic(String audioName) {
    if (!muteStatus) {
      try {
        String path = Sound.class.getResource("/sounds/" + audioName).toURI().toString();
        Thread.sleep(50);
        if (audioName.equals(titleMusic)) {
          Media media2 = new Media(path);
          mediaPlayer2 = new MediaPlayer(media2);
          mediaPlayer2.setVolume(0.1);
          mediaPlayer2.setAutoPlay(true);
          mediaPlayer2.setCycleCount(MediaPlayer.INDEFINITE);
        } else {
          Media media = new Media(path);
          mediaPlayer = new MediaPlayer(media);
          mediaPlayer.setVolume(0.1);
          mediaPlayer.setAutoPlay(true);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void muteTitleMusic() {
    if (mediaPlayer2.isAutoPlay()) {
      mediaPlayer2.dispose();
    }
  }
}
