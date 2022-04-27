package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard{

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

    /**
     * Color the center of the piece
     */
    public void colorCenter() {
        int x = rows / 2;
        int y = cols / 2;
        this.blocks[x][y].setCenter(true);
    }
}
