package torogo.model.test;

import android.test.InstrumentationTestCase;

import beothorn.github.com.toroidalgo.go.impl.logic.GoMatch;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.WHITE;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.BLACK;
import beothorn.github.com.toroidalgo.go.impl.logic.ToroidalGoMatch;

public class GoTest extends InstrumentationTestCase{

	private GoMatch match;

	private void assertScore(int black, float white) {
		assertEquals(black, match.blackScore(), 0);
		assertEquals(white, match.whiteScore(), 0);
	}

	public void testSingleStoneCapture() {
		match = new ToroidalGoMatch(9);
		
		play(4, 2);
		play(4, 3);
		play(3, 3);
		play(3, 4);
		play(5, 3);
		play(5, 4);

		assertNotNull(match.stoneAt(4, 3));
		play(4,4);
		assertNull(match.stoneAt(4, 3));
	}

	private void play(int x, int y) {
		assertTrue(match.canPlayStone(x, y));
		match.playStone(x, y);
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
		match = new ToroidalGoMatch(setup);
		
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
			match.printOut()
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
		match = new ToroidalGoMatch(setup);
		
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
			match.printOut()
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
		match = new ToroidalGoMatch(setup);
		
		match.playStone(5, 5);
		
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
			match.printOut()
		);
		
		assertScore(3, 6.5f);
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
		match = new ToroidalGoMatch(setup);
		assertFalse(match.canPlayStone(5, 4));
		assertTrue(match.stoneAt(5, 4) == null);
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
		match = new ToroidalGoMatch(setup);
		assertTrue(match.canPlayStone(4, 3));
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
		match = new ToroidalGoMatch(setup);
		assertTrue(match.canPlayStone(4, 3));
		match.playStone(4, 3);
		assertFalse(match.canPlayStone(4, 2));
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
		match = new ToroidalGoMatch(setup);
		
		match.playStone(4, 3);
		assertEquals(match.printOut(),
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
		assertScore(2, 6.5f);
	}

	public void testPass() {
		ToroidalGoMatch subject = new ToroidalGoMatch(new String[]{});
		assertSame(BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(WHITE, subject.nextToPlay());
	}

	public void testEndByPass() {
		ToroidalGoMatch subject = new ToroidalGoMatch(new String[]{});
		assertSame(BLACK, subject.nextToPlay());
		subject.passTurn();
		assertSame(WHITE, subject.nextToPlay());
		subject.passTurn();
		assertNull(subject.nextToPlay());
	}
	
	public void testResign() {
		ToroidalGoMatch subject = new ToroidalGoMatch(new String[]{});
		subject.resign();
		assertNull(subject.nextToPlay());
		assertSame(WHITE, subject.winner());
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
		match = new ToroidalGoMatch(setup);
		
		assertTrue(match.stoneAt(4, 3) != null);
		match.playStone(4,4);
		assertTrue(match.stoneAt(4, 3) == null);
		assertScore(1, 6.5f);
		
		match.playStone(4,5);
		match.playStone(0,1);
		
		assertTrue(match.stoneAt(4, 4) != null);
		match.playStone(4,3);
		assertTrue(match.stoneAt(4, 4) == null);
		assertScore(1, 7.5f);
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
		match = new ToroidalGoMatch(setup);
		match.passTurn();
		match.passTurn();
		assertScore(2, 7.5f);
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
		match = new ToroidalGoMatch(setup);
		match.passTurn();
		match.passTurn();
		match.toggleDeadStone(5, 4);
		assertScore(14, 6.5f);
		
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
		match = new ToroidalGoMatch(setup);
		match.passTurn();
		match.passTurn();
		match.toggleDeadStone(5, 4);
		assertScore(20, 7.5f);
	}

	public void testDeadGroupMissClickOnFreeIntersectionDoesNotFreeze() {
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
		match = new ToroidalGoMatch(setup);
		match.passTurn();
		match.passTurn();
		match.toggleDeadStone(0, 0);
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
		match = new ToroidalGoMatch(setup);
		match.passTurn();
		match.passTurn();
		match.toggleDeadStone(2, 2);
		assertScore(3, 6.5f);
		match.toggleDeadStone(2, 4);
		assertScore(6, 6.5f);
		match.toggleDeadStone(2, 4);
		assertScore(3, 6.5f);
	}

	public void testGetLastPlayedStone(){
		match = new ToroidalGoMatch(3);
		assertTrue(match.stoneAtPositionIsLastPlayedStone(0,0));
		match.playStone(1,1);
		assertFalse(match.stoneAtPositionIsLastPlayedStone(0,0));
		assertTrue(match.stoneAtPositionIsLastPlayedStone(1,1));
	}
	
}