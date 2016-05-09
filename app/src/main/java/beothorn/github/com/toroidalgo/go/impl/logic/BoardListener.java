package beothorn.github.com.toroidalgo.go.impl.logic;

public interface BoardListener {

	void updateScore(int _blackScore, float _whiteScore);
	void nextToPlay(GoMatch.StoneColor _nextToPlay);
	void nextToPlayOnPass(GoMatch.StoneColor _nextToPlay);

}
