package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the UI and methods for high scores at the end of a game
 */
public class ScoresScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);
    private final StringProperty currentName = new SimpleStringProperty("");
    private final BooleanProperty provideScore = new SimpleBooleanProperty(false);
    private ObservableList<Pair<String, Integer>> localScoresList;
    private ScoresList localScores;
    private VBox centerBox;
    private boolean newLocalScore = false;
    private boolean getScores = true;

    /**
     * Create a new scores scene
     * @param gameWindow the Game Window
     * @param game the game
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        logger.info("Creating Scores Scene");
    }

    /**
     * Load scores from file, if no file found, create 10 random scores
     * @return scores
     */
    public static ArrayList<Pair<String, Integer>> loadScores() {
        ArrayList<Pair<String, Integer>> score = new ArrayList<>();
        File file = new File("scores.txt");
        if (!file.exists()) {
            ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
            scores.add(new Pair<>("Guest", 300));
            scores.add(new Pair<>("Guest", 250));
            scores.add(new Pair<>("Guest", 200));
            scores.add(new Pair<>("Guest", 150));
            scores.add(new Pair<>("Guest", 100));
            scores.add(new Pair<>("Guest", 50));
            scores.add(new Pair<>("Guest", 40));
            scores.add(new Pair<>("Guest", 30));
            scores.add(new Pair<>("Guest", 20));
            scores.add(new Pair<>("Guest", 10));
            writeScores(scores);
        }
        try {
            FileInputStream reader = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(reader));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    score.add(new Pair<>(parts[0], Integer.parseInt(parts[1])));
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("File not found");
        }
        return score;
    }

    /**
     * Create dummy scores.txt for first launch
     */
    public static void writeScores(List<Pair<String, Integer>> scores) {
        scores.sort((score1, score2) -> (score2.getValue()).compareTo(score1.getValue()));
        try {
            if (new File("scores.txt").createNewFile()) {
                logger.info("File created");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("File creation error");
        }
        try {
            BufferedWriter scoreWriter = new BufferedWriter(new FileWriter("scores.txt"));
            int scoresNumber = 0;
            for (Pair<String, Integer> score : scores) {
                scoreWriter.write(score.getKey() + ":" + score.getValue() + "\n");
                scoresNumber++;
                if (scoresNumber > 9) {
                    break;
                }
            }
            scoreWriter.close();
            logger.info("Scores saved");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Scores save error");
        }
    }

    /**
     * Reveal scores
     */
    public void revealMethod() {
        if (getScores) {
            newHighScore();
            getScores = false;
            return;
        }
        provideScore.set(true);
        localScores.reveal();
    }

    /**
     * Request name if new high score achieved
     */
    public void newHighScore() {
        if (!game.getScores().isEmpty()) {
            provideScore.set(true);
            localScores.reveal();
            logger.info("No new score");
            return;
        }
        int scoreNumber = 0;
        int finalScoreNumber = scoreNumber;
        int currentScore = game.score.get();
        var nameField = new TextField();
        int lowestLocalScore = localScoresList.get(localScoresList.size() - 1).getValue();
        nameField.setMaxWidth(200);
        nameField.setPromptText("Enter your name");

        highScoreInterface r = () -> {
            currentName.set(nameField.getText().replace(":", ""));
            centerBox.getChildren().remove(1);
            centerBox.getChildren().remove(1);
            centerBox.getChildren().remove(1);

            if (newLocalScore) {
                localScoresList.add(finalScoreNumber, new Pair<>(nameField.getText().replace(":", ""), currentScore));
            }
            writeScores(localScoresList);
            Platform.runLater(this::revealMethod);
            newLocalScore = false;
            Multimedia.playAudio("victory.mp3");
        };
        // Score comparison
        if (currentScore > lowestLocalScore) {
            for (Pair<String, Integer> score : localScoresList) {
                if (currentScore > score.getValue()) {
                    newLocalScore = true;
                }
                scoreNumber++;
            }
        }
        // New high score prompt
        if (newLocalScore) {
            var newScoreText = new Text("New Score Recorded!");
            newScoreText.getStyleClass().add("title");
            centerBox.getChildren().add(1, newScoreText);

            // Text field
            nameField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    r.run();
                }
            });
            nameField.setAlignment(Pos.CENTER);
            nameField.requestFocus();
            centerBox.getChildren().add(2, nameField);

            // Confirm
            var saveText = new Text("Confirm");
            saveText.getStyleClass().add("heading-selectable");
            saveText.setOnMouseClicked(e -> r.run());
            centerBox.getChildren().add(3, saveText);
        }
        // Show past scores if no new high score
        else {
            Multimedia.playAudio("loser.mp3");
            logger.info("High score not achieved");
            provideScore.set(true);
            provideScore.set(true);
            localScores.reveal();
        }
    }

    /**
     * Initialize the Scores Scene
     */
    @Override
    public void initialize() {
        logger.info("Initializing " + this.getClass().getName());
        Multimedia.playMusic("end.wav");
        if (!game.getScores().isEmpty()) {
            currentName.set(game.name.getValue());
        }
        Platform.runLater(this::revealMethod);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Multimedia.playAudio("back.mp3");
                gameWindow.startMenu();
            }
        });
    }

    /**
     * Build the Scores Scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add(SettingsScene.getStyle());
        root.getChildren().add(scoresPane);

        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        /* Top */
        var topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
        mainPane.setTop(topBar);

        Text gameOverText = new Text("Game Over");
        gameOverText.getStyleClass().add("bigtitle");
        topBar.getChildren().add(gameOverText);

        /* Center */
        centerBox = new VBox();
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setSpacing(10);
        mainPane.setCenter(centerBox);

        var highScoreText = new Text("High Scores");
        highScoreText.getStyleClass().add("title");
        highScoreText.visibleProperty().bind(provideScore);

        var gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.visibleProperty().bind(provideScore);

        localScores = new ScoresList();
        localScores.setAlignment(Pos.CENTER);
        gridPane.getChildren().add(localScores);

        centerBox.getChildren().addAll(highScoreText, gridPane);

        localScoresList = FXCollections.observableArrayList(loadScores());
        localScoresList.sort((score1, score2) -> (score2.getValue().compareTo(score1.getValue())));
        SimpleListProperty<Pair<String, Integer>> localScore = new SimpleListProperty<>(localScoresList);
        localScores.nameProperty.bind(currentName);
        localScores.scores.bind(localScore);
    }

    /**
     * Interface for removing TextField and 'Confirm' Text
     */
    interface highScoreInterface {
        void run();
    }
}
