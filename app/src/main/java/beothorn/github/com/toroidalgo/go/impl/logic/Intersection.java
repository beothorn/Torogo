package beothorn.github.com.toroidalgo.go.impl.logic;


import java.util.HashSet;
import java.util.Set;


public class Intersection {

	private Intersection left;
	private Intersection right;
	private Intersection up;
	private Intersection down;

    private BoardPosition position;

	GoMatch.StoneColor stone = null;

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
	
	public void setStone(GoMatch.StoneColor stoneColor) throws IllegalMove {
		if (!isLiberty()) throw new IllegalMove();
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

    public Set<Intersection> getLinkedEmptyOrDeadTerritories() {
        HashSet<Intersection> intersections = new HashSet<Intersection>();
        internalGetLinkedEmptyOrDeadTerritories(intersections);
        return intersections;
    }

	private void internalGetLinkedEmptyOrDeadTerritories(Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);

        boolean notAFreePosition = stone != null && stone != GoMatch.StoneColor.WHITEDEAD && stone != GoMatch.StoneColor.BLACKDEAD;
        if(notAFreePosition) return;

		if (up != null) up.internalGetLinkedEmptyOrDeadTerritories(group);
		if (down != null) down.internalGetLinkedEmptyOrDeadTerritories(group);
		if (left != null) left.internalGetLinkedEmptyOrDeadTerritories(group);
		if (right != null) right.internalGetLinkedEmptyOrDeadTerritories(group);
	}

	void getLinkedStonesOfSameColor(GoMatch.StoneColor stoneColor, Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);

        boolean notAnotherStoneOfSameColor = stone != stoneColor && stone != null;
        if(notAnotherStoneOfSameColor) return;

		if (up != null) up.getLinkedStonesOfSameColor(stoneColor, group);
		if (down != null) down.getLinkedStonesOfSameColor(stoneColor, group);
		if (left != null) left.getLinkedStonesOfSameColor(stoneColor, group);
		if (right != null) right.getLinkedStonesOfSameColor(stoneColor, group);
	}
	
	
	void markDeadStones() {
		GoMatch.StoneColor colorToKill = stone;
		boolean killed;
		
		do {
			killed = false;
			Set<Intersection> group = getGroupWithNeighbours();
			for (Intersection intersection : group)
				if (intersection.stone == colorToKill) {
					if(colorToKill.equals(GoMatch.StoneColor.BLACK))
						intersection.stone = GoMatch.StoneColor.BLACKDEAD;
					else
						intersection.stone = GoMatch.StoneColor.WHITEDEAD;
					killed = true;
				}
		} while (killed);
	}

	
	Set<Intersection> getGroupWithNeighbours() {
		Set<Intersection> result = new HashSet<Intersection>();
		getLinkedStonesOfSameColor(stone, result);
		return result;
	}

	
	boolean killGroupIfSurrounded(GoMatch.StoneColor color) {
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

	public boolean isDead() {
		return stone == GoMatch.StoneColor.BLACKDEAD || stone == GoMatch.StoneColor.WHITEDEAD;
	}

    public BoardPosition getBoardPosition() {
        return position;
    }
}