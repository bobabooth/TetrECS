package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static uk.ac.soton.comp1206.game.GamePiece.PIECES;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Keep track of the current piece
     */
    public GamePiece currentPiece;
    protected ScheduledFuture loop;


    public GamePiece nextPiece;

    protected ScheduledExecutorService timer;

    int oldLevel = 0;

    /**
     * Initial values
     */
    public IntegerProperty score = new SimpleIntegerProperty(0);
    public IntegerProperty level = new SimpleIntegerProperty(0);
    public IntegerProperty lives = new SimpleIntegerProperty(3);
    public IntegerProperty multiplier = new SimpleIntegerProperty(1);

    protected NextPieceListener nextPieceListener = null;
    protected LineClearedListener lineClearedListener = null;
    protected GameLoopListener gameLoopListener = null;
    protected GameOverListener gameOverListener = null;
    protected ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

    public StringProperty name = new SimpleStringProperty();


    public ArrayList<Pair<String, Integer>> getScores() {
        return this.scores;
    }


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
        timer = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener();
    }

    public void setNextPieceListener(NextPieceListener listener) {
        nextPieceListener = listener;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void setOnLineCleared(LineClearedListener listener) {
        lineClearedListener = listener;
    }

    public void gameLoopListener() {
        if (gameLoopListener != null) {
            gameLoopListener.gameLoop(getTimerDelay());
        }
    }

    public void setOnGameLoop(GameLoopListener listener) {
        gameLoopListener = listener;
    }

    public void setOnGameOver(GameOverListener listener) {
        gameOverListener = listener;
    }

    /**
     * Calculate time allowed for each round
     */
    public int getTimerDelay() {
        return Math.max(12000 - 500 * this.level.get(), 2500);
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        this.nextPiece = spawnPiece();
        nextPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX() - 1;
        int y = gameBlock.getY() - 1;

        // Place the current piece
        if (grid.playPiece(currentPiece, x, y)){
            afterPiece();
            nextPiece();
            Multimedia.playAudio("place.wav");
            restartLoop();
        }
        else {
            Multimedia.playAudio("fail.wav");
        }

        // Check for lines to clear
        afterPiece();
    }

    /**
     * Handle the clearance of lines
     */
    public void afterPiece() {
        var clear = new HashSet<IntegerProperty>();
        var cleared = new HashSet<GameBlockCoordinate>();

        int linesCleared = 0;

        // Check for full horizontal lines (rows) --
        for (int x = 0; x < cols; x++) {
            int counter = 0;
            for (int y = 0; y < rows; y++) {
                if (grid.get(x, y) == 0) break;
                counter++;
            }
            if (counter == rows) {
                linesCleared++;
                for (int y = 0; y < rows; y++) {
                    clear.add(grid.getGridProperty(x, y));
                }
            }
        }
        // Check for full vertical lines (columns) ||
        for (int y = 0; y < rows; y++) {
            int counter = 0;
            for (int x = 0; x < cols; x++) {
                if (grid.get(x, y) == 0) break;
                counter++;
            }
            if (counter == cols) {
                linesCleared++;
                for (int x = 0; x < this.cols; x++) {
                    clear.add(grid.getGridProperty(x, y));
                }
            }
        }

        // Check if lines cleared
        if (linesCleared > 0) {
            // Clear block
            for (IntegerProperty block : clear) {
                block.set(0);
            }
            score(linesCleared, clear.size());

            // Multiplier increase by 1 if the next piece also clears lines
            multiplier.set(multiplier.add(1).get());

            // Sets the level
            level.set(Math.floorDiv(score.get(), 1000));

            // Plays sound when level up
            levelSounds(level.get());

            if (this.lineClearedListener != null) {
                this.lineClearedListener.lineCleared(cleared);
            }
        } else {
            // Multiplier resets to 1 if no lines cleared
            multiplier.set(1);
        }
    }

    /**
     * Calculate the score
     * @param lines number of lines cleared
     * @param blocks number of grid blocks cleared
     */
    public void score(int lines, int blocks) {
        int multiplayer = multiplier.get();
        score.set(score.add(lines * blocks * 10 * multiplayer).get());
    }

    /**
     * Create a randomly generated piece
     * @return GamePiece
     */
    public GamePiece spawnPiece() {
        Random random = new Random();
        return GamePiece.createPiece(random.nextInt(PIECES));
    }

    /**
     * Replace the current piece with a new piece
     */
    public void nextPiece() {
        currentPiece = nextPiece;
        nextPiece = spawnPiece();
        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(currentPiece);
        }
        logger.info("Current piece is now: " + currentPiece);
    }

    /**
     * Rotate the current piece
     *
     * @param times number of rotations specified by user
     */
    public void rotateCurrentPiece(int times) {
        currentPiece.rotate(times);
    }

    /**
     * Swap between current and next piece
     */
    public void swapCurrentPiece() {
        GamePiece gamePiece = currentPiece;
        currentPiece = nextPiece;
        nextPiece = gamePiece;
        logger.info("Pieces swapped");
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Stop the timer
     */
    public void stopTimer() {
        this.timer.shutdownNow();
    }

    /**
     * Play sound when player level up
     *
     * @param currentLevel current level
     */
    public void levelSounds(int currentLevel) {
        if (currentLevel != oldLevel) {
            Multimedia.playAudio("level.wav");
            oldLevel = currentLevel;
            logger.info("Level up");
        }
    }

    /**
     * Remove a life when timer ends
     */
    public void livesReset() {
        if (this.lives.get() > 0) {
            lives.set(lives.get() - 1);
            Multimedia.playAudio("lifelose.wav");
            logger.info("Life lost");
        } else {
            logger.info("Game over");
            if (gameOverListener != null) {
                Platform.runLater(() -> gameOverListener.gameOver());
            }
        }
    }


    /**
     * Reset multiplier after timer ends
     */
    public void multiplierReset() {
        if (this.multiplier.get() > 1) {
            logger.info("Multiplier set to 1");
            multiplier.set(1);
        }
    }

    /**
     * Loop events when timer end
     */
    public void gameLoop() {
        livesReset();
        multiplierReset();
        nextPiece();
        gameLoopListener();
        loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * Restart the timer after a piece is successfully placed
     */
    public void restartLoop() {
        loop.cancel(false);
        loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener();
        logger.info("Timer reset");
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }
}
