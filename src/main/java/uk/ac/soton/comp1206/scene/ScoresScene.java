package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    private final StringProperty currentName = new SimpleStringProperty("");
    private final BooleanProperty provideScore = new SimpleBooleanProperty(false);
    private final Communicator communicator;
    private ObservableList<Pair<String, Integer>> localScoresList;
    private ScoresList localScores;
    private VBox layout;
    private Text highScoreText;
    private boolean newLocalScore = false;
    private boolean getScores = true;

    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        communicator = gameWindow.getCommunicator();
    }

    /**
     * Load scores from file, if no file found, create 10 random scores
     *
     * @return scores
     */
    public static ArrayList<Pair<String, Integer>> loadScores() {
        ArrayList<Pair<String, Integer>> score = new ArrayList<>();
        File file = new File("scores.txt");
        if (!file.exists()) {
            ArrayList<Pair<String, Integer>> scores = new ArrayList<>();
            scores.add(new Pair<>("Guest", 100));
            scores.add(new Pair<>("Guest", 90));
            scores.add(new Pair<>("Guest", 80));
            scores.add(new Pair<>("Guest", 70));
            scores.add(new Pair<>("Guest", 60));
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
     * Write scores into a file if they don't exist
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
     * Handle HISCORES message
     */
    private void receiveOnlineMessage(String message) {
        String[] parts = message.split(" ", 2);
        if (parts[0].equals("HISCORES")) {
            if (parts.length <= 1) {
                splitMessage();
            } else {
                splitMessage();
            }
        }
    }

    /**
     * Get the online scores into an array
     */
    private void splitMessage() {
        revealMethod();
    }

    /**
     * Request for the online scores
     */

    private void loadOnlineScores() {
        communicator.send("HISCORES");
    }

    /**
     * Reveal the scores
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
     * Handle new high score
     */
    public void newHighScore() {
        if (!game.getScores().isEmpty()) {
            logger.info("No new score");
            provideScore.set(true);
            localScores.reveal();
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
            layout.getChildren().remove(2);
            layout.getChildren().remove(2);
            if (newLocalScore) {
                localScoresList.add(finalScoreNumber, new Pair<>(nameField.getText().replace(":", ""), currentScore));
            }
            writeScores(localScoresList);
            loadOnlineScores();
            newLocalScore = false;
            Multimedia.playAudio("victory.mp3");
        };
        // Local score update
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
            highScoreText.setText("Congratulations, New High Score!");
            Multimedia.playAudio("transition.wav");
            highScoreText.setTextAlignment(TextAlignment.CENTER);
            nameField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    r.run();
                }
            });
            nameField.setAlignment(Pos.CENTER);
            nameField.requestFocus();
            layout.getChildren().add(2, nameField);
            // Save button (text)
            var saveText = new Text("Save");
            saveText.getStyleClass().add("bigWords");
            layout.getChildren().add(3, saveText);
            saveText.setOnMouseClicked(e -> r.run());
        }
        // Show past scores if no new high score
        else {
            Multimedia.playAudio("loser.mp3");
            logger.info("High score not achieved");
            provideScore.set(true);
            localScores.reveal();
        }
    }

    /**
     * Initialize the Scores Scene
     */
    @Override
    public void initialise() {
        logger.info("Initializing " + this.getClass().getName());
        Multimedia.playMusic("end.wav");
        if (!game.getScores().isEmpty()) {
            currentName.set(game.name.getValue());
        }
        loadOnlineScores();
        communicator.addListener(message -> Platform.runLater(() -> receiveOnlineMessage(message)));
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

        StackPane challengePane = new StackPane();
        Text gameOverText = new Text("Game Over");
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        BorderPane mainPane = new BorderPane();
        challengePane.getStyleClass().add(SettingsScene.getStyle());
        root.getChildren().add(challengePane);
        challengePane.getChildren().add(mainPane);
        layout = new VBox();
        highScoreText = new Text("High Scores");
        gameOverText.getStyleClass().add("bigtitle");
        gameOverText.setTextAlignment(TextAlignment.CENTER);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setSpacing(40);
        mainPane.setCenter(layout);
        GridPane gridPane = new GridPane();
        highScoreText.getStyleClass().add("title");
        highScoreText.setTextAlignment(TextAlignment.CENTER);

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(100);
        gridPane.visibleProperty().bind(provideScore);

        layout.getChildren().addAll(gameOverText, highScoreText, gridPane);

        Text local = new Text("Local Scores");
        local.getStyleClass().add("heading");
        localScores = new ScoresList();
        gridPane.add(local, 0, 0);
        gridPane.add(localScores, 0, 1);
        localScores.setAlignment(Pos.CENTER);

        localScoresList = FXCollections.observableArrayList(loadScores());

        localScoresList.sort((score1, score2) -> (score2.getValue().compareTo(score1.getValue())));
        SimpleListProperty<Pair<String, Integer>> localScore = new SimpleListProperty<>(localScoresList);
        localScores.getScoreProperty().bind(localScore);
        localScores.getNameProperty().bind(currentName);
    }

    interface highScoreInterface {
        void run();
    }
}
