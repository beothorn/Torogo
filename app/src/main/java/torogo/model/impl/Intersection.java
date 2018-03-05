package torogo.model.impl;


import java.util.HashSet;
import java.util.Set;

import beothorn.github.com.toroidalgo.go.impl.logic.BoardPosition;
import beothorn.github.com.toroidalgo.go.impl.logic.IllegalMove;
import torogo.model.StoneColor;

public class Intersection {

	private Intersection left;
	private Intersection right;
	private Intersection up;
	private Intersection down;

    private BoardPosition position;

	StoneColor stone = null;

    public Intersection(int column, int line){
        position = new BoardPosition(column, line);
    }

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
		Intersection intersection = new Intersection(position.getColumn(), position.getLine());
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

   void getLinkedStonesOfSameColor(StoneColor stoneColor, Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);

        boolean notAnotherStoneOfSameColor = stone != stoneColor && stone != null;
        if(notAnotherStoneOfSameColor) return;

		if (up != null) up.getLinkedStonesOfSameColor(stoneColor, group);
		if (down != null) down.getLinkedStonesOfSameColor(stoneColor, group);
		if (left != null) left.getLinkedStonesOfSameColor(stoneColor, group);
		if (right != null) right.getLinkedStonesOfSameColor(stoneColor, group);
	}
	
	
	Set<Intersection> getGroupWithNeighbours() {
		Set<Intersection> result = new HashSet<Intersection>();
		getLinkedStonesOfSameColor(stone, result);
		return result;
	}

	
	boolean killGroupIfSurrounded(StoneColor color) {
		if (stone != color) return false;
		
		Set<Intersection> groupWithNeighbours = getGroupWithNeighbours();
		
		for (Intersection intersection : groupWithNeighbours)
			if (intersection.isLiberty()) return false;

		for (Intersection intersection : groupWithNeighbours)
			if (intersection.stone == color) intersection.stone = null;
		
		return true;
	}

	
	boolean isLiberty() {
		return stone == null;
	}

	public BoardPosition getBoardPosition() {
        return position;
    }
}