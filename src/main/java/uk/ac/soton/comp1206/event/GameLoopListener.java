package uk.ac.soton.comp1206.event;

/**
 * The Game Loop listener is used to link the timer inside the game with the UI timer
 */
public interface GameLoopListener {
    /**
     * Handle when the countdown ends
     * @param n the block that was clicked
     */
    void gameLoop(int n);
}
