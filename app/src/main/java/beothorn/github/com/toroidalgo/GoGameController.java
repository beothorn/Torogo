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

    public GoGameController(){
        goBoard = new ToroidalGoBoard(size);
        goBoard.setBoardListener(this);
    }

    public void play(GoBoard.StoneColor player, int line, int column){
        if(goBoard.canPlayStone(column, line))
            goBoard.playStone(column, line);
    }

    public GoBoard.StoneColor getPieceAt(int line, int column){
        return goBoard.stoneAt(column, line);
    }

    public int getSize() {
        return size;
    }

    private String nextToPlay(){
        if(goBoard.nextToPlay() == null){
            return "game ended";
        }
        return goBoard.nextToPlay().toString();
    }

    public void setStateLabel(TextView stateLabel) {
        this.stateLabel = stateLabel;
    }

    public void setPassButton(Button passButton) {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBoard.passTurn();
                stateLabel.setText(nextToPlay());
            }
        });
    }

    @Override
    public void updateScore(int _blackScore, int _whiteScore) {
        blackScore.setText("black "+_blackScore);
        whiteScore.setText("white "+_whiteScore);
    }

    @Override
    public void nextToPlay(GoBoard.StoneColor _nextToPlay) {
        if(_nextToPlay == null){
            stateLabel.setText("Game ended");
        }else{
            stateLabel.setText(_nextToPlay.toString());
        }
    }

    public void setBlackScore(TextView blackScore) {
        this.blackScore = blackScore;
    }

    public void setWhiteScore(TextView whiteScore) {
        this.whiteScore = whiteScore;
    }
}
