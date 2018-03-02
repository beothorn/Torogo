package beothorn.github.com.toroidalgo.go.impl.logic;

public interface BoardListener {

	void updateScore(int _blackScore, float _whiteScore); //White can have 6.5 Komi, for example.
	void nextToPlay(StoneColor _nextToPlay);
	void nextToPlayOnPass(StoneColor _nextToPlay);

}
