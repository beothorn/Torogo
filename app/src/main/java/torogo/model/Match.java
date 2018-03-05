package torogo.model;

import torogo.model.impl.Board;

public interface Match {

    boolean isToroidal();
    boolean isParallel(); //For the future. ;)

    enum Action {PLAY, PASS, RESIGN, TOGGLE_DEAD_STONE, ACCEPT_DEAD_STONES}
    void handle(Action action, Object... args);

    interface Listener {
        void onChange();
    }

    StoneColor stoneAt(int x, int y);

}
