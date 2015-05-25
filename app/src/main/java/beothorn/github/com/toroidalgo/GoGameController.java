package beothorn.github.com.toroidalgo;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import beothorn.github.com.toroidalgo.go.impl.logic.BoardListener;
import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoBoard;

public class GoGameController implements BoardListener{

    private GoBoard goBoard;
    private int size = 9;
    private StateListener stateLabel;
    private ScoreListener scoreListener;
    private ScoreListener whiteScore;
    private Publisher publisher;

    private GoBoard.StoneColor myColor;
    private GoBoard.StoneColor turn = GoBoard.StoneColor.BLACK;

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
        this.stateLabel = stateLabel;
    }

    public void callPass() {
        if(!isMyTurn()) return;
        publishPass();
    }

    public void callResign() {
        publishResign();
    }

    private boolean isMyTurn() {
        return turn.equals(myColor);
    }

    public void setContinueButton(Button continueButton) {
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishContinue();
            }
        });
    }

    @Override
    public void updateScore(int _blackScore, int _whiteScore) {
        if(scoreListener != null)
            scoreListener.setScore(_blackScore, _whiteScore);
    }

    @Override
    public void nextToPlay(GoBoard.StoneColor _nextToPlay) {
        if(_nextToPlay == null){
            if(stateLabel!= null)
                stateLabel.setState("Game ended, please mark the dead stones");
        }else{
            if(_nextToPlay.equals(GoBoard.StoneColor.BLACK)){
                if(stateLabel!= null)
                    stateLabel.setState("Black's turn");
            }else{
                if(stateLabel!= null)
                    stateLabel.setState("White's turn");
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

    public void setColor(GoBoard.StoneColor color){
        myColor = color;
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

    private void publishContinue() {
        publisher.continueGame(turn == GoBoard.StoneColor.BLACK ? Publisher.BLACK_TURN : Publisher.WHITE_TURN, turn);
    }

    private void publishResign() {
        publisher.resign(turn);
    }

}
