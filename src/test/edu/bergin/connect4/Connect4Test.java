package edu.bergin.connect4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Acceptance tests for Connect Four

public class Connect4Test {

	private Connect4 c4;
	private NullView console;
	private int numberOfFieldsToWin = 4;
	private String previousPlayer = "o";

	private String alternatePlayer() {
		String nextPlayer = previousPlayer.equals("x") ? "o" : "x";
		previousPlayer = nextPlayer;
		return nextPlayer;
	}

	@Test
	public void cannotAdd3rdPlayer() {
		try {
			c4.addPlayer(new SimpleStrategyPlayer("x"));
			c4.addPlayer(new SimpleStrategyPlayer("o"));
			c4.addPlayer(new SimpleStrategyPlayer("z"));
			fail("Should not be able to add a 3rd player");
		} catch (Exception expected) {
			System.out.println("Expected exception: " + expected.getMessage());
		}
	}

	@Test
	public void cannotMoveIfOtherPlayerHasWon() {
		// Fill up a row with one player's pieces
		for (int col = 0, row = c4.numberOfRows()-1; col < c4.numberOfCols(); col++) {
			c4.move(alternatePlayer(), row, col);
			// Stop when a row is full because the game should be over
			if (c4.isOver())
				break;
			c4.move(alternatePlayer(), row-1, col);
		}
		try {
			c4.move(alternatePlayer(), c4.numberOfRows()-3, 0);
			fail("should not be able to move when a row is full");
		} catch (Exception e) { }
	}

	@Test
	public void cannotTakeAFieldIfItIsTaken() {
		c4.move("x", c4.numberOfRows()-1, 0);
		try {
			c4.move("o", c4.numberOfRows()-1, 0);
			fail("cannot take a field that's taken");
		} catch (Exception e) {
		}
	}

	@Test
	public void cannotTakeAFieldUnlesRowBelowTaken() {
		c4.move("x", 5, 0);
		try {
			c4.move("o", 3, 0);
			fail("cannot take a field unless row below occupied");
		} catch (Exception e) {
		}
	}

	private void fillColumn(int col, int numberOfMoves) {
		for (int row = c4.numberOfRows()-1, count = 0; count < numberOfMoves; row--, count++)
			c4.move(alternatePlayer(), row, col);
	}

	private void fillRow(int row, int numberOfMoves) {
		for (int col = 0, count = 0; count < numberOfMoves; col++, count++)
			c4.move(alternatePlayer(), row, col);
	}

	@Test
	public void mustHave2Players() {
		c4.addPlayer(new SimpleStrategyPlayer("x"));
		try {
			c4.play("x");
			fail("should have 2 players");
		} catch (Exception expected) {
			System.out.println("Expected exception: " + expected.getMessage());
		}
	}

	@Test
	public void noFieldsAreTakenAtStart() {
		assertTrue("should be no moves at start of game",
				c4.numberOfMoves() == 0);
	}

	@Test
	public void playersShouldAlternate() {
		c4.move("x", 5, 0);
		try {
			c4.move("x", 5, 1);
			fail("should not allow a player to move 2x in a row");
		} catch (Exception e) {
		}
	}

	@Before
	public void setUp() throws Exception {
		// We'll be testing a game of default size
		console = new NullView();
		c4 = new Connect4(console);
	}

	@Test
	public void shouldBeOverWhen4ContiguousHorizontally() {
		for (int col = 0; col < c4.numberOfCols(); col++) {
			c4.move(alternatePlayer(), 5, col);
			// Stop when 4 in a row because the game should be over
			if (col == numberOfFieldsToWin - 1)
				break;
			c4.move(alternatePlayer(), 4, col);
		}
		assertTrue("should be over when a player has 4 slots in a row",
				c4.isOver());
	}

	@Test
	public void shouldBeOverWhen4ContiguousVertically() {
		for (int row = c4.numberOfRows()-1, count = 0; row >= 0; row--) {
			c4.move(alternatePlayer(), row, 1);
			// Stop when a column is full because the game should be over
			if (++count == numberOfFieldsToWin)
				break;
			c4.move(alternatePlayer(), row, 2);
		}
		assertTrue("should be over when 4 in a row vertically",
				c4.isOver());
	}

