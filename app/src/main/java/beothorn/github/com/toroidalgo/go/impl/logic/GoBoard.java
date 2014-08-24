package beothorn.github.com.toroidalgo.go.impl.logic;

import android.graphics.Point;

import java.util.HashSet;
import java.util.Set;


public class GoBoard {

	public static enum StoneColor { BLACK, WHITE;}
	
	public GoBoard(int size) {
		_intersections = createIntersections(size);
		_previousSituation = createIntersections(size);
	}

	public GoBoard(String[] setup) {
		this(setup.length);
		IntersectionUtils.setup(_intersections,setup);
	}

	
	private StoneColor _nextToPlay = StoneColor.BLACK;
	private int _blackScore = 0;
	private int _whiteScore = 0;
	private StoneColor _winner = null;
	
	
	protected Intersection[][] _intersections;
	private Intersection[][] _previousSituation;
	private boolean _previousWasPass = false;
	private int _capturedStonesBlack;
	private int _capturedStonesWhite;
	private BoardListener _boardListener;
	private Point _lastPlayedPiece;
	
		
	protected Intersection intersection(int x, int y) {
		return _intersections[x][y];
	}
	
	
	public String printOut(){
		return IntersectionUtils.print(_intersections);
	}

	public int size() {
		return _intersections.length;
	}
	
	
	public boolean canPlayStone(int x, int y) {
		if (nextToPlay() == null) return false;
		
		Intersection[][] situation = copy(_intersections);
		try {
			tryToPlayStone(x, y);
		} catch (IllegalMove im) {
			return false;
		} finally {
			restoreSituation(situation);
		}
		
		return true;
	}
	
	
	public void playStone(int x, int y) {
		Intersection[][] situationFound = copy(_intersections);
		
		try {
			tryToPlayStone(x, y);
		} catch (IllegalMove e) {
			throw new IllegalArgumentException(e);
		}
		_lastPlayedPiece = new Point(x, y);
		_previousWasPass = false;
		_previousSituation = situationFound;
		countDeadStones();
		next();
	}
	
	
	public void toggleDeadStone(int x, int y) {
		if (_intersections[x][y]._stone == null)
			unmarkDeadStones(x, y);
		else
			_intersections[x][y].markDeadStones();

		updateScore();
	}


	public void passTurn() {
		next();
		
		if (_previousWasPass)
			stopAcceptingMoves();
		
		_previousWasPass = true;
	}
	
	
	public void resign() {
		StoneColor loser = nextToPlay();
		_winner = other(loser);
		stopAcceptingMoves();
	}
	
	
	public int blackScore() {
		return _blackScore;
	}
	
	
	public int whiteScore() {
		return _whiteScore;
	}
	
	
	public StoneColor other(StoneColor color) {
		return (color == StoneColor.BLACK) ? StoneColor.WHITE: StoneColor.BLACK;
	}
	
	
	public StoneColor stoneAt(int x, int y) {
		return intersection(x, y)._stone;
	}
	
	public StoneColor nextToPlay() {
		return _nextToPlay;
	}
	
	public StoneColor getPrevColor(int x, int y) {
		return _previousSituation[x][y]._stone;
	}
	
	
	public StoneColor winner() {
		return _winner; 
	}


	public void setBoardListener(BoardListener boardListener) {
		_boardListener = boardListener;
	}


	private void unmarkDeadStones(int x, int y) {
		Set<Intersection> group = _intersections[x][y].getGroupWithNeighbours();
		for (Intersection intersection : group)
			if (intersection._stone == null)
				intersection._stone = previousEquivalent(intersection)._stone;
	}


