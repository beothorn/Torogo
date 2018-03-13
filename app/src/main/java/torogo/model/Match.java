package torogo.model;

import torogo.model.impl.Board;

public interface Match {

    boolean isToroidal();
    boolean isParallel(); //For the future. ;)

    String printOut();

    enum Action {PLAY, PASS, RESIGN, TOGGLE_DEAD_STONE, ACCEPT_DEAD_STONES}
    void handle(Action action, Object... args);

    StoneColor stoneAt(int x, int y);

}
