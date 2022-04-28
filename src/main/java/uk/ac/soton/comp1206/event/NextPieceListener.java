package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener handles the upcoming piece after the current piece is placed
 */
public interface NextPieceListener {
    /**
     * Gets a new piece
     * @param gamePiece the game piece to come
     */
    void nextPiece(GamePiece gamePiece);
}
