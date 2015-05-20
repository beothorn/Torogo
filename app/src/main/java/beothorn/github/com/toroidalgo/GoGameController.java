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
    private TextView stateLabel;
    private TextView blackScore;
    private TextView whiteScore;
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

    public void setStateLabel(TextView stateLabel) {
        this.stateLabel = stateLabel;
    }

    public void setPassButton(Button passButton) {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isMyTurn()) return;
                publishPass();
            }
        });
    }

    private boolean isMyTurn() {
        return turn.equals(myColor);
    }

    public void setResignButton(Button resignButton) {
        resignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishResign();
            }
        });
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
        blackScore.setText("Black: "+_blackScore);
        whiteScore.setText("White: " + _whiteScore);
    }

    @Override
    public void nextToPlay(GoBoard.StoneColor _nextToPlay) {
        if(_nextToPlay == null){
            stateLabel.setText("Game ended, please mark the dead stones");
        }else{
            if(_nextToPlay.equals(GoBoard.StoneColor.BLACK)){
                stateLabel.setText("Black's turn");
            }else{
                stateLabel.setText("White's turn");
            }
        }
    }

    public void setBlackScore(TextView blackScore) {
        this.blackScore = blackScore;
    }

    public void setWhiteScore(TextView whiteScore) {
        this.whiteScore = whiteScore;
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
