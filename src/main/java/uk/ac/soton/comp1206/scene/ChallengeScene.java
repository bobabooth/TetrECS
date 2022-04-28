package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.HashSet;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    /**
     * Game mode
     */
    protected Game game;
    /**
     * Current and next piece
     */
    protected PieceBoard currentPiece, nextPiece;
    /**
     * Game board
     */
    protected GameBoard board;
    /**
     * Timer
     */
    protected HBox timer;
    /**
     * Used for keyboard input
     */
    private int x = 0, y = 0;
    /**
     * Countdown timer at the bottom
     */
    private Rectangle timerBar;
    /**
     * Current score
     */
    protected IntegerProperty score = new SimpleIntegerProperty();
    /**
     * Highest high score to display
     */
    protected IntegerProperty highscore = new SimpleIntegerProperty();

    /**
     * Create a new Single Player challenge scene
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
        challengePane.getStyleClass().add(SettingsScene.getStyle());
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        /* Top */
        var topBar = new HBox(140);
        topBar.setAlignment(Pos.CENTER);
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
        var title = new Text("TetrECS");
        title.getStyleClass().add("bigtitle");

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

        /* Left */
        VBox leftBar = new VBox();
        leftBar.setAlignment(Pos.CENTER);
        leftBar.setPadding(new Insets(0, 0, 0, 15));
        mainPane.setLeft(leftBar);

        var skipPieceText = new Text("Skip piece");
        skipPieceText.getStyleClass().add("heading-selectable");
        skipPieceText.setOnMouseClicked(e -> game.skipPiece());
        Text skipPieceText2 = new Text("50 points\n\n");
        skipPieceText2.getStyleClass().add("channelItem");

        var addLifeText = new Text("Buy one life");
        addLifeText.getStyleClass().add("heading-selectable");
        addLifeText.setOnMouseClicked(e -> game.addLives());
        var addLifeText2 = new Text("100 points\n\n");
        addLifeText2.getStyleClass().add("channelItem");

        var clearGridText = new Text("Clear grid");
        clearGridText.getStyleClass().add("heading-selectable");
        clearGridText.setOnMouseClicked(e -> game.clearAll());
        var clearGridText2 = new Text("200 points");
        clearGridText2.getStyleClass().add("channelItem");

        leftBar.getChildren().addAll(skipPieceText, skipPieceText2, addLifeText, addLifeText2, clearGridText, clearGridText2);

        /* Right */
        var rightBar = new VBox();
        rightBar.setAlignment(Pos.CENTER);
        rightBar.setPadding(new Insets(0, 15, 0, 0));
        mainPane.setRight(rightBar);

        // High score
        var highScoreText = new Text("High Score");
        highScoreText.getStyleClass().add("heading");
        var highScoreNum = new Text();
        highScoreNum.getStyleClass().add("hiscore");
        highScoreNum.textProperty().bind(highscore.asString());

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
        multiplierNum.getStyleClass().add("multiplier");
        multiplierNum.textProperty().bind(game.multiplier.asString());

        // Current piece
        var incomingText = new Text("Incoming");
        incomingText.getStyleClass().add("heading");
        currentPiece = new PieceBoard(100, 100);
        currentPiece.setPadding(new Insets(5, 0, 0, 0));
        currentPiece.blocks[1][1].center();
        currentPiece.setOnMouseClicked(e -> this.rotate());

        // Next piece
        nextPiece = new PieceBoard(75, 75);
        nextPiece.setPadding(new Insets(15, 0, 0, 0));
        nextPiece.setOnMouseClicked(e -> this.swap());

        rightBar.getChildren().addAll(highScoreText, highScoreNum, levelText, levelNum, multiplierText, multiplierNum, incomingText, currentPiece, nextPiece);

        board = new GameBoard(game.getGrid(), (float) gameWindow.getWidth() / 2, (float) gameWindow.getWidth() / 2);
        board.getStyleClass().add("gameBox");
        //Handle block on game-board grid being clicked
        board.setOnBlockClick(this::blockClicked);
        //Handle rotation on block when right-clicked
        board.setOnRightClick(this::rotate);
        mainPane.setCenter(board);

        // Countdown bar
        timer = new HBox();
        timerBar = new Rectangle();
        timerBar.setHeight(10);
        timer.getChildren().add(timerBar);
        mainPane.setBottom(timer);
    }

    /**
     * Replace the current piece with a new piece
     * @param piece piece to be replaced
     */
    protected void nextPiece(GamePiece piece) {
        currentPiece.showPiece(piece);
        nextPiece.showPiece(game.nextPiece);
    }

    /**
     * Rotate the piece right
     */
    protected void rotate() {
        logger.info("Block rotated right");
        Multimedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(1);
        currentPiece.showPiece(game.currentPiece);
    }

    /**
     * Rotate the piece left
     */
    protected void rotateLeft() {
        logger.info("Block rotated left");
        Multimedia.playAudio("rotate.wav");
        game.rotateCurrentPiece(3);
        currentPiece.showPiece(game.currentPiece);
    }

    /**
     * Swap current piece with next piece
     */
    protected void swap() {
        logger.info("Block swapped");
        Multimedia.playAudio("pling.wav");
        game.swapCurrentPiece();
        currentPiece.showPiece(game.currentPiece);
        nextPiece.showPiece(game.nextPiece);
    }

    /**
     * Fade animation after cleared line
     * @param set set of block coordinates
     */
    protected void fadeLine(HashSet<GameBlockCoordinate> set) {
        logger.info("Line cleared");
        board.fadeOut(set);
        Multimedia.playAudio("clear.wav");
    }

    /**
     * Countdown animation
     * @param time time left
     */
    protected void timer(int time) {
        KeyValue start = new KeyValue(timerBar.widthProperty(), timer.getWidth());
        KeyValue green = new KeyValue(timerBar.fillProperty(), Color.GREEN);
        KeyValue yellow = new KeyValue(timerBar.fillProperty(), Color.YELLOW);
        KeyValue red = new KeyValue(timerBar.fillProperty(), Color.RED);
        KeyValue end = new KeyValue(timerBar.widthProperty(), 0);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(new Duration(0), start));
        timeline.getKeyFrames().add(new KeyFrame(new Duration(0), green));
        timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time / 2), yellow));
        timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time * 3 / 4), red));
        timeline.getKeyFrames().add(new KeyFrame(new Duration(time), end));
        timeline.play();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Set up the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");
        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Handle keyboard input
     * @param key key pressed
     */
    private void keyboard(KeyEvent key) {
        switch (key.getCode()) {
            case W:
            case UP:
                if (y > 0) {
                    y--;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case A:
            case LEFT:
                if (x > 0) {
                    x--;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case S:
            case DOWN:
                if (y < game.getRows() - 1) {
                    y++;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case D:
            case RIGHT:
                if (x < game.getCols() - 1) {
                    x++;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case Q:
            case Z:
            case OPEN_BRACKET:
                rotateLeft();
                break;
            case E:
            case C:
            case CLOSE_BRACKET:
                rotate();
                break;
            case X:
            case ENTER:
                blockClicked(board.getBlock(x, y));
                break;
            case R:
            case SPACE:
                swap();
                break;
            case ESCAPE:
                game.stopTimer();
                Multimedia.playAudio("back.mp3");
                gameWindow.startMenu();
                break;
        }
    }

    /**
     * Get the saved high score and display it
     * If the player exceeds it, replace old high score with the current high score
     * If the player spends points, decrease the current high score accordingly
     * @param observable   observable
     * @param oldHighScore old recorded high score
     * @param newHighScore new recorded high score
     */
    protected void getHighScore(ObservableValue<? extends Number> observable, Number oldHighScore, Number newHighScore) {
        logger.info("Updated high score");
        if (newHighScore.intValue() > this.highscore.get()) {
            this.highscore.set(newHighScore.intValue());
        }
        if (newHighScore.intValue() < this.highscore.get()) {
            if (newHighScore.intValue() > ScoresScene.loadScores().get(0).getValue()) {
                this.highscore.set(newHighScore.intValue());
            } else {
                this.highscore.set(ScoresScene.loadScores().get(0).getValue());
            }
        }
        var timeline = new Timeline();
        KeyValue oldScore = new KeyValue(score, oldHighScore);
        KeyValue newScore = new KeyValue(score, newHighScore);
        KeyFrame oldScoreFrame = new KeyFrame(new Duration(0), oldScore);
        KeyFrame newScoreFrame = new KeyFrame(new Duration(100), newScore);
        timeline.getKeyFrames().add(oldScoreFrame);
        timeline.getKeyFrames().add(newScoreFrame);
        timeline.play();
    }

    /**
     * Initialize the scene and start the game
     */
    @Override
    public void initialize() {
        logger.info("Initializing Challenge");
        Multimedia.playMusic("game_start.wav");
        game.setNextPieceListener(this::nextPiece);
        game.setOnLineCleared(this::fadeLine);
        game.setOnGameLoop(this::timer);
        game.score.addListener(this::getHighScore);
        highscore.set(ScoresScene.loadScores().get(0).getValue());
        game.start();
        scene.setOnKeyPressed(this::keyboard);
        game.setOnGameOver(() -> {
            game.stopTimer();
            gameWindow.startScores(game);
        });
    }
}
