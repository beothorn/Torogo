package beothorn.github.com.toroidalgo;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoBoard;

public class GoGameController {

    private GoBoard goBoard;
    private int size = 9;

    public GoGameController(){
        goBoard = new ToroidalGoBoard(size);
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
}
