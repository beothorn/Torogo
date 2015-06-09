package beothorn.github.com.toroidalgo;

import android.graphics.Point;
import android.os.Bundle;

import java.io.Serializable;
import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logic.BoardListener;
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
            publishPlayStone(line, column);
        if(goBoard.gameHasEnded()){
            publishToogleDeadStone(line, column);
        }
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
        publishPass();
    }

    public void callResign() {
        publishResign();
    }

    private boolean isMyTurn() {
        if(myColor == GoBoard.StoneColor.ANY) return true;
        return turn.equals(myColor);
    }

    @Override
    public void updateScore(int _blackScore, int _whiteScore) {
        if(scoreListener != null)
            scoreListener.setScore(_blackScore, _whiteScore);
    }

    @Override
    public void nextToPlay(GoBoard.StoneColor nextToPlay) {
        boolean gameEnded = nextToPlay == null;
        if(gameEnded){
            if(stateListener != null)
                stateListener.onMarkStonesPhaseStart();
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
    }

    public void playStone(int line, int column) {
        goBoard.playStone(column, line);
        changePlayingColor();
    }

    public void pass(){
        goBoard.passTurn();
        changePlayingColor();
    }

    public void continueGame(int turn) {
        goBoard.continueGame(turn);
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

    private void publishToogleDeadStone(int line, int column) {
        publisher.toggleDeadStone(line, column, turn);
    }

    private void publishPlayStone(int line, int column) {
        publisher.playStone(line, column, turn);
    }

    private void publishPass() {
        publisher.pass(turn);
    }

    private void publishResign() {
        publisher.resign(turn);
    }

    public GoBoard.StoneColor getWinner() {
        return goBoard.winner();
    }

    public void endMarkingStones() {
        goBoard.endMarkingStones();
    }

    public int getWhiteScore() {
        return goBoard.whiteScore();
    }

    public int getBlackScore() {
        return goBoard.blackScore();
    }

    private String asString() {
        Point lastPlayedPiece = goBoard.getLastPlayedPiece();


        return lastPlayedPiece.x+","+lastPlayedPiece.y+"|"+goBoard.nextToPlay()+"|"+goBoard.printOut();
    }

    public void save(Bundle outState) {

        Point lastPlayedPiece = goBoard.getLastPlayedPiece();
        if(lastPlayedPiece == null){
            lastPlayedPiece = new Point(-1, -1);
        }

        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.x", lastPlayedPiece.x);
        outState.putInt(CLASSIFIED_NAME + "lastPlayedPiece.y", lastPlayedPiece.y);
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
        switch (play.get("TYPE")){
            case Publisher.TOGGLE_DEAD_STONE:
                toggleDeadStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PLAY:
                playStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PASS:
                pass();
                break;
            case Publisher.RESIGN:
                resign();
                break;
        }
    }
}
