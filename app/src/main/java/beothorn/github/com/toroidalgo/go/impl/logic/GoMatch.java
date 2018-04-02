package beothorn.github.com.toroidalgo.go.impl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.WHITE;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.WHITEDEAD;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.BLACK;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.BLACKDEAD;

public class GoMatch {

    private static final float KOMI = 6.5f;

    public GoMatch(int size) {
        setup(size);
    }

    private void setup(int size) {
        intersections = createIntersections(size);
        previousSituation = createIntersections(size);
    }

    public GoMatch(String[] setup) {
        this(setup.length);
        IntersectionUtils.setup(intersections,setup);
    }

    public void loadGame(int lastPlayedPieceX, int lastPlayedPieceY, StoneColor playingColor, String boardSetup) {
        lastPlayedPiece = new BoardPosition(lastPlayedPieceX, lastPlayedPieceY);
        nextToPlay = playingColor;
        String[] setup = boardSetup.split("\n");
        setup(setup.length);
        IntersectionUtils.setup(intersections,setup);
    }

    private StoneColor nextToPlay = BLACK;

    private float whiteScore = 0;

    private StoneColor winner = null;
    protected Intersection[][] intersections;
    private Intersection[][] previousSituation;


    private boolean previousWasPass = false;
    private int capturedStonesBlack;
    private float capturedStonesWhite;
    private BoardListener boardListener;
    private BoardPosition lastPlayedPiece;
    protected Intersection intersection(int x, int y) {
        return intersections[x][y];
    }

    public BoardPosition getLastPlayedPiece() {
        return lastPlayedPiece;
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
        lastPlayedPiece = new BoardPosition(x, y);
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
        return intersection.stone == null || intersection.stone.equals(BLACKDEAD) || intersection.stone.equals(WHITEDEAD);
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
        winner = blackScore > whiteScore ? BLACK : WHITE;
    }


    public int blackScore() {
        return blackScore;
    }
    public float whiteScore() {
        return whiteScore + KOMI;
    }


    public StoneColor other(StoneColor color) {
        return (color == BLACK) ? WHITE : BLACK;
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

    private HashSet<Intersection> allIntersections() {
        HashSet<Intersection> ret = new HashSet<>();

        for (Intersection[] column: intersections)
            Collections.addAll(ret, column);

        return ret;
    }

    private void tryToPlayStone(int x, int y) throws IllegalMove{
        intersection(x, y).setStone(nextToPlay());

        killSurroundedStones(other(nextToPlay()));

        //Suicide
        if (killSurroundedStones(nextToPlay()))
            throw new IllegalMove();

        //Ko
        if(IntersectionUtils.sameSituation(previousSituation, intersections))
            throw new IllegalMove();
    }


    private void stopAcceptingMoves() {
        previousSituation = copy(intersections);
        nextToPlay = null;
        notifyNextToPlay();

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
                if ( (intersections[x][y].stone == null && BLACK.equals(previousStone) ) || BLACKDEAD.equals(intersections[x][y].stone)){
                    whiteScore++;
                }
                if ((intersections[x][y].stone == null && WHITE.equals(previousStone) ) || WHITEDEAD.equals(intersections[x][y].stone)){
                    blackScore++;
                }
            }
        }
    }


    public Map<StoneColor, List<List<BoardPosition>>> getTerritoriesOwnership(){
        LinkedHashMap<StoneColor, List<List<BoardPosition>>> ownedTerritories = new LinkedHashMap<>();
        List<List<BoardPosition>> blackTerritories = new ArrayList<>();
        List<List<BoardPosition>> whiteTerritories = new ArrayList<>();

        ownedTerritories.put(BLACK, blackTerritories);
        ownedTerritories.put(WHITE, whiteTerritories);

        HashSet<Intersection> pending = allIntersections();

        while (!pending.isEmpty()) {
            Intersection startingStone = pending.iterator().next();

            Set<Intersection> stonePlusAllEmptiesAround = startingStone.getLinkedEmptyOrDeadTerritories();

            boolean whiteClaimsGroupOwnership=false, blackClaimsGroupOwnership=false;
            ArrayList<BoardPosition> boardPositions = new ArrayList<>();
            for (Intersection intersection : stonePlusAllEmptiesAround) {
                pending.remove(intersection);

                if (intersection.stone == BLACK) blackClaimsGroupOwnership = true;
                if (intersection.stone == WHITE) whiteClaimsGroupOwnership = true;
                if (intersection.isLiberty() || intersection.isDead()) boardPositions.add(intersection.getBoardPosition());
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

        List<List<BoardPosition>> blackTerritories = territoriesOwnership.get(BLACK);
        for(List<BoardPosition> blackTerritory : blackTerritories){
            blackScore += blackTerritory.size();
        }

        List<List<BoardPosition>> whiteTerritories = territoriesOwnership.get(WHITE);
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
        notifyNextToPlay();
    }

    private void notifyNextToPlay() {
        if (boardListener != null)
            boardListener.nextToPlay(nextToPlay);
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
        for (int column = 0; column < size; column++) {
            for (int line = 0; line < size; line++) {
                if (column != 0) intersections[column][line].connectToYourLeft(intersections[column - 1][line]);
                if (line != 0) intersections[column][line].connectUp(intersections[column][line - 1]);
            }
        }
    }

    public boolean stoneAtPositionIsLastPlayedStone(int col, int lin) {
        if(lastPlayedPiece == null) return false;

        //if(lastPlayedPiece.getColumn() < 0) return false;
        if (col < 0) throw new IllegalStateException(); //Remove this line and the one above if the exception is never thrown.


        return col == lastPlayedPiece.getColumn() && lin == lastPlayedPiece.getLine();
    }

}
