package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Used to display an upcoming piece in a 3x3 grid
 */
public class PieceBoard extends GameBoard {

    /**
     * Create a piece board
     * @param width  width of board
     * @param height height of board
     */
    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
        build();
    }

    /**
     * Display the piece
     * @param gamePiece piece to be displayed
     */
    public void showPiece(GamePiece gamePiece) {
        this.grid.clean();
        grid.playPiece(gamePiece, 0, 0);
    }
}
