package beothorn.github.com.toroidalgo.go.impl.logic;

public interface BoardListener {

	void updateScore(int _blackScore, int _whiteScore);
	void nextToPlay(GoBoard.StoneColor _nextToPlay);
	void nextToPlayOnPass(GoBoard.StoneColor _nextToPlay);

}