	@Test
	public void shouldBeOverWhenAllFieldsAreTakenAndNoWinner() {

		fillColumn(0, 5);
		fillColumn(1, 5);
		fillColumn(3, 5);
		fillColumn(2, 5);
		fillColumn(4, 5);
		fillColumn(6, 5);
		fillColumn(5, 5);
		fillRow(0, 7);

		assertTrue("should be over when all fields are taken",
				c4.isOver() && (c4.numberOfMoves() == c4.numberOfCols() * c4.numberOfRows()));
	}

	@Test
	public void shouldBeOverWhenLeftToRightDiagonalIsTakenByPlayer() {

		// Only tests 1 diagonal
		// Fill a size 4 L2R diagonal with same pieces
		c4.move("o", 5, 0);
		c4.move("x", 4, 0);
		c4.move("o", 3, 0);
		c4.move("x", 2, 0);
		c4.move("o", 5, 1);
		c4.move("x", 4, 1);
		c4.move("o", 5, 2);
		c4.move("x", 3, 1);
		c4.move("o", 2, 1);
		c4.move("x", 4, 2);
		c4.move("o", 3, 2);
		c4.move("x", 5, 3);

		assertTrue("should be over when a L2R diagonal is taken",	c4.isOver());
	}

	@Test
	public void shouldBeOverWhenRightToLeftDiagonalIsTakenByPlayer() {

		// Only tests 1 diagonal
		// Fill a size 4 R2L diagonal with same pieces
		c4.move("o", 5, 6);
		c4.move("x", 4, 6);
		c4.move("o", 3, 6);
		c4.move("x", 2, 6);
		c4.move("o", 5, 5);
		c4.move("x", 4, 5);
		c4.move("o", 5, 4);
		c4.move("x", 3, 5);
		c4.move("o", 2, 5);
		c4.move("x", 4, 4);
		c4.move("o", 3, 4);
		c4.move("x", 5, 3);

		assertTrue("should be over when a R2L diagonal is taken", c4.isOver());
	}

	@Test
	public void shouldHaveMinNumberOfMovesInCompleteGame() {
		final int MIN_MOVES = 7;
		c4.addPlayer(new SimpleStrategyPlayer("x", 0)); // No think time for tests
		c4.addPlayer(new SimpleStrategyPlayer("o", 0)); // No think time for tests
		c4.play("x");
		assertTrue("should have a minimum number of moves in a complete game", c4.numberOfMoves() >= MIN_MOVES);
	}

	@Test
	public void shouldNotBeOverIfColumnTakenByTwoPlayers() {
		// Only tests 1 column
		// Fill a column with alternate moves...
		for (int row = c4.numberOfRows()-1; row >= 0; row--)
			c4.move(alternatePlayer(), row, 0);
		assertFalse("should not be over if column taken by two players", c4.isOver());
	}

	@Test
	public void shouldNotBeOverIfDiagonalTakenByTwoPlayers() {
		// Only tests 1 diagonal
		// Fill a size 4 diagonal with alternate pieces
		c4.move("o", 5, 0);
		c4.move("x", 4, 0);
		c4.move("o", 3, 0);
		c4.move("x", 2, 0);
		c4.move("o", 1, 0);
		c4.move("x", 5, 1);
		c4.move("o", 5, 2);
		c4.move("x", 4, 1);
		c4.move("o", 3, 1);
		c4.move("x", 4, 2);
		c4.move("o", 5, 3);

		assertFalse("should not be over if diagonal taken by two players", c4.isOver());
	}

	@Test
	public void shouldNotBeOverIfRowTakenByTwoPlayers() {
		// Only tests 1 row
		// Fill a row with alternate moves...
		for (int col = 0, row = c4.numberOfRows()-1; col < c4.numberOfCols(); col++)
			c4.move(alternatePlayer(), row, col);
		assertFalse("should not be over if row taken by two players", c4.isOver());
	}

	@Test
	public void shouldNotBeOverWhenNoFieldsAreTaken() {
		assertFalse("should not be over", c4.isOver());
	}
}
