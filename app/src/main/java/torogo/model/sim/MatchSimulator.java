package torogo.model.sim;

import android.content.Context;
import android.widget.Toast;

import java.util.Arrays;

import torogo.model.Match;
import torogo.model.StoneColor;

import static torogo.model.StoneColor.BLACK;
import static torogo.model.StoneColor.WHITE;

public class MatchSimulator implements Match {
	private final Context context;

	public MatchSimulator(Context context) {
		this.context = context;
	}

	@Override
	public void initListener(Runnable onStateChanged) {
		onStateChanged.run();
	}

	@Override
	public boolean isLastPlayedStone(int x, int y) {
		return false;
	}

	@Override
	public boolean isValidMove(int x, int y) {
		return (x + y) % 10 == 0;
	}

	@Override
	public void handle(Action action, Object... args) {
		CharSequence text = action.name() + " " + Arrays.toString(args);
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean isToroidal() {
		return true;
	}

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int boardSize() {
		return 0;
	}

	@Override
	public StoneColor stoneAt(int x, int y) {
		if (x == 0) return BLACK;
		if (x == 1) return WHITE;
		return null;
	}

	@Override
	public int blackScore() {
		return 0;
	}

	@Override
	public float whiteScore() {
		return 0;
	}

	@Override
	public boolean hasEnded() {
		return false;
	}

	@Override
	public String[] printOut() {
		return null;
	}
}
