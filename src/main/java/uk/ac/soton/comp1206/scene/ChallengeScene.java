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
import javafx.scene.layout.*;
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

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    protected PieceBoard currentPiece;
    protected PieceBoard nextPiece;
    protected GameBoard board;
    protected StackPane timer;

    protected int posX = 0;
    protected int posY = 0;
    protected Rectangle timerBar;

    protected IntegerProperty highscore = new SimpleIntegerProperty();

    protected IntegerProperty score = new SimpleIntegerProperty();



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
        challengePane.getStyleClass().add(SettingsScene.getStyle());
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

        var skipPieceText = new Text("Skip piece");
        skipPieceText.getStyleClass().add("heading-selectable");
        skipPieceText.setOnMouseClicked(e -> game.skipPiece());
        Text skipPieceText2 = new Text("Costs 50 points\n\n");
        skipPieceText2.getStyleClass().add("channelItem");

        var addLifeText = new Text("Buy one life");
        addLifeText.getStyleClass().add("bigWords");
        addLifeText.setOnMouseClicked(e -> game.addLives());
        var addLifeText2 = new Text("Costs 100 points\n\n");
        addLifeText2.getStyleClass().add("words");

        var clearGridText = new Text("Clear grid");
        clearGridText.getStyleClass().add("bigWords");
        clearGridText.setOnMouseClicked(e -> game.clearAll());
        var clearGridText2 = new Text("Costs 200 points");
        clearGridText2.getStyleClass().add("words");

        leftBar.getChildren().addAll(skipPieceText, skipPieceText2, addLifeText, addLifeText2, clearGridText, clearGridText2);

        /*
         Right
         */
        var rightBar = new VBox();
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

        // Incoming piece
        var incomingText = new Text("Incoming");
        incomingText.getStyleClass().add("heading");
        currentPiece = new PieceBoard(100,100);
        currentPiece.colorCenter();
        currentPiece.setOnMouseClicked(e -> this.rotate());

        nextPiece = new PieceBoard(75, 75);
        nextPiece.setPadding(new Insets(15, 0, 0, 0));
        nextPiece.setOnMouseClicked(e -> this.swap());

        rightBar.getChildren().addAll(highScoreText, highScoreNum, levelText, levelNum, multiplierText, multiplierNum, incomingText, currentPiece, nextPiece);

        var board = new GameBoard(game.getGrid(), (float) gameWindow.getWidth() / 2, (float) gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        timer = new StackPane();
        mainPane.setBottom(timer);
        timerBar = new Rectangle();
        timerBar.setHeight(10);
        timer.getChildren().add(timerBar);
        StackPane.setAlignment(timerBar, Pos.CENTER_RIGHT);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Support keyboard input
     *
     * @param key keyboard input
     */
    protected void keyboard(KeyEvent key) {
        switch (key.getCode()) {
            case W:
            case UP:
                if (posY > 0) {
                    posY--;
                    this.board.hover(this.board.getBlock(posX, posY));
                }
                break;
            case A:
            case LEFT:
                if (posX > 0) {
                    posX--;
                    this.board.hover(this.board.getBlock(posX, posY));
                }
                break;
            case S:
            case DOWN:
                if (posY < game.getRows() - 1) {
                    posY++;
                    this.board.hover(this.board.getBlock(posX, posY));
                }
                break;
            case D:
            case RIGHT:
                if (posX < game.getCols() - 1) {
                    posX++;
                    this.board.hover(this.board.getBlock(posX, posY));
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
                blockClicked(board.getBlock(posX, posY));
                logger.info("challenge");
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
     * Replace the current piece with a new piece
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
     * Fade animation after line is cleared
     *
     * @param set set
     */
    protected void fadeLine(HashSet<GameBlockCoordinate> set) {
        logger.info("Line cleared");
        Multimedia.playAudio("clear.wav");
        for (GameBlockCoordinate block : set) {
            board.fadeOut(board.getBlock(block.getX(), block.getY()));
        }
    }

    /**
     * Timer at the bottom
     *
     * @param time time
     */
    protected void timer(int time) {
        Timeline timeline = new Timeline();

        KeyValue start = new KeyValue(timerBar.widthProperty(), timer.getWidth());
        KeyValue green = new KeyValue(timerBar.fillProperty(), Color.GREEN);
        KeyValue yellow = new KeyValue(timerBar.fillProperty(), Color.YELLOW);
        KeyValue red = new KeyValue(timerBar.fillProperty(), Color.RED);
        KeyValue end = new KeyValue(timerBar.widthProperty(), 0);

        timeline.getKeyFrames().add(new KeyFrame(new Duration(0), start));
        timeline.getKeyFrames().add(new KeyFrame(new Duration(0), green));
        timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time / 2), yellow));
        timeline.getKeyFrames().add(new KeyFrame(new Duration((float) time * 3 / 4), red));
        timeline.getKeyFrames().add(new KeyFrame(new Duration(time), end));

        timeline.play();
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
     * Get the stored high score and display it
     * If the player exceeds it, replace old high score with the current high score
     * If the player spends points, decrease the current high score accordingly
     *
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
        Timeline timeline = new Timeline();
        KeyValue oldScore = new KeyValue(score, oldHighScore);
        KeyValue newLocalScore = new KeyValue(score, newHighScore);
        KeyFrame oldScoreFrame = new KeyFrame(new Duration(0), oldScore);
        KeyFrame newScoreFrame = new KeyFrame(new Duration(100), newLocalScore);
        timeline.getKeyFrames().add(oldScoreFrame);
        timeline.getKeyFrames().add(newScoreFrame);
        timeline.play();
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        Multimedia.playMusic("game_start.wav");
        game.setNextPieceListener(this::nextPiece);
        game.setOnLineCleared(this::fadeLine);
        game.setOnGameLoop(this::timer);
        game.scoreProperty().addListener(this::getHighScore);
        highscore.set(ScoresScene.loadScores().get(0).getValue());
        game.start();
        scene.setOnKeyPressed(this::keyboard);
        game.setOnGameOver(() -> {
            game.stopTimer();
            gameWindow.startScores(game);
        });
    }
}
