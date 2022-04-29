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
    /**
     * Score achieved
     */
    public final SimpleListProperty<Pair<String, Integer>> scores;
    /**
     * Player name
     */
    public final StringProperty nameProperty;
    /**
     * Score to display in a list
     */
    private final ArrayList<VBox> scoreDisplay = new ArrayList<>();

    /**
     * Create a scores list
     */
    public ScoresList() {
        getStyleClass().add("scorelist");
        scores = new SimpleListProperty<>();
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> {
            scoreDisplay.clear();
            getChildren().clear();
            int counter = 0;
            for (Pair<String, Integer> score : scores) {
                counter++;
                if (counter > 10) {
                    break;
                }
                // Center score box
                var scoreBox = new VBox();
                scoreBox.setAlignment(Pos.CENTER);

                // The high score line (name + score)
                var name = new Text(score.getKey() + ":" + score.getValue());
                name.setFill(GameBlock.COLORS[counter]);
                scoreBox.getChildren().add(name);
                scoreDisplay.add(scoreBox);
                getChildren().add(scoreBox);

                reveal();
            }
        });
        nameProperty = new SimpleStringProperty();
        logger.info("Creating Scores List");
    }

    /**
     * Reveal scores with animation
     */
    public void reveal() {
        ArrayList<Transition> transitionArrayList = new ArrayList<>();
        for (var score : scoreDisplay) {
            FadeTransition fade = new FadeTransition(new Duration(100), score);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setCycleCount(2);
            transitionArrayList.add(fade);
        }
        SequentialTransition transition = new SequentialTransition(transitionArrayList.toArray(Animation[]::new));
        transition.play();
        logger.info("Score revealed");
    }
}
