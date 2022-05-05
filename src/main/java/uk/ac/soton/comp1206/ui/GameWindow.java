package uk.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.scene.*;

/**
 * The GameWindow is the single window for the game where everything takes place. To move between screens in the game,
 * we simply change the scene.
 *
 * The GameWindow has methods to launch each of the different parts of the game by switching scenes. You can add more
 * methods here to add more screens to the game.
 */
public class GameWindow {
    private static final Logger logger = LogManager.getLogger(GameWindow.class);
    private final int width;
    private final int height;
    private final Stage stage;
    private BaseScene currentScene;
    private Scene scene;

    /**
     * Create a new GameWindow attached to the given stage with the specified width and height
     * @param stage  stage
     * @param width  width
     * @param height height
     */
    public GameWindow(Stage stage, int width, int height) {
        this.width = width;
        this.height = height;
        this.stage = stage;

        // Setup window
        setupStage();

        // Setup resources
        setupResources();

        // Setup default scene
        setupDefaultScene();

        // Go to menu
        startMenu();
    }

    /**
     * Set up the font and any other resources we need
     */
    private void setupResources() {
        logger.info("Loading resources");

        // We need to load fonts here due to the Font loader bug with spaces in URLs in the CSS files
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Regular.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-Bold.ttf"), 32);
        Font.loadFont(getClass().getResourceAsStream("/style/Orbitron-ExtraBold.ttf"), 32);
    }

    /**
     * Display the main menu
     */
    public void startMenu() {
        loadScene(new MenuScene(this));
    }

    /**
     * Display the single player challenge
     */
    public void startChallenge() {
        loadScene(new ChallengeScene(this));
    }

    /**
     * Display scores
     * @param game game mode to display scores from
     */
    public void startScores(Game game) {
        loadScene(new ScoresScene(this, game));
    }

    /**
     * Display instructions
     */
    public void startInstructions() {
        loadScene(new InstructionsScene(this));
    }

    /**
     * Display settings
     */
    public void startSettings() {
        loadScene(new SettingsScene(this));
    }

    /**
     * Set up the default settings for the stage itself (the window), such as the title and minimum width and height.
     */
    public void setupStage() {
        stage.setTitle("TetrECS");
        stage.setMinWidth(width);
        stage.setMinHeight(height + 20);
        stage.setOnCloseRequest(ev -> App.getInstance().shutdown());
    }

    /**
     * Load a given scene which extends BaseScene and switch over.
     * @param newScene new scene to load
     */
    public void loadScene(BaseScene newScene) {
        // Cleanup remains of the previous scene
        cleanup();

        // Create the new scene and set it up
        newScene.build();
        currentScene = newScene;
        scene = newScene.setScene();
        stage.setScene(scene);

        // Initialize the scene when ready
        Platform.runLater(() -> currentScene.initialize());
    }

    /**
     * Set up the default scene (an empty black scene) when no scene is loaded
     */
    public void setupDefaultScene() {
        this.scene = new Scene(new Pane(), width, height, Color.BLACK);
        stage.setScene(this.scene);
    }

    /**
     * When switching scenes, perform any cleanup needed, such as removing previous listeners
     */
    public void cleanup() {
        logger.info("Clearing up previous scene");
    }

    /**
     * Get the current scene being displayed
     * @return scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Get the width of the Game Window
     * @return width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Get the height of the Game Window
     * @return height
     */
    public int getHeight() {
        return this.height;
    }
}
