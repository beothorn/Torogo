package beothorn.github.com.toroidalgo.tests;

import android.test.InstrumentationTestCase;
import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoBoard;

public class GoTest extends InstrumentationTestCase{

	private GoBoard _board;

    private void assertScore(int black, int white) {
		assertSame(black, _board.blackScore());
		assertSame(white, _board.whiteScore());
	}

	public void testSingleStoneCapture() {
		_board = new ToroidalGoBoard(9);
		
		play(4, 2);
		play(4, 3);
		play(3, 3);
		play(3, 4);
		play(5, 3);
		play(5, 4);

		assertNotNull(_board.stoneAt(4, 3));
		play(4,4);
		assertNull(   _board.stoneAt(4, 3));
	}

	private void play(int x, int y) {
		assertTrue(_board.canPlayStone(x, y));
		_board.playStone(x, y);
	}
	
	public void testSingleStoneCaptureToroidal_shouldNotCapture() {
		String[] setup = new String[]{
			    "+ + + + w b + + +",
				"+ + + + b + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		play(3, 0);
		
		assertEquals(
		    " + + + b w b + + +\n" +
			" + + + + b + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n",
			_board.printOut()
		);
	}
	
	public void testSingleStoneCaptureToroidal_shouldCapture() {
		String[] setup = new String[]{
			    "+ + + b w b + + +",
				"+ + + + b + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		play(4, 8);
		
		assertEquals(
		    " + + + b + b + + +\n" +
			" + + + + b + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + b + + + +\n",
			_board.printOut()
		);
	}
	
	public void testBigGroupCapture() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + b b + + +",
				"+ + + b w w b + +",
				"+ + + + b w b + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		_board.playStone(5, 5);
		
		assertEquals(
		    " + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + b b + + +\n" +
			" + + + b + + b + +\n" +
			" + + + + b + b + +\n" +
			" + + + + + b + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n" +
			" + + + + + + + + +\n",
			_board.printOut()
		);
		
		assertScore(3, 0);
	}
	
	public void testSuicide() {
		String[] setup = new String[] {
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + w w + + +",
				"+ + + w b b w + +",
				"+ + + + w + w + +",
				"+ + + + + w + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertFalse(_board.canPlayStone(5, 4));
		assertTrue(_board.stoneAt(5, 4) == null);
	}
	
	public void testKillOtherFirst() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b + + + +",
				"+ + + b w b + + +",
				"+ + + w + w + + +",
				"+ + + + w + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertTrue(_board.canPlayStone(4, 3));
	}
	
	public void testKo() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b + + + +",
				"+ + + b w b + + +",
				"+ + + w + w + + +",
				"+ + + + w + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		assertTrue(_board.canPlayStone(4, 3));
		_board.playStone(4, 3);
		assertFalse(_board.canPlayStone(4, 2));
	}

	
	public void testMultipleGroupKill() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b + + + +",
				"+ + + b w b + + +",
				"+ + b w + + + + +",
				"+ + + b + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		_board.playStone(4, 3);
		assertEquals(_board.printOut(),
			 	" + + + + + + + + +\n"+
				" + + + + b + + + +\n"+
				" + + + b + b + + +\n"+
				" + + b + b + + + +\n"+
				" + + + b + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"+
				" + + + + + + + + +\n"
		);
		assertScore(2, 0);
	}

	public void testPass() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		assertSame(GoBoard.StoneColor.BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(GoBoard.StoneColor.WHITE, subject.nextToPlay());
	}

	public void testEndByPass() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		assertSame(GoBoard.StoneColor.BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(GoBoard.StoneColor.WHITE, subject.nextToPlay());
		subject.passTurn();
		assertNull(subject.nextToPlay());
	}
	
	public void testResign() {
		ToroidalGoBoard subject = new ToroidalGoBoard(new String[]{});
		subject.resign();
		assertNull(subject.nextToPlay());
		assertSame(GoBoard.StoneColor.WHITE, subject.winner());
	}

	public void testSingleStoneCaptureScore() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + b + + + +",
				"+ + + b w b + + +",
				"+ + + w + w + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		
		assertTrue(_board.stoneAt(4, 3) != null);
		_board.playStone(4,4);
		assertTrue(_board.stoneAt(4, 3) == null);
		assertScore(1, 0);
		
		_board.playStone(4,5);
		_board.playStone(0,1);
		
		assertTrue(_board.stoneAt(4, 4) != null);
		_board.playStone(4,3);
		assertTrue(_board.stoneAt(4, 4) == null);
		assertScore(1, 1);
	}
	
	public void testScore() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b + + + +",
				"+ + + b + b + + +",
				"+ + b + b + + + +",
				"+ + + b + + + + +",
				"+ + + + w w w + +",
				"+ + + + w + w + +",
				"+ + + + + w + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		assertScore(2, 1);
	}

	public void testDeadGroup() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b b + + +",
				"+ + + b + + b + +",
				"+ + b + + + w b +",
				"+ + + b + w w b +",
				"+ + + + b w b + +",
				"+ + + + b b b + +",
				"+ w + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(5, 4);
		assertScore(14, 0);
		
		setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b b + + +",
				"+ b b b + w b + +",
				"b w + w + w w b +",
				"+ b b b + w w b +",
				"+ + + + b w b + +",
				"+ + w + b b b + +",
				"+ w + w + + + + +",
				"+ + w + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(5, 4);
		assertScore(20, 1);
	}

	public void testDeadGroupMisclickOnFreeIntersectionDoesNotFreeze() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + + + b b + + +",
				"+ + + b + + b + +",
				"+ + b + + + w b +",
				"+ + + b + w w b +",
				"+ + + + b w b + +",
				"+ + + + b b b + +",
				"+ w + + + + + + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(0, 0);
	}

	public void testUntoggleDeadGroup() {
		String[] setup = new String[]{
			    "+ + + + + + + + +",
				"+ + b b + + + + +",
				"+ b w + b + + + +",
				"+ + b b + + + + +",
				"+ b w + b + + + +",
				"+ + b b + + + + +",
				"+ + + + + + + + +",
				"+ + + + + + w + +",
				"+ + + + + + + + +"};
		_board = new ToroidalGoBoard(setup);
		_board.passTurn();
		_board.passTurn();
		_board.toggleDeadStone(2, 2);
		assertScore(3, 0);
		_board.toggleDeadStone(2, 4);
		assertScore(6, 0);
		_board.toggleDeadStone(2, 4);
		assertScore(3, 0);
	}

	public void testGetLastPlayedStone(){
		_board = new ToroidalGoBoard(3);
		_board.playStone(0,0);
		assertTrue(_board.stoneAtPositionIsLastPlayedStone(0,0));
		_board.playStone(1,1);
		assertFalse(_board.stoneAtPositionIsLastPlayedStone(0,0));
		assertTrue(_board.stoneAtPositionIsLastPlayedStone(1,1));
	}
	
}