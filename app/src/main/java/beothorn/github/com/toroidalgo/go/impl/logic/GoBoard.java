package beothorn.github.com.toroidalgo.go.impl.logic;

import android.graphics.Point;

import java.util.HashSet;
import java.util.Set;

import beothorn.github.com.toroidalgo.Publisher;


public class GoBoard {

	public Point getLastPlayedPiece() {
		return lastPlayedPiece;
	}

	public enum StoneColor { BLACK, WHITE, BLACKDEAD, WHITEDEAD, ANY;}

	public GoBoard(int size) {
		setup(size);
	}

	private void setup(int size) {
		intersections = createIntersections(size);
		previousSituation = createIntersections(size);
	}

	public GoBoard(String[] setup) {
		this(setup.length);
		IntersectionUtils.setup(intersections,setup);
	}

	public void loadGame(String gameState) {
		String[] playingColorAndBoardState = gameState.split("\\|");
		String lastPlayed = playingColorAndBoardState[0];
		lastPlayedPiece = new Point();
		lastPlayedPiece.set(Integer.valueOf(lastPlayed.split(",")[0]), Integer.valueOf(lastPlayed.split(",")[1]));
		String playingColor = playingColorAndBoardState[1];
		if(!playingColor.equals("null"))
			nextToPlay = StoneColor.valueOf(playingColor);
		String[] setup = playingColorAndBoardState[2].split("\n");
		setup(setup.length);
		IntersectionUtils.setup(intersections,setup);
	}

	private StoneColor nextToPlay = StoneColor.BLACK;


    private int whiteScore = 0;
    private StoneColor winner = null;
	protected Intersection[][] intersections;


    private Intersection[][] previousSituation;
    private boolean previousWasPass = false;
    private int capturedStonesBlack;
    private int capturedStonesWhite;
    private BoardListener boardListener;
    private Point lastPlayedPiece;
	protected Intersection intersection(int x, int y) {
		return intersections[x][y];
	}


	public String printOut(){
		return IntersectionUtils.print(intersections);
	}

	public boolean canPlayStone(int x, int y) {
		if (nextToPlay() == null) return false;

		Intersection[][] situation = copy(intersections);
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
		Intersection[][] situationFound = copy(intersections);

		try {
			tryToPlayStone(x, y);
		} catch (IllegalMove e) {
			throw new IllegalArgumentException(e);
		}
		lastPlayedPiece = new Point(x, y);
		previousWasPass = false;
		previousSituation = situationFound;
		countDeadStones();
		next();
	}

	public void toggleDeadStone(int x, int y) {
		if (isDead(intersections[x][y]))
			unmarkDeadStones(x, y);
		else
			intersections[x][y].markDeadStones();

		updateScore();
	}

	private boolean isDead(Intersection intersection) {
		return intersection._stone == null || intersection._stone.equals(StoneColor.BLACKDEAD) || intersection._stone.equals(StoneColor.WHITEDEAD);
	}

	private int _blackScore = 0;

    public boolean gameHasEnded() {
        return nextToPlay() == null;
    }

	public void passTurn() {
		nextPass();

		if (previousWasPass)
			stopAcceptingMoves();

		previousWasPass = true;
	}

    public void continueGame(int turn) {
        returnToAcceptingMoves(turn);
    }

	public void resign() {
		StoneColor loser = nextToPlay();
		winner = other(loser);
		stopAcceptingMoves();
	}

	public void endMarkingStones(){
		stopAcceptingMoves();
		if(_blackScore > whiteScore){
			winner = StoneColor.BLACK;
		}else{
			winner = StoneColor.WHITE;
		}
	}


	public int blackScore() {
		return _blackScore;
	}


	public int whiteScore() {
		return whiteScore;
	}

	
	public StoneColor other(StoneColor color) {
		return (color == StoneColor.BLACK) ? StoneColor.WHITE: StoneColor.BLACK;
	}
	
	
	public StoneColor stoneAt(int x, int y) {
		return intersection(x, y)._stone;
	}
	
	public StoneColor nextToPlay() {
		return nextToPlay;
	}

	public StoneColor winner() {
		return winner;
	}

	public void setBoardListener(BoardListener boardListener) {
		this.boardListener = boardListener;
	}


	private void unmarkDeadStones(int x, int y) {
		Set<Intersection> group = intersections[x][y].getGroupWithNeighbours();
		for (Intersection intersection : group)
			if (isDead(intersection))
				intersection._stone = previousEquivalent(intersection)._stone;
	}


	private Intersection previousEquivalent(Intersection intersection) {
		int size = intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if ( intersections[x][y] == intersection )
					return previousSituation[x][y];
			}
		}
		throw new IllegalStateException("Intersection " + intersection + " not found.");
	}

	private boolean killSurroundedStones(StoneColor color) {
		boolean wereStonesKilled = false;
		for(Intersection[] column : intersections)
			for(Intersection intersection : column)
				if (intersection.killGroupIfSurrounded(color))
					wereStonesKilled = true;
		
		return wereStonesKilled;
	}
	
	private HashSet<Intersection> allIntersections() {
		HashSet<Intersection> set = new HashSet<Intersection>();
		
		for (Intersection[] column: intersections)
			for(Intersection inter : column)
				set.add(inter);
		
		return set;
	}
	
	private void tryToPlayStone(int x, int y) throws IllegalMove{
		intersection(x, y).setStone(nextToPlay());
		
		killSurroundedStones(other(nextToPlay()));
		if (killSurroundedStones(nextToPlay()))
			throw new IllegalMove();
		
		if(IntersectionUtils.sameSituation(previousSituation, intersections))
			throw new IllegalMove();
	}


    private void stopAcceptingMoves() {
        previousSituation = copy(intersections);
        nextToPlay = null;
        if(boardListener != null){
            boardListener.nextToPlay(nextToPlay);
        }

        capturedStonesBlack = _blackScore;
        capturedStonesWhite = whiteScore;

        updateScore();
    }

    private void returnToAcceptingMoves(int turn) {
        previousSituation = copy(intersections);
        nextToPlay = turn == Publisher.BLACK_TURN ? StoneColor.BLACK : StoneColor.WHITE;
        if(boardListener != null){
            boardListener.nextToPlay(nextToPlay);
        }

        capturedStonesBlack = 0;
        capturedStonesWhite = 0;

        updateScore();
    }


    private void updateScore() {
		_blackScore = capturedStonesBlack;
		whiteScore = capturedStonesWhite;
		countDeadStones();
		countTerritories();
		if(boardListener != null){
			boardListener.updateScore(_blackScore, whiteScore);
		}
	}

	
	private void countDeadStones() {
		int size = intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (!intersections[x][y].isLiberty())
					continue;
				StoneColor previousStone = previousSituation[x][y]._stone;
				if (previousStone == StoneColor.BLACK){
					whiteScore++;
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
				whiteScore += numEmpty;
			}
		}
	}

	private void nextPass() {
		nextToPlay = other(nextToPlay());
		if(boardListener !=null){
			boardListener.nextToPlayOnPass(nextToPlay);
		}
	}

	private void next() {
		nextToPlay = other(nextToPlay());
		if(boardListener !=null){
			boardListener.nextToPlay(nextToPlay);
		}
	}
	
	private void restoreSituation(Intersection[][] situation) {
		intersections = situation;
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
		if(lastPlayedPiece == null) return false;
		return x == lastPlayedPiece.x && y == lastPlayedPiece.y;
	}
	
}
