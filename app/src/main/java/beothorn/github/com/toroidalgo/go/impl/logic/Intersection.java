package beothorn.github.com.toroidalgo.go.impl.logic;


import java.util.HashSet;
import java.util.Set;


public class Intersection {

	private Intersection _left;
	private Intersection _right;
	private Intersection _up;
	private Intersection _down;
	
	GoBoard.StoneColor _stone = null;

	
	@Override
	public boolean equals(Object obj) {
		final Intersection other = (Intersection) obj;
		if (_stone == null) 
			return (other._stone == null);
		return _stone.equals(other._stone);
	}
	
	public void setStone(GoBoard.StoneColor stoneColor) throws IllegalMove {
		if (!isLiberty()) throw new IllegalMove();
		_stone = stoneColor;
	}

	protected Intersection copy(){
		Intersection intersection = new Intersection();
		intersection._stone = _stone;
		return intersection;
	}
	
	void connectToYourLeft(Intersection other) {
		_left = other;
		other._right = this;
	}

	
	void connectUp(Intersection other) {
		_up = other;
		other._down = this;
	}

	
	void fillGroupWithNeighbours(GoBoard.StoneColor stoneColor, Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);
		
		if(_stone != stoneColor) return;
		
		if (_up != null) _up.fillGroupWithNeighbours(stoneColor, group);
		if (_down != null) _down.fillGroupWithNeighbours(stoneColor, group);
		if (_left != null) _left.fillGroupWithNeighbours(stoneColor, group);
		if (_right != null) _right.fillGroupWithNeighbours(stoneColor, group);
	}
	
	
	void markDeadStones() {
		GoBoard.StoneColor colorToKill = _stone;
		boolean killed;
		
		do {
			killed = false;
			Set<Intersection> group = getGroupWithNeighbours();
			for (Intersection intersection : group)
				if (intersection._stone == colorToKill) {
					if(colorToKill.equals(GoBoard.StoneColor.BLACK))
						intersection._stone = GoBoard.StoneColor.BLACKDEAD;
					else
						intersection._stone = GoBoard.StoneColor.WHITEDEAD;
					killed = true;
				}
		} while (killed);
	}

	
	Set<Intersection> getGroupWithNeighbours() {
		Set<Intersection> result = new HashSet<Intersection>();
		fillGroupWithNeighbours(_stone, result);
		return result;
	}

	
	boolean killGroupIfSurrounded(GoBoard.StoneColor color) {
		if (_stone != color) return false;
		
		Set<Intersection> groupWithNeighbours = getGroupWithNeighbours();
		
		for (Intersection intersection : groupWithNeighbours)
			if (intersection.isLiberty()) return false;

		for (Intersection intersection : groupWithNeighbours)
			if (intersection._stone == color) intersection._stone = null;
		
		return true;
	}

	
	boolean isLiberty() {
		return _stone == null;
	}

}