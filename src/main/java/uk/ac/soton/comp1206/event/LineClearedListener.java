package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

public interface LineClearedListener {
    void lineCleared(HashSet<GameBlockCoordinate> lineCleared);
}
