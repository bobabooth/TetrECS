package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
        SettingsScene.loadSettings();
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        // Logo
        ImageView logo = new ImageView(Multimedia.getImage("TetrECS.png"));
        logo.setFitHeight(130);
        logo.setPreserveRatio(true);
        mainPane.setCenter(logo);

        // Logo animation
        RotateTransition rt = new RotateTransition(Duration.millis(2000), logo);
        rt.setToAngle(5);
        rt.setFromAngle(-5);
        rt.setAutoReverse(true);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.play();

        // Menu items
        var menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setAlignment(Pos.CENTER);
        mainPane.setBottom(menu);

        var local = new Text("Play");
        local.getStyleClass().add("menuItem");
        local.setOnMouseClicked(
                e -> {
                    Multimedia.playAudio("select.mp3");
                    gameWindow.startChallenge();
                });

        var instructions = new Text("Instructions");
        instructions.getStyleClass().add("menuItem");
        instructions.setOnMouseClicked(
                e -> {
                    Multimedia.playAudio("select.mp3");
                    gameWindow.startInstructions();
                });

        var settings = new Text("Settings");
        settings.getStyleClass().add("menuItem");
        settings.setOnMouseClicked(
                e -> {
                    Multimedia.playAudio("select.mp3");
                    gameWindow.startSettings();
                });

        var quit = new Text("Quit");
        quit.getStyleClass().add("menuItem");
        quit.setOnMouseClicked((e) -> App.getInstance().shutdown());

        menu.getChildren().addAll(local, instructions, settings, quit);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playMusic("menu.mp3");
        scene.setOnKeyPressed(
                e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        App.getInstance().shutdown();
                    }
                });
    }

    /**
     * Handle when the Start Game button is pressed
     *
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

}
