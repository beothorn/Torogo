package beothorn.github.com.toroidalgo;

public interface StateListener {
    void onPassWhite();
    void onPassBlack();
    void onWhiteTurn();
    void onBlackTurn();
    void onResignWhite();
    void onResignBlack();
    void onMarkStonesPhaseStart();
}
