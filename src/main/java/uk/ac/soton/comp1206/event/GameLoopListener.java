package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to link the timer inside the game with the UI timer
 */
public interface GameLoopListener {
    void gameLoop(int n);
}
