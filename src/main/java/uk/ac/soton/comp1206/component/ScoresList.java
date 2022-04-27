package uk.ac.soton.comp1206.component;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Hold and display a list of names and associated scores
 */
public class ScoresList extends VBox {
    private static final Logger logger = LogManager.getLogger(ScoresList.class);
    public final SimpleListProperty<Pair<String, Integer>> scores;
    public final StringProperty nameProperty;
    private final ArrayList<HBox> scoreDisplay = new ArrayList<>();

    /**
     * Create a scores list
     */
    public ScoresList() {
        getStyleClass().add("scorelist");
        scores = new SimpleListProperty<>();
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> update());
        nameProperty = new SimpleStringProperty();
        logger.info("Creating Scores List");
    }

    /**
     * Add the scores to the list
     */
    public void update() {
        scoreDisplay.clear();
        getChildren().clear();
        int counter = 0;
        for (Pair<String, Integer> score : scores) {
            counter++;
            if (counter > 10) {
                break;
            }
            var scoreBox = new HBox();
            scoreBox.getStyleClass().add("scoreitem");
            scoreBox.setAlignment(Pos.CENTER);

            var name = new Text(score.getKey() + ":" + score.getValue());
            name.setFill(GameBlock.COLORS[counter]);
            scoreBox.getChildren().add(name);
            scoreDisplay.add(scoreBox);
            this.getChildren().add(scoreBox);

            reveal();
        }
    }

    /**
     * Reveal scores with animation
     */
    public void reveal() {
        ArrayList<Transition> transition = new ArrayList<>();
        for (HBox score : scoreDisplay) {
            FadeTransition fade = new FadeTransition(new Duration(100), score);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setCycleCount(2);
            transition.add(fade);
        }
        SequentialTransition seqTransition = new SequentialTransition(transition.toArray(Animation[]::new));
        seqTransition.play();
        logger.info("Score reveal animation");
    }
}
