package torogo.model.impl;

import torogo.model.Match;
import torogo.model.StoneColor;

import static torogo.model.StoneColor.BLACK;
import static torogo.model.StoneColor.WHITE;

public class MatchImpl implements Match {

    private static final float KOMI = 6.5f;

    private final boolean isToroidal;

    private final Board board;
    private StoneColor[][] previousBoardState; // For KO

    private StoneColor nextToPlay = BLACK;

    private int blackCaptures;
    private int whiteCaptures;

    public MatchImpl(boolean isToroidal, int size) {
        this.isToroidal = isToroidal;
        board = new Board(this.isToroidal, size);
        previousBoardState = board.state();
    }

    public MatchImpl(boolean isToroidal, String[] setup) {
        this(isToroidal, setup.length);
        board.setup(setup);
        previousBoardState = board.state();
    }

    @Override public boolean isToroidal() { return isToroidal; }
    @Override public boolean isParallel() { return false; }

    @Override
    public String printOut() {
        return board.printOut();
    }

    @Override
    public void handle(Action action, Object... args) {
        if (action == Action.PLAY) play((int)args[0], (int)args[1]);
    }

    private void play(int x, int y) {
        board.setStone(x, y, nextToPlay);
        board.killSurroundedStones(other(nextToPlay));
        nextToPlay = other(nextToPlay);
    }

    public StoneColor other(StoneColor color) {
        return (color == BLACK) ? WHITE : BLACK;
    }

    @Override
    public StoneColor stoneAt(int x, int y) {
        return board.stoneAt(x, y);
    }

    @Override public int   blackScore() { return blackCaptures; }
    @Override public float whiteScore() { return whiteCaptures + KOMI; }

/*

    private void setup(int size) {
        intersections = createIntersections(size);
        previousSituation = createIntersections(size);
    }


    public MatchImpl(int size) {
        setup(size);
    }

    public MatchImpl(String[] setup) {
        this(setup.length);
        IntersectionUtils.setup(intersections,setup);
    }

    public void loadGame(int lastPlayedPieceX, int lastPlayedPieceY, beothorn.github.com.toroidalgo.go.impl.logic.StoneColor playingColor, String boardSetup) {
        lastPlayedPiece = new BoardPosition(lastPlayedPieceX, lastPlayedPieceY);
        nextToPlay = playingColor;
        String[] setup = boardSetup.split("\n");
        setup(setup.length);
        IntersectionUtils.setup(intersections,setup);
    }

    private beothorn.github.com.toroidalgo.go.impl.logic.StoneColor nextToPlay = BLACK;

    private float whiteCaptures = 0;

    private beothorn.github.com.toroidalgo.go.impl.logic.StoneColor winner = null;
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

    private int blackCaptures = 0;

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
        beothorn.github.com.toroidalgo.go.impl.logic.StoneColor loser = nextToPlay();
        winner = other(loser);
        stopAcceptingMoves();
    }

    public void endMarkingStones(){
        stopAcceptingMoves();
        winner = blackCaptures > whiteCaptures ? BLACK : WHITE;
    }


    public int blackCaptures() {
        return blackCaptures;
    }


    public float whiteCaptures() {
        return whiteCaptures + KOMI;
    }


    public beothorn.github.com.toroidalgo.go.impl.logic.StoneColor other(beothorn.github.com.toroidalgo.go.impl.logic.StoneColor color) {
        return (color == BLACK) ? WHITE : BLACK;
    }


    public beothorn.github.com.toroidalgo.go.impl.logic.StoneColor stoneAt(int x, int y) {
        return intersection(x, y).stone;
    }

    public beothorn.github.com.toroidalgo.go.impl.logic.StoneColor nextToPlay() {
        return nextToPlay;
    }

    public beothorn.github.com.toroidalgo.go.impl.logic.StoneColor winner() {
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

    private boolean killSurroundedStones(beothorn.github.com.toroidalgo.go.impl.logic.StoneColor color) {
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
        if (killSurroundedStones(nextToPlay()))
            throw new IllegalMove();

        if(IntersectionUtils.sameSituation(previousSituation, intersections))
            throw new IllegalMove();
    }


    private void stopAcceptingMoves() {
        previousSituation = copy(intersections);
        nextToPlay = null;
        notifyNextToPlay();

        capturedStonesBlack = blackCaptures;
        capturedStonesWhite = whiteCaptures;

        updateScore();
    }

    private void updateScore() {
        blackCaptures = capturedStonesBlack;
        whiteCaptures = capturedStonesWhite;
        countDeadStones();
        countTerritories();
        if(boardListener != null){
            boardListener.updateScore(blackCaptures, whiteCaptures);
        }
    }


    private void countDeadStones() {
        int size = intersections.length;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                beothorn.github.com.toroidalgo.go.impl.logic.StoneColor previousStone = previousSituation[x][y].stone;
                if ( (intersections[x][y].stone == null && BLACK.equals(previousStone) ) || BLACKDEAD.equals(intersections[x][y].stone)){
                    whiteCaptures++;
                }
                if ((intersections[x][y].stone == null && WHITE.equals(previousStone) ) || WHITEDEAD.equals(intersections[x][y].stone)){
                    blackCaptures++;
                }
            }
        }
    }


    public Map<beothorn.github.com.toroidalgo.go.impl.logic.StoneColor, List<List<BoardPosition>>> getTerritoriesOwnership(){
        LinkedHashMap<beothorn.github.com.toroidalgo.go.impl.logic.StoneColor, List<List<BoardPosition>>> ownedTerritories = new LinkedHashMap<>();
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
            blackCaptures += blackTerritory.size();
        }

        List<List<BoardPosition>> whiteTerritories = territoriesOwnership.get(WHITE);
        for(List<BoardPosition> whiteTerritory : whiteTerritories){
            whiteCaptures += whiteTerritory.size();
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
*/

}
