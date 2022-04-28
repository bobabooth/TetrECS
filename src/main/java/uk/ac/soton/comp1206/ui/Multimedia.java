package uk.ac.soton.comp1206.ui;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.SettingsScene;

/**
 * Get media files
 */
public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Multimedia.class);
    /**
     * Used to play music
     */
    public static MediaPlayer musicPlayer;
    /**
     * Used to play audio
     */
    public static MediaPlayer audioPlayer;

    /**
     * Play background music
     * @param music music name
     */
    public static void playMusic(String music) {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
        String toPlay = Multimedia.class.getResource("/music/" + music).toExternalForm();
        musicPlayer = new MediaPlayer(new Media(toPlay));
        musicPlayer.setVolume(SettingsScene.musicVolume / 100);
        musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        musicPlayer.play();
        logger.info("Music played: " + music);
    }

    /**
     * Play audio effects
     * @param sound sound name
     */
    public static void playAudio(String sound) {
        String toPlay = Multimedia.class.getResource("/sounds/" + sound).toExternalForm();
        audioPlayer = new MediaPlayer(new Media(toPlay));
        audioPlayer.setVolume(SettingsScene.audioVolume / 100);
        audioPlayer.play();
        logger.info("Audio played: " + sound);
    }

    /**
     * Retrieve image
     * @param file image name
     * @return selected image
     */
    public static Image getImage(String file) {
        logger.info("Image " + file + " selected");
        return new Image(Multimedia.class.getResource("/images/" + file).toExternalForm());
    }
}