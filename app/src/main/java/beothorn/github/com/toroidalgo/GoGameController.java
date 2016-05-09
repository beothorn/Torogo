package beothorn.github.com.toroidalgo;

import android.os.Bundle;

import java.util.List;
import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.go.impl.logic.BoardListener;
import beothorn.github.com.toroidalgo.go.impl.logic.BoardPosition;
import beothorn.github.com.toroidalgo.go.impl.logic.GoMatch;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoMatch;
import beothorn.github.com.toroidalgo.publisher.Publisher;

public class GoGameController implements BoardListener{

    public static final String CLASSIFIED_NAME = "beothorn.github.com.toroidalgo.GoGameController";
    private GoMatch goMatch;
    private int size = 9;
    private StateListener stateListener;
    private ScoreListener scoreListener;
    private Publisher publisher;

    private GoMatch.StoneColor myColor;
    private GoMatch.StoneColor turn = GoMatch.StoneColor.BLACK;
    private boolean gameFinished = false;

    public GoGameController(Publisher publisher) {
        this(publisher, GoMatch.StoneColor.ANY);
    }

    public GoGameController(Publisher publisher, GoMatch.StoneColor myColor){
        this.publisher = publisher;
        this.myColor = myColor;
        goMatch = new ToroidalGoMatch(size);
        goMatch.setBoardListener(this);
    }

    public void play(int line, int column){
        if(!isMyTurn()) return;

        if(goMatch.canPlayStone(column, line))
            publisher.playStone(line, column, turn);
        if(gameHasEnded()){
            publisher.toggleDeadStone(line, column, turn);
        }
    }

    public boolean gameHasEnded() {
        return goMatch.gameHasEnded();
    }

    public GoMatch.StoneColor getPieceAt(int line, int column){
        return goMatch.stoneAt(column, line);
    }

    public boolean stoneAtPositionIsLastPlayedStone(int line, int column){
        return goMatch.stoneAtPositionIsLastPlayedStone(column, line);
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
        return myColor == GoMatch.StoneColor.ANY || turn.equals(myColor);
    }

    @Override
    public void updateScore(int _blackScore, float _whiteScore) {
        if(scoreListener != null)
            scoreListener.setScore(_blackScore, _whiteScore);
    }

    @Override
    public void nextToPlay(GoMatch.StoneColor nextToPlay) {
        boolean gameEnded = nextToPlay == null;
        if(gameEnded) {
            if (stateListener != null) {
                if (getWinner() != null) {
                    if (getWinner().equals(GoMatch.StoneColor.BLACK))
                        stateListener.onResignWhite();
                    else
                        stateListener.onResignBlack();
                } else {
                    stateListener.onMarkStonesPhaseStart();
                }
            }
        }else{
            if(nextToPlay.equals(GoMatch.StoneColor.BLACK)){
                stateListener.onBlackTurn();
            }
            if(nextToPlay.equals(GoMatch.StoneColor.WHITE)){
                stateListener.onWhiteTurn();
            }
        }
    }

    @Override
    public void nextToPlayOnPass(GoMatch.StoneColor nextToPlay) {
        boolean gameEnded = nextToPlay == null;
        if(gameEnded){
            if(stateListener != null){
                stateListener.onMarkStonesPhaseStart();
            }
        }else{
            if(nextToPlay.equals(GoMatch.StoneColor.BLACK)){
                stateListener.onPassWhite();
            }
            if(nextToPlay.equals(GoMatch.StoneColor.WHITE)){
                stateListener.onPassBlack();
            }
        }
    }

    public void setScoreListener(ScoreListener scoreListener) {
        this.scoreListener = scoreListener;
    }

    public void toggleDeadStone(int line, int column) {
        goMatch.toggleDeadStone(column, line);
        stateListener.onMarkStone();
    }

    public void playStone(int line, int column) {
        goMatch.playStone(column, line);
        changePlayingColor();
    }

    public void pass(){
        goMatch.passTurn();
        changePlayingColor();
    }

    private void changePlayingColor() {
        if(turn.equals(GoMatch.StoneColor.BLACK)){
            turn = GoMatch.StoneColor.WHITE;
        }else{
            turn = GoMatch.StoneColor.BLACK;
        }
    }

    public void resign(){
        goMatch.resign();
    }

    public GoMatch.StoneColor getWinner() {
        return goMatch.winner();
    }

    public void endMarkingStones() {
        gameFinished = true;
        goMatch.endMarkingStones();
        stateListener.onMarkStonesPhaseEnded();
    }

    public float getWhiteScore() {
        return goMatch.whiteScore();
    }

    public int getBlackScore() {
        return goMatch.blackScore();
    }

    public void save(Bundle outState) {

        BoardPosition lastPlayedPiece = goMatch.getLastPlayedPiece();
        if(lastPlayedPiece == null){
            lastPlayedPiece = new BoardPosition(-1, -1);
        }

        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.x", lastPlayedPiece.getColumn());
        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.y", lastPlayedPiece.getLine());
        outState.putSerializable(CLASSIFIED_NAME + "goMatch.nextToPlay", goMatch.nextToPlay());
        outState.putString(CLASSIFIED_NAME + "goMatch.printOut", goMatch.printOut());
    }

    public void recoverFrom(Bundle savedInstanceState) {
        int lastPieceX = savedInstanceState.getInt(CLASSIFIED_NAME + "lastPlayedPiece.x");
        int lastPieceY = savedInstanceState.getInt(CLASSIFIED_NAME + "lastPlayedPiece.y");
        GoMatch.StoneColor playingColor = (GoMatch.StoneColor) savedInstanceState.getSerializable(CLASSIFIED_NAME + "goMatch.nextToPlay");
        String boardSetup = savedInstanceState.getString(CLASSIFIED_NAME + "goMatch.printOut");
        goMatch.loadGame(lastPieceX, lastPieceY, playingColor, boardSetup);
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

    public Map<GoMatch.StoneColor, List<List<BoardPosition>>> getTerritoriesOwnership(){
        return goMatch.getTerritoriesOwnership();
    }


}
