package beothorn.github.com.toroidalgo;

import android.os.Bundle;

import java.util.List;
import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.go.impl.logic.BoardListener;
import beothorn.github.com.toroidalgo.go.impl.logic.BoardPosition;
import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoBoard;
import beothorn.github.com.toroidalgo.publisher.Publisher;

public class GoGameController implements BoardListener{

    public static final String CLASSIFIED_NAME = "beothorn.github.com.toroidalgo.GoGameController";
    private GoBoard goBoard;
    private int size = 9;
    private StateListener stateListener;
    private ScoreListener scoreListener;
    private Publisher publisher;

    private GoBoard.StoneColor myColor;
    private GoBoard.StoneColor turn = GoBoard.StoneColor.BLACK;
    private boolean gameFinished = false;

    public GoGameController(Publisher publisher) {
        this(publisher, GoBoard.StoneColor.ANY);
    }

    public GoGameController(Publisher publisher, GoBoard.StoneColor myColor){
        this.publisher = publisher;
        this.myColor = myColor;
        goBoard = new ToroidalGoBoard(size);
        goBoard.setBoardListener(this);
    }

    public void play(int line, int column){
        if(!isMyTurn()) return;

        if(goBoard.canPlayStone(column, line))
            publisher.playStone(line, column, turn);
        if(gameHasEnded()){
            publisher.toggleDeadStone(line, column, turn);
        }
    }

    public boolean gameHasEnded() {
        return goBoard.gameHasEnded();
    }

    public GoBoard.StoneColor getPieceAt(int line, int column){
        return goBoard.stoneAt(column, line);
    }

    public boolean stoneAtPositionIsLastPlayedStone(int line, int column){
        return goBoard.stoneAtPositionIsLastPlayedStone(column, line);
    }

    public int getSize() {
        return size;
    }

    public void setStateListener(StateListener stateLabel) {
        this.stateListener = stateLabel;
    }

    public void callPass() {
        if(!isMyTurn()) return;
        publisher.pass(turn);
    }

    public void callResign() {
        publisher.resign(turn);
    }

    public void callEndMarkingStones() {
        publisher.endMarkingStones();
    }

    private boolean isMyTurn() {
        return myColor == GoBoard.StoneColor.ANY || turn.equals(myColor);
    }

    @Override
    public void updateScore(int _blackScore, float _whiteScore) {
        if(scoreListener != null)
            scoreListener.setScore(_blackScore, _whiteScore);
    }

    @Override
    public void nextToPlay(GoBoard.StoneColor nextToPlay) {
        boolean gameEnded = nextToPlay == null;
        if(gameEnded) {
            if (stateListener != null) {
                if (getWinner() != null) {
                    if (getWinner().equals(GoBoard.StoneColor.BLACK))
                        stateListener.onResignWhite();
                    else
                        stateListener.onResignBlack();
                } else {
                    stateListener.onMarkStonesPhaseStart();
                }
            }
        }else{
            if(nextToPlay.equals(GoBoard.StoneColor.BLACK)){
                stateListener.onBlackTurn();
            }
            if(nextToPlay.equals(GoBoard.StoneColor.WHITE)){
                stateListener.onWhiteTurn();
            }
        }
    }

    @Override
    public void nextToPlayOnPass(GoBoard.StoneColor nextToPlay) {
        boolean gameEnded = nextToPlay == null;
        if(gameEnded){
            if(stateListener != null){
                stateListener.onMarkStonesPhaseStart();
            }
        }else{
            if(nextToPlay.equals(GoBoard.StoneColor.BLACK)){
                stateListener.onPassWhite();
            }
            if(nextToPlay.equals(GoBoard.StoneColor.WHITE)){
                stateListener.onPassBlack();
            }
        }
    }

    public void setScoreListener(ScoreListener scoreListener) {
        this.scoreListener = scoreListener;
    }

    public void toggleDeadStone(int line, int column) {
        goBoard.toggleDeadStone(column, line);
        stateListener.onMarkStone();
    }

    public void playStone(int line, int column) {
        goBoard.playStone(column, line);
        changePlayingColor();
    }

    public void pass(){
        goBoard.passTurn();
        changePlayingColor();
    }

    private void changePlayingColor() {
        if(turn.equals(GoBoard.StoneColor.BLACK)){
            turn = GoBoard.StoneColor.WHITE;
        }else{
            turn = GoBoard.StoneColor.BLACK;
        }
    }

    public void resign(){
        goBoard.resign();
    }

    public GoBoard.StoneColor getWinner() {
        return goBoard.winner();
    }

    public void endMarkingStones() {
        gameFinished = true;
        goBoard.endMarkingStones();
        stateListener.onMarkStonesPhaseEnded();
    }

    public float getWhiteScore() {
        return goBoard.whiteScore();
    }

    public int getBlackScore() {
        return goBoard.blackScore();
    }

    public void save(Bundle outState) {

        BoardPosition lastPlayedPiece = goBoard.getLastPlayedPiece();
        if(lastPlayedPiece == null){
            lastPlayedPiece = new BoardPosition(-1, -1);
        }

        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.x", lastPlayedPiece.getColumn());
        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.y", lastPlayedPiece.getLine());
        outState.putSerializable(CLASSIFIED_NAME + "goBoard.nextToPlay", goBoard.nextToPlay());
        outState.putString(CLASSIFIED_NAME + "goBoard.printOut", goBoard.printOut());
    }

    public void recoverFrom(Bundle savedInstanceState) {
        int lastPieceX = savedInstanceState.getInt(CLASSIFIED_NAME + "lastPlayedPiece.x");
        int lastPieceY = savedInstanceState.getInt(CLASSIFIED_NAME + "lastPlayedPiece.y");
        GoBoard.StoneColor playingColor = (GoBoard.StoneColor) savedInstanceState.getSerializable(CLASSIFIED_NAME + "goBoard.nextToPlay");
        String boardSetup = savedInstanceState.getString(CLASSIFIED_NAME + "goBoard.printOut");
        goBoard.loadGame(lastPieceX, lastPieceY, playingColor, boardSetup);
    }

    public void playLocally(Map<String, Integer> play) {
        if (gameFinished) return;

        switch (play.get("TYPE")){
            case Publisher.TOGGLE_DEAD_STONE:
                GoLogger.log("Controller Playing TOGGLE_DEAD_STONE: line: " + play.get("line")+" column: "+ play.get("column"));
                toggleDeadStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PLAY:
                GoLogger.log("Controller Playing PLAY: line: " + play.get("line")+" column: "+ play.get("column"));
                playStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PASS:
                GoLogger.log("Controller Playing PASS");
                pass();
                break;
            case Publisher.RESIGN:
                GoLogger.log("Controller Playing RESIGN");
                resign();
                break;
            case Publisher.END_GAME:
                GoLogger.log("Controller Playing END GAME");
                endMarkingStones();
                break;
        }
    }

    public Map<GoBoard.StoneColor, List<List<BoardPosition>>> getTerritoriesOwnership(){
        return goBoard.getTerritoriesOwnership();
    }


}
