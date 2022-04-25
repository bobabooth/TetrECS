package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        /*
         Top
         */
        var topBar = new HBox(200);
        topBar.setAlignment(Pos.CENTER);
        //BorderPane.setAlignment(topBar, Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
        mainPane.setTop(topBar);

        // Score
        var scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        var scoreText = new Text("Score");
        scoreText.getStyleClass().add("heading");
        var scoreNum = new Text();
        scoreNum.getStyleClass().add("score");
        scoreNum.textProperty().bind(game.score.asString());
        scoreBox.getChildren().addAll(scoreText, scoreNum);

        // Title
        var title = new Text("Single Player");
        title.getStyleClass().add("title");

        // Lives
        var livesBox = new VBox();
        livesBox.setAlignment(Pos.CENTER);
        var livesText = new Text("Lives");
        livesText.getStyleClass().add("heading");
        var livesNum = new Text();
        livesNum.getStyleClass().add("lives");
        livesNum.textProperty().bind(game.lives.asString());
        livesBox.getChildren().addAll(livesText, livesNum);

        topBar.getChildren().addAll(scoreBox, title, livesBox);

        /*
         Left
         */
        VBox leftBar = new VBox();
        leftBar.setAlignment(Pos.CENTER);
        leftBar.setPadding(new Insets(0, 0, 0, 20));
        mainPane.setLeft(leftBar);

        // Test
        var test = new Text("test");
        test.getStyleClass().add("heading");

        leftBar.getChildren().addAll(test);

        /*
         Right
         */
        VBox rightBar = new VBox();
        rightBar.setAlignment(Pos.CENTER);
        rightBar.setPadding(new Insets(0, 20, 0, 0));
        mainPane.setRight(rightBar);

        // High score
        var highScoreText = new Text("High Score");
        highScoreText.getStyleClass().add("heading");
        var highScoreNum = new Text();
        highScoreNum.getStyleClass().add("hiscore");

        // Level
        var levelText = new Text("Level");
        levelText.getStyleClass().add("heading");
        var levelNum = new Text();
        levelNum.getStyleClass().add("level");
        levelNum.textProperty().bind(game.level.asString());

        // Multiplier
        var multiplierText = new Text("Multiplier");
        multiplierText.getStyleClass().add("heading");
        var multiplierNum = new Text();
        multiplierNum.getStyleClass().add("heading");
        multiplierNum.textProperty().bind(game.multiplier.asString());

        rightBar.getChildren().addAll(highScoreText, highScoreNum, levelText, levelNum, multiplierText, multiplierNum);

        var board = new GameBoard(game.getGrid(), (float) gameWindow.getWidth() / 2, (float) gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        Multimedia.playMusic("game_start.wav");
        game.start();
    }

}
