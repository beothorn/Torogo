package torogo.model.impl;

import java.util.HashSet;
import java.util.Set;

import torogo.model.StoneColor;

import static torogo.model.StoneColor.BLACK;
import static torogo.model.StoneColor.WHITE;

public class Board {

	private Intersection[][] intersections;

	Board(boolean isToroidal, int size) {
		intersections = createIntersections(size);
		if (isToroidal) makeToroidal();
	}

	StoneColor stoneAt(int x, int y) {
		return intersections[x][y].stone;
	}

	private Intersection[][] createIntersections(int size) {
		Intersection[][] intersections = new Intersection[size][size];
		for (int column = 0; column < size; column++) {
			for (int line = 0; line < size; line++) {
				Intersection newOne = new Intersection();
				intersections[column][line] = newOne;
			}
		}

		connectInternally(intersections);
		return intersections;
	}

	private void connectInternally(Intersection[][] intersections) {
		int size = intersections.length;
		for (int column = 0; column < size; column++) {
			for (int line = 0; line < size; line++) {
				if (column != 0) intersections[column][line].connectToYourLeft(intersections[column - 1][line]);
				if (line != 0) intersections[column][line].connectUp(intersections[column][line - 1]);
			}
		}
	}

	void setStone(int x, int y, StoneColor color) {
		intersections[x][y].setStone(color);
	}

	boolean killSurroundedStones(StoneColor color) {
		boolean wereStonesKilled = false;
		for(Intersection[] column : intersections)
			for(Intersection intersection : column)
				if (killSurroundedStones(intersection, color))
					wereStonesKilled = true;

		return wereStonesKilled;
	}

	private boolean killSurroundedStones(Intersection start, StoneColor color) {
		if (start.stone != color) return false;

		Set<Intersection> groupWithNeighbours = new HashSet<>();
		accumulateGroupWithNeighbours(color, start, groupWithNeighbours);

		for (Intersection intersection : groupWithNeighbours)
			if (intersection.isLiberty()) return false;

		for (Intersection intersection : groupWithNeighbours)
			if (intersection.stone == color) intersection.stone = null;

		return true;
	}

	private void accumulateGroupWithNeighbours(StoneColor stoneColor, Intersection intersection, Set<Intersection> group) {
		if (intersection == null || group.contains(intersection)) return;
		group.add(intersection);

		boolean sameColor = intersection.stone == stoneColor;
		if (!sameColor) return;

		accumulateGroupWithNeighbours(stoneColor, intersection.up,    group);
		accumulateGroupWithNeighbours(stoneColor, intersection.right, group);
		accumulateGroupWithNeighbours(stoneColor, intersection.down,  group);
		accumulateGroupWithNeighbours(stoneColor, intersection.left,  group);
	}


	public void setup(String[] setup) {
		for (int y = 0; y < setup.length; y++)
			setupLine(y, setup[y]);
	}

	public void setupLine(int y, String line) {
		int x = 0;
		for(char symbol : line.toCharArray()) {
			if (symbol == ' ') continue;

			StoneColor stone = null;
			if (symbol == 'w') stone = WHITE;
			if (symbol == 'b') stone = BLACK;

			intersections[x][y].stone = stone;
			x++;
		}
	}

	public String printOut() {
		StringBuffer result= new StringBuffer();
		for (int y = 0; y < intersections.length; y++) {
			for (int x = 0; x < intersections[y].length; x++) {
				StoneColor stone = intersections[x][y].stone;
				if(stone == WHITE)
					result.append(" w");
				else if(stone == BLACK)
					result.append(" b");
				else
					result.append(" +");
			}
			result.append("\n");
		}
		return result.toString();
	}

	private void makeToroidal() {
		connectTopToBottom();
		connectLeftToRight();
	}

	private void connectTopToBottom() {
		for (int x = 0; x < intersections.length; x++) {
			Intersection top = intersections[x][0];
			Intersection bottom = intersections[x][intersections.length - 1];
			top.connectUp(bottom);
		}
	}

	private void connectLeftToRight() {
		for (int y = 0; y < intersections.length; y++) {
			Intersection left = intersections[0][y];
			Intersection right = intersections[intersections.length - 1][y];
			left.connectToYourLeft(right);
		}
	}

	public StoneColor[][] state() {
		return null;
	}
}
