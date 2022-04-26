package uk.ac.soton.comp1206.component;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Class which creates the score list
 */
public class ScoresList extends VBox {

    private static final Logger logger = LogManager.getLogger(ScoresList.class);
    protected static Text name;
    public final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();
    private final ArrayList<HBox> scoreSpaces = new ArrayList<>();
    private final StringProperty nameProperty = new SimpleStringProperty();

    /**
     * Create the Scores list
     */
    public ScoresList() {
        getStyleClass().add("scorelist");
        this.setAlignment(Pos.CENTER);
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) e -> update());
    }

    public ListProperty<Pair<String, Integer>> getScoreProperty() {
        return scores;
    }

    public StringProperty getNameProperty() {
        return nameProperty;
    }

    /**
     * Add the scores to the list
     */
    public void update() {
        logger.info("Score updated");

        scoreSpaces.clear();
        getChildren().clear();
        int counter = 0;

        for (Pair<String, Integer> score : scores) {
            counter++;
            if (counter > 10) {
                break;
            }
            var scoreBox = new HBox();
            name = new Text(score.getKey() + ":" + score.getValue());
            scoreBox.getStyleClass().add("scoreitem");
            scoreBox.setAlignment(Pos.CENTER);

            name.setFill(GameBlock.COLOURS[counter]);
            name.setTextAlignment(TextAlignment.CENTER);
            scoreBox.getChildren().add(name);

            this.getChildren().add(scoreBox);
            scoreSpaces.add(scoreBox);
            reveal();
        }
    }

    /**
     * Reveal the scores with animation
     */
    public void reveal() {
        ArrayList<Transition> transitions = new ArrayList<>();
        for (HBox score : scoreSpaces) {
            FadeTransition fade = new FadeTransition(new Duration(200), score);
            fade.setFromValue(0);
            fade.setToValue(1);
            transitions.add(fade);
        }
        SequentialTransition seqTransition = new SequentialTransition(transitions.toArray(Animation[]::new));
        seqTransition.play();
    }
}
