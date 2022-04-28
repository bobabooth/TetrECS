package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
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

/**
 * Contains instructions and dynamically generated pieces
 */
public class InstructionsScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    /**
     * Create a new instructions scene
     * @param gameWindow the Game Window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    /**
     * Initialize the Instructions Scene
     */
    @Override
    public void initialize() {
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
     * Build the Instructions Scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(this.gameWindow.getWidth(), this.gameWindow.getHeight());

        var instructionsPane = new StackPane();
        instructionsPane.setMaxWidth(gameWindow.getWidth());
        instructionsPane.setMaxHeight(gameWindow.getHeight());
        instructionsPane.getStyleClass().add(SettingsScene.theme.getText());
        root.getChildren().add(instructionsPane);

        var mainPane = new BorderPane();
        instructionsPane.getChildren().add(mainPane);

        /* Top */
        var topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
        mainPane.setTop(topBar);

        var instructionsText = new Text("Instructions");
        instructionsText.getStyleClass().add("title");
        topBar.getChildren().add(instructionsText);

        /* Center */
        var centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        mainPane.setCenter(centerBox);

        ImageView image = new ImageView(Multimedia.getImage("Instructions.png"));
        image.setFitHeight(340);
        image.setPreserveRatio(true);

        var piecesText = new Text("Game Pieces");
        piecesText.getStyleClass().add("heading");

        var grid = new VBox();
        grid.setAlignment(Pos.CENTER);
        grid.setSpacing(10);

        // Dynamically generated game pieces
        for (int x = 0; x < 3; x++) {
            var hBox = new HBox();
            grid.getChildren().add(hBox);
            hBox.setAlignment(Pos.CENTER);
            hBox.setSpacing(10);
            for (int y = 0; y < 5; y++) {
                var pieceBoard = new PieceBoard(50, 50);
                GamePiece gamePiece = GamePiece.createPiece(x * 5 + y);
                pieceBoard.showPiece(gamePiece);
                hBox.getChildren().add(pieceBoard);
            }
        }
        centerBox.getChildren().addAll(image, piecesText, grid);
    }
}
