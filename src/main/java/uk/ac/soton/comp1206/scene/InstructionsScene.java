package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

public class InstructionsScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
    }


    /**
     * Initialize the scene
     */
    @Override
    public void initialise() {
        logger.info("Initializing " + this.getClass().getName());
        scene.setOnKeyPressed(
                e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        Multimedia.playAudio("back.mp3");
                        gameWindow.startMenu();
                    }
                });
    }

    /**
     * Build the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(this.gameWindow.getWidth(), this.gameWindow.getHeight());

        var instructions = new StackPane();
        instructions.setMaxWidth(gameWindow.getWidth());
        instructions.setMaxHeight(gameWindow.getHeight());
        instructions.getStyleClass().add(SettingsScene.getStyle());
        root.getChildren().add(instructions);

        var pane = new BorderPane();
        instructions.getChildren().add(pane);

        var vBox = new VBox();
        vBox.setAlignment(Pos.TOP_CENTER);
        BorderPane.setAlignment(vBox, Pos.CENTER);
        pane.setCenter(vBox);

        var heading = new Text("Instructions");
        heading.getStyleClass().add("title");
        vBox.getChildren().add(heading);

        ImageView image = new ImageView(Multimedia.getImage("Instructions.png"));
        image.setFitHeight(350);
        image.setPreserveRatio(true);
        vBox.getChildren().add(image);

        var pieces = new Text("Game Pieces");
        pieces.getStyleClass().add("heading");
        vBox.getChildren().add(pieces);

        VBox grid = new VBox();
        grid.setAlignment(Pos.CENTER);
        grid.setSpacing(10);

        for (int x = 0; x < 3; x++) {
            HBox hBox = new HBox();
            grid.getChildren().add(hBox);
            hBox.setAlignment(Pos.CENTER);
            int y = 0;
            hBox.setSpacing(10);
            while (y < 5) {
                PieceBoard pieceBoard = new PieceBoard( 50, 50);
                GamePiece gamePiece = GamePiece.createPiece(x * 5 + y);
                pieceBoard.showPiece(gamePiece);
                hBox.getChildren().add(pieceBoard);
                y++;
            }
        }
        vBox.getChildren().add(grid);
    }
}
