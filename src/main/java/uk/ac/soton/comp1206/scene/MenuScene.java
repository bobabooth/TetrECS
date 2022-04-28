package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
     * The current selected menu item
     */
    private int selector;
    /**
     * Menu items
     */
    private Text local, instructions, settings, quit;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        SettingsScene.loadSettings();
        logger.info("Creating Menu Scene");
    }

    /**
     * Handle keyboard input in menu
     * @param key key pressed
     */
    private void keyboard(KeyEvent key) {
        switch (key.getCode()) {
            case ESCAPE:
                App.getInstance().shutdown();
                break;
            case W:
            case UP:
                if (selector > 1) {
                    selector--;
                    paint();
                } else if (selector < 1) {
                    selector = 1;
                    paint();
                }
                break;
            case S:
            case DOWN:
                if (selector < 4) {
                    selector++;
                    paint();
                }
                break;
            case ENTER:
                if (selector == 1) {
                    gameWindow.startChallenge();
                } else if (selector == 2) {
                    gameWindow.startInstructions();
                } else if (selector == 3) {
                    gameWindow.startSettings();
                } else if (selector == 4) {
                    App.getInstance().shutdown();
                }
                break;
        }
    }

    /**
     * Create hover effect over text when selected
     */
    public void paint() {
        switch (selector) {
            case 1 -> {
                local.getStyleClass().remove("menuItem");
                local.getStyleClass().add("menuItem-fixed");
                instructions.getStyleClass().add("menuItem");
                instructions.getStyleClass().remove("menuItem-fixed");
                settings.getStyleClass().add("menuItem");
                settings.getStyleClass().remove("menuItem-fixed");
                quit.getStyleClass().add("menuItem");
                quit.getStyleClass().remove("menuItem-fixed");
            }
            case 2 -> {
                local.getStyleClass().add("menuItem");
                local.getStyleClass().remove("menuItem-fixed");
                instructions.getStyleClass().remove("menuItem");
                instructions.getStyleClass().add("menuItem-fixed");
                settings.getStyleClass().add("menuItem");
                settings.getStyleClass().remove("menuItem-fixed");
                quit.getStyleClass().add("menuItem");
                quit.getStyleClass().remove("menuItem-fixed");
            }
            case 3 -> {
                local.getStyleClass().add("menuItem");
                local.getStyleClass().remove("menuItem-fixed");
                instructions.getStyleClass().add("menuItem");
                instructions.getStyleClass().remove("menuItem-fixed");
                settings.getStyleClass().remove("menuItem");
                settings.getStyleClass().add("menuItem-fixed");
                quit.getStyleClass().add("menuItem");
                quit.getStyleClass().remove("menuItem-fixed");
            }
            case 4 -> {
                local.getStyleClass().add("menuItem");
                local.getStyleClass().remove("menuItem-fixed");
                instructions.getStyleClass().add("menuItem");
                instructions.getStyleClass().remove("menuItem-fixed");
                settings.getStyleClass().add("menuItem");
                settings.getStyleClass().remove("menuItem-fixed");
                quit.getStyleClass().remove("menuItem");
                quit.getStyleClass().add("menuItem-fixed");
            }
        }
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        selector = 0;
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add(SettingsScene.theme.getText());
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

        local = new Text("Play");
        local.getStyleClass().add("menuItem");
        local.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            gameWindow.startChallenge();
        });
        instructions = new Text("Instructions");
        instructions.getStyleClass().add("menuItem");
        instructions.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            gameWindow.startInstructions();
        });
        settings = new Text("Settings");
        settings.getStyleClass().add("menuItem");
        settings.setOnMouseClicked(e -> {
            Multimedia.playAudio("select.mp3");
            gameWindow.startSettings();
        });
        quit = new Text("Quit");
        quit.getStyleClass().add("menuItem");
        quit.setOnMouseClicked(e -> App.getInstance().shutdown());

        menu.getChildren().addAll(local, instructions, settings, quit);
    }

    /**
     * Initialize the menu
     */
    @Override
    public void initialize() {
        logger.info("Initializing " + this.getClass().getName());
        Multimedia.playMusic("menu.mp3");
        scene.setOnKeyPressed(this::keyboard);
    }
}
