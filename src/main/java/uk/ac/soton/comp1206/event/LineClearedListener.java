package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

/**
 * The Line Cleared listener takes a Set of GameBlockCoordinates and add it to the Game class to trigger when lines are cleared.
 */
public interface LineClearedListener {
    /**
     * Handle a line cleared event
     * @param lineCleared the line that was cleared
     */
    void lineCleared(HashSet<GameBlockCoordinate> lineCleared);
}
