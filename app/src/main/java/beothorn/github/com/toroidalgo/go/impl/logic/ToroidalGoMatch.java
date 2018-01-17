package beothorn.github.com.toroidalgo.go.impl.logic;

public class ToroidalGoMatch extends GoMatch {

	public ToroidalGoMatch(int size) {
		super(size);
		makeToroidal(intersections);
		playStone(0,0); //In toroidal go it does not matter where the first stone is played, so it starts off with a stone already played.
	}

	public ToroidalGoMatch(String[] setup) {
		super(setup);
		makeToroidal(intersections);
	}
	
	private void makeToroidal(Intersection[][] intersection) {
		connectTopToBottom(intersection);
		connectLeftToRight(intersection);
	}

	private void connectTopToBottom(Intersection[][] intersection) {
		for (int x = 0; x < intersection.length; x++) {
			Intersection top = intersection[x][0];
			Intersection bottom = intersection[x][intersection.length - 1];
			top.connectUp(bottom);
		}
	}

	private void connectLeftToRight(Intersection[][] intersection) {
		for (int y = 0; y < intersection.length; y++) {
			Intersection left = intersection[0][y];
			Intersection right = intersection[intersection.length - 1][y];
			left.connectToYourLeft(right);
		}
	}
	
	@Override
	protected Intersection[][] copy(Intersection[][] intersections) {
		Intersection[][] copy = super.copy(intersections);
		makeToroidal(copy);
		return copy;
	}

}
