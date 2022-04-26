package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard{

    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
    }

    public void showPiece(GamePiece gamePiece) {
        this.grid.clean();
        grid.playPiece(gamePiece, 0, 0);
    }
}