	private Intersection previousEquivalent(Intersection intersection) {
		int size = _intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if ( _intersections[x][y] == intersection )
					return _previousSituation[x][y];
			}
		}
		throw new IllegalStateException("Intersection " + intersection + " not found.");
	}

	private boolean killSurroundedStones(StoneColor color) {
		boolean wereStonesKilled = false;
		for(Intersection[] column : _intersections)
			for(Intersection intersection : column)
				if (intersection.killGroupIfSurrounded(color))
					wereStonesKilled = true;
		
		return wereStonesKilled;
	}
	
	private HashSet<Intersection> allIntersections() {
		HashSet<Intersection> set = new HashSet<Intersection>();
		
		for (Intersection[] column: _intersections )
			for(Intersection inter : column)
				set.add(inter);
		
		return set;
	}
	
	private void tryToPlayStone(int x, int y) throws IllegalMove{
		intersection(x, y).setStone(nextToPlay());
		
		killSurroundedStones(other(nextToPlay()));
		if (killSurroundedStones(nextToPlay()))
			throw new IllegalMove();
		
		if(IntersectionUtils.sameSituation(_previousSituation, _intersections))
			throw new IllegalMove();
	}
	
	
	private void stopAcceptingMoves() {
		_previousSituation = copy(_intersections);
		_nextToPlay = null;
		if(_boardListener != null){
			_boardListener.nextToPlay(_nextToPlay);
		}
		
		_capturedStonesBlack = _blackScore;
		_capturedStonesWhite = _whiteScore;
		
		updateScore();
	}
	
	
	private void updateScore() {
		_blackScore = _capturedStonesBlack;
		_whiteScore = _capturedStonesWhite;
		countDeadStones();
		countTerritories();
		if(_boardListener != null){
			_boardListener.updateScore(_blackScore,_whiteScore);
		}
	}

	
	private void countDeadStones() {
		int size = _intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (!_intersections[x][y].isLiberty())
					continue;
				StoneColor previousStone = _previousSituation[x][y]._stone;
				if (previousStone == StoneColor.BLACK){
					_whiteScore++;
				}
				if (previousStone == StoneColor.WHITE){
					_blackScore++;
				}
			}
		}
	}
	
	private void countTerritories() {
	
		HashSet<Intersection> pending = allIntersections();
		
		while (!pending.isEmpty()) {
			Intersection starting = pending.iterator().next();
			
			HashSet<Intersection> group = new HashSet<Intersection>();
			starting.fillGroupWithNeighbours(null, group);
			
			boolean belongsToW=false, belongsToB=false;
			int numEmpty=0;
			for (Intersection groupee : group) {
				pending.remove(groupee);
				if (groupee._stone == StoneColor.BLACK) belongsToB = true;
				if (groupee._stone == StoneColor.WHITE) belongsToW = true;
				if (groupee.isLiberty()) numEmpty++;
			}
			if (belongsToB & !belongsToW){
				_blackScore += numEmpty;
			}
			if (!belongsToB & belongsToW){
				_whiteScore += numEmpty;
			}
		}
	}

	
	private void next() {
		_nextToPlay = other(nextToPlay());
		if(_boardListener!=null){
			_boardListener.nextToPlay(_nextToPlay);
		}
	}
	
	private void restoreSituation(Intersection[][] situation) {
		_intersections = situation;
	}
	
	protected Intersection[][] copy(Intersection[][] intersections) {
		if(intersections.length == 0)
			return new Intersection[0][0];
		Intersection[][] copy =  new Intersection[intersections.length][intersections[0].length];
		
		for (int x = 0; x < intersections.length; x++) {
			for (int y = 0; y < intersections[x].length; y++) {
				copy[x][y] = intersections[x][y].copy();
			}
		}
		
		connectInternally(copy);
		
		return copy;
	}
	
	private Intersection[][] createIntersections(int size) {
		Intersection[][] intersections = new Intersection[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Intersection newOne = new Intersection();
				intersections[x][y] = newOne;
			}
		}
		
		connectInternally(intersections);
		return intersections;
	}

	private void connectInternally(Intersection[][] intersections) {
		int size = intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (x != 0) intersections[x][y].connectToYourLeft(intersections[x - 1][y]);
				if (y != 0) intersections[x][y].connectUp(intersections[x][y - 1]);
			}
		}
	}

	public boolean stoneAtPositionIsLastPlayedStone(int x, int y) {
		if(_lastPlayedPiece == null) return false;
		return x == _lastPlayedPiece.x && y == _lastPlayedPiece.y; 
	}
	
}
