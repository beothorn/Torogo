package beothorn.github.com.toroidalgo.go.impl.logic;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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

    public void loadGame(int lastPlayedPieceX, int lastPlayedPieceY, StoneColor playingColor, String boardSetup) {
        lastPlayedPiece = new Point(lastPlayedPieceX, lastPlayedPieceY);
        nextToPlay = playingColor;
        String[] setup = boardSetup.split("\n");
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
		return intersection.stone == null || intersection.stone.equals(StoneColor.BLACKDEAD) || intersection.stone.equals(StoneColor.WHITEDEAD);
	}

	private int blackScore = 0;

    public boolean gameHasEnded() {
        return nextToPlay() == null;
    }

	public void passTurn() {
		nextPass();

		if (previousWasPass)
			stopAcceptingMoves();

		previousWasPass = true;
	}

	public void resign() {
		StoneColor loser = nextToPlay();
		winner = other(loser);
		stopAcceptingMoves();
	}

	public void endMarkingStones(){
		stopAcceptingMoves();
		if(blackScore > whiteScore){
			winner = StoneColor.BLACK;
		}else{
			winner = StoneColor.WHITE;
		}
	}


	public int blackScore() {
		return blackScore;
	}


	public int whiteScore() {
		return whiteScore;
	}

	
	public StoneColor other(StoneColor color) {
		return (color == StoneColor.BLACK) ? StoneColor.WHITE: StoneColor.BLACK;
	}
	
	
	public StoneColor stoneAt(int x, int y) {
		return intersection(x, y).stone;
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
				intersection.stone = previousEquivalent(intersection).stone;
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
	
	private HashSet<Intersection> copyIntersections() {
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

        capturedStonesBlack = blackScore;
        capturedStonesWhite = whiteScore;

        updateScore();
    }

    private void updateScore() {
		blackScore = capturedStonesBlack;
		whiteScore = capturedStonesWhite;
		countDeadStones();
		countTerritories();
		if(boardListener != null){
			boardListener.updateScore(blackScore, whiteScore);
		}
	}

	
	private void countDeadStones() {
		int size = intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				StoneColor previousStone = previousSituation[x][y].stone;
				if ( (intersections[x][y].stone == null && StoneColor.BLACK.equals(previousStone) ) || StoneColor.BLACKDEAD.equals(intersections[x][y].stone)){
					whiteScore++;
				}
				if ((intersections[x][y].stone == null && StoneColor.WHITE.equals(previousStone) ) || StoneColor.WHITEDEAD.equals(intersections[x][y].stone)){
					blackScore++;
				}
			}
		}
	}


	public Map<StoneColor, List<List<BoardPosition>>> getTerritoriesOwnership(){
        LinkedHashMap<StoneColor, List<List<BoardPosition>>> ownedTerritories = new LinkedHashMap<StoneColor, List<List<BoardPosition>>>();
        List<List<BoardPosition>> blackTerritories = new ArrayList<List<BoardPosition>>();
        List<List<BoardPosition>> whiteTerritories = new ArrayList<List<BoardPosition>>();

        ownedTerritories.put(StoneColor.BLACK, blackTerritories);
        ownedTerritories.put(StoneColor.WHITE, whiteTerritories);

        HashSet<Intersection> pending = copyIntersections();

        while (!pending.isEmpty()) {
            Intersection startingStone = pending.iterator().next();

            Set<Intersection> stonePlusAllEmptiesAround = startingStone.getLinkedEmptyOrDeadTerritories();

            boolean whiteClaimsGroupOwnership=false, blackClaimsGroupOwnership=false;
            ArrayList<BoardPosition> boardPositions = new ArrayList<BoardPosition>();
            for (Intersection intersection : stonePlusAllEmptiesAround) {
                pending.remove(intersection);

                if (intersection.stone == StoneColor.BLACK) blackClaimsGroupOwnership = true;
                if (intersection.stone == StoneColor.WHITE) whiteClaimsGroupOwnership = true;
                if (intersection.isLiberty() || intersection.isDead()) boardPositions.add(intersection.getBoardPosition());;
            }
            if (blackClaimsGroupOwnership & !whiteClaimsGroupOwnership){
                blackTerritories.add(boardPositions);
            }
            if (!blackClaimsGroupOwnership & whiteClaimsGroupOwnership){
                whiteTerritories.add(boardPositions);
            }
        }

        return ownedTerritories;
    }

	private void countTerritories() {

        Map<StoneColor, List<List<BoardPosition>>> territoriesOwnership = getTerritoriesOwnership();

        List<List<BoardPosition>> blackTerritories = territoriesOwnership.get(StoneColor.BLACK);
        for(List<BoardPosition> blackTerritory : blackTerritories){
            blackScore += blackTerritory.size();
        }

        List<List<BoardPosition>> whiteTerritories = territoriesOwnership.get(StoneColor.WHITE);
        for(List<BoardPosition> whiteTerritory : whiteTerritories){
            whiteScore += whiteTerritory.size();
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
		
		for (int column = 0; column < intersections.length; column++) {
			for (int line = 0; line < intersections[column].length; line++) {
				copy[column][line] = intersections[column][line].copy();
			}
		}
		
		connectInternally(copy);
		
		return copy;
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
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (x != 0) intersections[x][y].connectToYourLeft(intersections[x - 1][y]);
				if (y != 0) intersections[x][y].connectUp(intersections[x][y - 1]);
			}
		}
	}

	public boolean stoneAtPositionIsLastPlayedStone(int x, int y) {
		if(lastPlayedPiece == null) return false;
        if(lastPlayedPiece.x < 0) return false;
		return x == lastPlayedPiece.x && y == lastPlayedPiece.y;
	}
	
}
