package uk.ac.soton.comp1206.scene;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
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
    private static Text style = new Text("challenge-background");

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

        // Title
        var title = new Text("Settings");
        title.getStyleClass().add("title");
        topBar.getChildren().add(title);

        /*
        Center
         */
        var centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(centerBox);

        var volumeControl = new Text("Volume Control");
        volumeControl.getStyleClass().add("heading");
        centerBox.getChildren().add(volumeControl);

        var volumeBox = new HBox(100);
        volumeBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(volumeBox);

        // Music
        var musicBox = new VBox(10);
        musicBox.setAlignment(Pos.CENTER);
        var musicText = new Text("Music");
        musicText.getStyleClass().add("heading");
        musicSlider = new Slider(0, 100, musicVolume);
        musicSlider.setPrefSize(300, 20);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMajorTickUnit(25);
        musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            musicVolume = (int) musicSlider.getValue();
            Multimedia.musicPlayer.setVolume(musicVolume / 100);
        });
        musicBox.getChildren().addAll(musicText, musicSlider);

        // Audio
        var audioBox = new VBox(10);
        audioBox.setAlignment(Pos.CENTER);
        var audioText = new Text("Audio");
        audioText.getStyleClass().add("heading");
        audioSlider = new Slider(0, 100, audioVolume);
        audioSlider.setPrefSize(300, 20);
        audioSlider.setShowTickMarks(true);
        audioSlider.setMajorTickUnit(25);
        audioSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            audioVolume = (int) audioSlider.getValue();
            Multimedia.audioPlayer.setVolume(audioVolume / 100);
        });
        audioBox.getChildren().addAll(audioText, audioSlider);

        volumeBox.getChildren().addAll(musicBox, audioBox);

        // Background editor
        var themeGrid = new GridPane();
        centerBox.getChildren().add(themeGrid);
        themeGrid.setHgap(15);
        themeGrid.setVgap(15);
        themeGrid.setAlignment(Pos.CENTER);
        var imagesLabel = new Text("Select Theme");
        GridPane.setHalignment(imagesLabel, HPos.CENTER);
        imagesLabel.getStyleClass().add("heading");
        themeGrid.add(imagesLabel, 0, 0, 3, 1);

        ImageView one = new ImageView(Multimedia.getImage("1.jpg"));
        themeGrid.add(one, 0, 1);
        one.setFitWidth(240);
        one.setPreserveRatio(true);
        one.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("menu-background");
            logger.info("Set theme to 1");
        });

        ImageView two = new ImageView(Multimedia.getImage("2.jpg"));
        themeGrid.add(two, 1, 1);
        two.setFitWidth(240);
        two.setPreserveRatio(true);
        two.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("challenge-background");
            logger.info("Set theme to 2");
        });

        ImageView three = new ImageView(Multimedia.getImage("3.jpg"));
        themeGrid.add(three, 2, 1);
        three.setFitWidth(240);
        three.setPreserveRatio(true);
        three.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("background3");
            logger.info("Set theme to 3");
        });

        ImageView four = new ImageView(Multimedia.getImage("4.jpg"));
        themeGrid.add(four, 0, 2);
        four.setFitWidth(240);
        four.setPreserveRatio(true);
        four.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("background4");
            logger.info("Set theme to 4");
        });

        ImageView five = new ImageView(Multimedia.getImage("5.jpg"));
        themeGrid.add(five, 1, 2);
        five.setFitWidth(240);
        five.setPreserveRatio(true);
        five.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("background5");
            logger.info("Set theme to 5");
        });

        ImageView six = new ImageView(Multimedia.getImage("6.jpg"));
        themeGrid.add(six, 2, 2);
        six.setFitWidth(240);
        six.setPreserveRatio(true);
        six.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            style = new Text("background6");
            logger.info("Set theme to 6");
        });
    }
}
