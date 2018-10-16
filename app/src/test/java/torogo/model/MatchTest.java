package torogo.model;


import junit.framework.TestCase;

import torogo.model.impl.MatchImpl;

import static org.junit.Assert.assertArrayEquals;
import static torogo.model.Match.Action.PASS;
import static torogo.model.Match.Action.PLAY;
import static torogo.model.Match.Action.RESIGN;
import static torogo.model.StoneColor.BLACK;
import static torogo.model.StoneColor.WHITE;

public class MatchTest extends TestCase {

	private Match match;

	public void testSingleStoneCapture() {
		initToroidal(9);

		play(4, 2);
		play(4, 3);
		play(3, 3);
		play(3, 4);
		play(5, 3);
		play(5, 4);

		assertNotNull(stoneAt(4, 3));
		play(4,4);
		assertNull(stoneAt(4, 3));
	}

	private StoneColor stoneAt(int x, int y) {
		return match.stoneAt(x, y);
	}


	private void initToroidal(int size) {
		match = new MatchImpl(true, size);
	}
	private void initToroidal(String... setup) {
		match = new MatchImpl(true, setup);
	}

	private void play(int x, int y) {
		match.handle(PLAY, x, y);
	}

	public void testSingleStoneCaptureToroidal_shouldNotCapture() {
		initToroidal("+ + + + w b + + +",
					 "+ + + + b + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +",
					 "+ + + + + + + + +");
		play(3, 0);

		assertBoard(match.printOut(), 	" + + + b w b + + +",
									    " + + + + b + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +",
									    " + + + + + + + + +");
	}


	public void testSingleStoneCaptureToroidal_shouldCapture() {
		initToroidal(	"+ + + b w b + + +",
						"+ + + + b + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");

		play(4, 8);

		assertBoard(match.printOut(), 	" + + + b + b + + +",
										" + + + + b + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + b + + + +");
	}


	public void testBigGroupCapture() {
		initToroidal(	"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + b b + + +",
						"+ + + b w w b + +",
						"+ + + + b w b + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");

		play(5, 5);

		assertBoard(match.printOut(),	" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + b b + + +",
										" + + + b + + b + +",
										" + + + + b + b + +",
										" + + + + + b + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +");

		assertScore(3, 6.5f);
	}

	public void testSuicide() {
		initToroidal(	"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + w w + + +",
						"+ + + w b b w + +",
						"+ + + + w + w + +",
						"+ + + + + w + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");

		assertTrue(match.isValidMove(0, 0));
		assertFalse(match.isValidMove(5, 4));
	}

	public void testKillOtherFirst() {
		initToroidal(	"+ + + + + + + + +",
						"+ + + + b + + + +",
						"+ + + b w b + + +",
						"+ + + w + w + + +",
						"+ + + + w + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");
		assertTrue(match.isValidMove(4, 3));
	}


	public void testKo() {
		initToroidal(	"+ + + + + + + + +",
						"+ + + + b + + + +",
						"+ + + b w b + + +",
						"+ + + w + w + + +",
						"+ + + + w + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");
		assertTrue(match.isValidMove(4, 3));
		play(4, 3);
		assertFalse(match.isValidMove(4, 2));
	}


	public void testMultipleGroupKill() {
		initToroidal(	"+ + + + + + + + +",
						"+ + + + b + + + +",
						"+ + + b w b + + +",
						"+ + b w + + + + +",
						"+ + + b + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +",
						"+ + + + + + + + +");

		play(4, 3);

		assertBoard(match.printOut(), 	" + + + + + + + + +",
										" + + + + b + + + +",
										" + + + b + b + + +",
										" + + b + b + + + +",
										" + + + b + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +",
										" + + + + + + + + +");
		assertScore(2, 6.5f);
	}

	public void testPass() {
		initToroidal();

		assertSame(BLACK, match.nextToPlay());
		match.handle(PASS);
		assertSame(WHITE, match.nextToPlay());
	}

	public void testEndByPass() {
		initToroidal();

		match.handle(PASS);
		match.handle(PASS);
		assertNull(match.nextToPlay());
	}

	public void testResign() {
		initToroidal();

		match.handle(RESIGN);
		assertNull(match.nextToPlay());
		assertSame(WHITE, match.winner());
	}

//	public void testSingleStoneCaptureScore() {
//		String[] setup = new String[]{
//			    "+ + + + + + + + +",
//				"+ + + + + + + + +",
//				"+ + + + b + + + +",
//				"+ + + b w b + + +",
//				"+ + + w + w + + +",
//				"+ + + + + + + + +",
//				"+ + + + + + + + +",
//				"+ + + + + + + + +",
//				"+ + + + + + + + +"};
//		initToroidal(new String[]{});
//		match = new ToroidalGoMatch(setup);
//
//		assertTrue(match.stoneAt(4, 3) != null);
//		match.playStone(4,4);
//		assertTrue(match.stoneAt(4, 3) == null);
//		assertScore(1, 6.5f);
//
//		match.playStone(4,5);
//		match.playStone(0,1);
//
//		assertTrue(match.stoneAt(4, 4) != null);
//		match.playStone(4,3);
//		assertTrue(match.stoneAt(4, 4) == null);
//		assertScore(1, 7.5f);
//	}

	/*
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


boardSlotsCount is in the match

porque passamos o context para o MatchSimulator

porque o match tem initListener e setStateListener

retirar repositorio do google do gradle para ver o que acontece
*/

	private void assertScore(int black, float white) {
		assertEquals(black, match.blackScore(), 0);
		assertEquals(white, match.whiteScore(), 0);
	}

	private void assertBoard(String[] actualBoard, String... expectedBoard) {
		assertArrayEquals(expectedBoard, actualBoard);
	}

}