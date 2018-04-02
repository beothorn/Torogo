package torogo.model;

import torogo.model.impl.Board;

public interface Match {

    enum Action {PLAY, PASS, RESIGN, TOGGLE_DEAD_STONE, ACCEPT_DEAD_STONES}
    void handle(Action action, Object... args);

    boolean isToroidal();
    boolean isParallel(); //For the future. ;)

    StoneColor stoneAt(int x, int y);

    int blackScore();
    float whiteScore();

    String printOut();

}
