package torogo.model.impl;

import torogo.model.StoneColor;

public class Board {

	private Intersection[][] intersections;

	Board(int size) {
		intersections = createIntersections(size);
	}

	StoneColor stoneAt(int x, int y) {
		return intersections[x][y].stone;
	}

	private Intersection[][] createIntersections(int size) {
		Intersection[][] intersections = new Intersection[size][size];
		for (int column = 0; column < size; column++) {
			for (int line = 0; line < size; line++) {
				Intersection newOne = new Intersection(column, line);
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

	void killSurrounded(StoneColor color) {

	}
}
