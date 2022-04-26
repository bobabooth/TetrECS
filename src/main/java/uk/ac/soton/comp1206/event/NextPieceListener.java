package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Block Clicked listener is used to handle the event when a block in a GameBoard is clicked. It
 * passes the GameBlock that was clicked in the message
 */
public interface NextPieceListener {

    /**
     * Handle a block clicked event
     *
     * @param gamePiece the block that was clicked
     */
    void nextPiece(GamePiece gamePiece);
}
