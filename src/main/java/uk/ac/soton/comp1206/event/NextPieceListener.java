package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece listener handles the upcoming piece after the current piece is placed
 */
public interface NextPieceListener {
    void nextPiece(GamePiece gamePiece);
}
