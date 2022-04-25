package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.HashSet;
import java.util.Random;

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
    protected GamePiece currentPiece;

    /**
     * Initial values
     */
    public IntegerProperty score = new SimpleIntegerProperty(0);
    public IntegerProperty level = new SimpleIntegerProperty(0);
    public IntegerProperty lives = new SimpleIntegerProperty(3);
    public IntegerProperty multiplier = new SimpleIntegerProperty(1);

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
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // Place the current piece
        if (grid.playPiece(currentPiece, x, y)){
            afterPiece();
            nextPiece();
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
        currentPiece = spawnPiece();
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
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


}
