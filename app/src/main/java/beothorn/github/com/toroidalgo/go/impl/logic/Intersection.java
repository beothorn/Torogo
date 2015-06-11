package beothorn.github.com.toroidalgo.go.impl.logic;


import java.util.HashSet;
import java.util.Set;


public class Intersection {

	private Intersection left;
	private Intersection right;
	private Intersection up;
	private Intersection down;
	
	GoBoard.StoneColor stone = null;

	
	@Override
	public boolean equals(Object obj) {
		final Intersection other = (Intersection) obj;
		if (stone == null)
			return (other.stone == null);
		return stone.equals(other.stone);
	}
	
	public void setStone(GoBoard.StoneColor stoneColor) throws IllegalMove {
		if (!isLiberty()) throw new IllegalMove();
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

	void fillGroupWithNeighbours2(Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);

		if(stone != null && stone != GoBoard.StoneColor.WHITEDEAD && stone != GoBoard.StoneColor.BLACKDEAD) return;

		if (up != null) up.fillGroupWithNeighbours2(group);
		if (down != null) down.fillGroupWithNeighbours2(group);
		if (left != null) left.fillGroupWithNeighbours2(group);
		if (right != null) right.fillGroupWithNeighbours2(group);
	}

	void fillGroupWithNeighbours(GoBoard.StoneColor stoneColor, Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);
		
		if(stone != stoneColor && stone != null) return;

		if (up != null) up.fillGroupWithNeighbours(stoneColor, group);
		if (down != null) down.fillGroupWithNeighbours(stoneColor, group);
		if (left != null) left.fillGroupWithNeighbours(stoneColor, group);
		if (right != null) right.fillGroupWithNeighbours(stoneColor, group);
	}
	
	
	void markDeadStones() {
		GoBoard.StoneColor colorToKill = stone;
		boolean killed;
		
		do {
			killed = false;
			Set<Intersection> group = getGroupWithNeighbours();
			for (Intersection intersection : group)
				if (intersection.stone == colorToKill) {
					if(colorToKill.equals(GoBoard.StoneColor.BLACK))
						intersection.stone = GoBoard.StoneColor.BLACKDEAD;
					else
						intersection.stone = GoBoard.StoneColor.WHITEDEAD;
					killed = true;
				}
		} while (killed);
	}

	
	Set<Intersection> getGroupWithNeighbours() {
		Set<Intersection> result = new HashSet<Intersection>();
		fillGroupWithNeighbours(stone, result);
		return result;
	}

	
	boolean killGroupIfSurrounded(GoBoard.StoneColor color) {
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
		return stone == GoBoard.StoneColor.BLACKDEAD || stone == GoBoard.StoneColor.WHITEDEAD;
	}
}