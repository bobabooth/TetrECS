package uk.ac.soton.comp1206.scene;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

public class SettingsScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(SettingsScene.class);
    private static double musicVolume = 50;
    private static double audioVolume = 50;
    private Slider musicSlider;
    private Slider audioSlider;

    /**
     * Create a new settings scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public SettingsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Settings Scene");
    }

    @Override
    public void initialise() {
        logger.info("Initializing " + this.getClass().getName());
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var settingsPane = new StackPane();
        settingsPane.setMaxWidth(gameWindow.getWidth());
        settingsPane.setMaxHeight(gameWindow.getHeight());
        settingsPane.getStyleClass().add("menu-background");
        root.getChildren().add(settingsPane);

        var mainPane = new BorderPane();
        settingsPane.getChildren().add(mainPane);

        /*
         Top
         */
        var topBar = new HBox(170);
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
        mainPane.setTop(topBar);

        var title = new Text("Settings");
        title.getStyleClass().add("title");
        topBar.getChildren().add(title);

        var centerBox = new VBox();
        mainPane.setCenter(centerBox);

        var volumeControl = new Text("Volume Control");
        volumeControl.getStyleClass().add("heading");
        centerBox.getChildren().add(volumeControl);

        var volumeBox = new HBox(10);
        centerBox.getChildren().add(volumeBox);

        var musicText = new Text("Music");
        GridPane.setHalignment(musicText, HPos.CENTER);
        musicText.getStyleClass().add("heading");
        volumeBox.getChildren().add(musicText);

        musicSlider = new Slider(0, 100, musicVolume);
        musicSlider.setPrefSize(300, 20);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMajorTickUnit(25);
        volumeBox.getChildren().add(musicSlider);

        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            musicVolume = (int) musicSlider.getValue();
            Multimedia.musicPlayer.setVolume(musicVolume / 100);
        });

        var audioText = new Text("Audio");
        GridPane.setHalignment(audioText, HPos.CENTER);
        audioText.getStyleClass().add("heading");
        volumeBox.getChildren().add(audioText);

        audioSlider = new Slider(0, 100, audioVolume);
        audioSlider.setPrefSize(300, 20);
        audioSlider.setShowTickMarks(true);
        audioSlider.setMajorTickUnit(25);
        volumeBox.getChildren().add(audioSlider);

        audioSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioVolume = (int) audioSlider.getValue();
            Multimedia.audioPlayer.setVolume(audioVolume / 100);
        });
    }
}
