package torogo.model.impl;


import java.util.HashSet;
import java.util.Set;

import beothorn.github.com.toroidalgo.go.impl.logic.BoardPosition;
import beothorn.github.com.toroidalgo.go.impl.logic.IllegalMove;
import torogo.model.StoneColor;

public class Intersection {

	Intersection left;
	Intersection right;
	Intersection up;
	Intersection down;

	StoneColor stone = null;

	@Override
	public boolean equals(Object obj) {
		final Intersection other = (Intersection) obj;
		if (stone == null)
			return (other.stone == null);
		return stone.equals(other.stone);
	}
	
	void setStone(StoneColor stoneColor) {
		if (!isLiberty()) throw new IllegalStateException();
		stone = stoneColor;
	}

	protected Intersection copy(){
		Intersection intersection = new Intersection();
		intersection.stone = stone;
		return intersection;
	}
	
	void connectToYourLeft(Intersection other) {
		left = other;
		other.right = this;
	}

	
	void connectUp(Intersection other) {
		up = other;
		other.down = this;
	}

	boolean isLiberty() {
		return stone == null;
	}
}