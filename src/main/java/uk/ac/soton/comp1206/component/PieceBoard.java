package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard{

    public PieceBoard(double width, double height) {
        super(3, 3, width, height);
        build();
    }

    public void showPiece(GamePiece gamePiece) {
        this.grid.clean();
        grid.playPiece(gamePiece, 0, 0);
    }


    /**
     * Color the center of the piece
     */
    public void colorCenter() {
        //the X and Y midpoints of the piece board are determined
        double midX = Math.ceil(rows / 2);
        double midY = Math.ceil(cols / 2);

        //the midpoint of those blocks sets the setCentre method true
        //as it's the centre of the block
        this.blocks[(int) midX][(int) midY].setCenter(true);
    }
}
