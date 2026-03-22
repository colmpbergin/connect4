package edu.bergin.connect4;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	private SimpleStrategyPlayer simplePlayer = new SimpleStrategyPlayer("x", 0); // No think time for tests
	private SimpleStrategyPlayer randomPlayer = new SimpleStrategyPlayer("o", 0); // No think time for tests
	private TacticalStrategyPlayer tacticalPlayer = new TacticalStrategyPlayer("x", 0); // No think time for tests
	private Board theMoves;

	@Before
	public void setUp() throws Exception {
		// Set up a new 6x7 (row x col) board
		theMoves = new Board(6,7);
	}

	@Test
	public void takingValidTurnAddsMove() {
		simplePlayer.takeTurn(theMoves);
		assertTrue("simple player taking a turn adds a move", theMoves.moves().size() == 1);
		randomPlayer.takeTurn(theMoves);
		assertTrue("random player taking a turn adds a move", theMoves.moves().size() == 2);
	}

	@Test
	public void takingInvalidTurnThrows () {
		simplePlayer.takeTurn(theMoves);
		try {
			simplePlayer.takeTurn(theMoves);
			fail("player cannot move twice in succession");
		} catch (Exception expected) {
			System.out.println("Expected exception: " + expected.getMessage());
		}

		randomPlayer.takeTurn(theMoves);
		try {
			randomPlayer.takeTurn(theMoves);
			fail("player cannot move twice in succession");
		} catch (Exception expected) {
			System.out.println("Expected exception: " + expected.getMessage());
		}
	}

	// Enhanced tests for SimpleStrategyPlayer

	@Test
	public void testFindsWinningMove() {
		// Set up a board where simplePlayer can win
		theMoves.add(new Move("x", 5, 0));
		theMoves.add(new Move("o", 5, 1));
		theMoves.add(new Move("x", 4, 0));
		theMoves.add(new Move("o", 5, 2));
		theMoves.add(new Move("x", 3, 0));
		theMoves.add(new Move("o", 5, 3));

		// SimpleStrategyPlayer should take the winning move at (2,0)
		simplePlayer.takeTurn(theMoves);

		assertTrue("should take winning move", theMoves.takenBy("x", 2, 0));
		assertTrue("game should be over", theMoves.isOver());
	}

	@Test
	public void testBlocksOpponentWin() {
		// Set up a board where opponent is about to win
		theMoves.add(new Move("o", 5, 0));
		theMoves.add(new Move("x", 5, 1));
		theMoves.add(new Move("o", 4, 0));
		theMoves.add(new Move("x", 5, 2));
		theMoves.add(new Move("o", 3, 0));

		// SimpleStrategyPlayer (x) should block at (2,0)
		simplePlayer.takeTurn(theMoves);

		assertTrue("should block opponent's winning move", theMoves.takenBy("x", 2, 0));
		assertFalse("game should not be over", theMoves.isOver());
	}

	@Test
	public void testBuildsOnOwnPieces() {
		// Set up a board with simplePlayer's pieces
		theMoves.add(new Move("x", 5, 3));
		theMoves.add(new Move("o", 5, 0));
		theMoves.add(new Move("x", 4, 3));
		theMoves.add(new Move("o", 5, 1));

		// SimpleStrategyPlayer should build on its own stack
		simplePlayer.takeTurn(theMoves);

		// Should place at (3,3) to continue building
		assertTrue("should build on own pieces", theMoves.takenBy("x", 3, 3));
	}

	@Test
	public void testPicksAvailableColumn() {
		// Empty board - should pick any valid column
		simplePlayer.takeTurn(theMoves);

		// Should have placed a piece in bottom row
		boolean foundMove = false;
		for (int col = 0; col < 7; col++) {
			if (theMoves.takenBy("x", 5, col)) {
				foundMove = true;
				break;
			}
		}
		assertTrue("should pick an available column", foundMove);
	}

	@Test
	public void testThinkTime() {
		// Create a player with 1-second think time for this test
		SimpleStrategyPlayer playerWithThinkTime = new SimpleStrategyPlayer("x", 1000);

		// Measure time taken for a move
		long startTime = System.currentTimeMillis();
		playerWithThinkTime.takeTurn(theMoves);
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;

		// Should take at least 1 sec due to think time
		assertTrue("should have think time of at least 2 seconds", duration >= 999);
	}

	@Test
	public void testDoesNotMoveOutOfTurn() {
		// Player x moves first
		simplePlayer.takeTurn(theMoves);

		// Player x tries to move again - should throw exception
		try {
			simplePlayer.takeTurn(theMoves);
			fail("should not allow player to move out of turn");
		} catch (Exception expected) {
			// Expected behavior
			assertTrue("exception message should mention turn",
				expected.getMessage().contains("turn") ||
				expected.getMessage().contains("not") ||
				expected.getMessage().contains("valid"));
		}
	}

	@Test
	public void testPrioritizesWinOverBlock() {
		// Set up board where player can win OR block opponent
		theMoves.add(new Move("x", 5, 0));
		theMoves.add(new Move("o", 5, 1));
		theMoves.add(new Move("x", 4, 0));
		theMoves.add(new Move("o", 4, 1));
		theMoves.add(new Move("x", 3, 0));
		theMoves.add(new Move("o", 3, 1));

		// Both players can win on their next move
		// x can win at (2,0), o can win at (2,1)
		// x should take its own winning move, not block
		simplePlayer.takeTurn(theMoves);

		assertTrue("should prioritize winning over blocking", theMoves.takenBy("x", 2, 0));
		assertTrue("game should be over", theMoves.isOver());
	}

	@Test
	public void testHandlesFullColumn() {
		// Fill column 0 completely
		theMoves.add(new Move("x", 5, 0));
		theMoves.add(new Move("o", 4, 0));
		theMoves.add(new Move("x", 3, 0));
		theMoves.add(new Move("o", 2, 0));
		theMoves.add(new Move("x", 1, 0));
		theMoves.add(new Move("o", 0, 0));

		// SimpleStrategyPlayer should pick a different column
		simplePlayer.takeTurn(theMoves);

		// Should have placed in a column other than 0
		boolean foundMove = false;
		for (int col = 1; col < 7; col++) {
			if (theMoves.takenBy("x", 5, col)) {
				foundMove = true;
				break;
			}
		}
		assertTrue("should pick available column when one is full", foundMove);
	}

	@Test
	public void testRandomOpeningMoveIfFirst() {
		Move opening = simplePlayer.randomOpeningMoveIfFirst(theMoves);
		assertNotNull("should return an opening move on an empty board", opening);

		assertEquals("opening move should be for player x", "x", opening.player());
		assertTrue("opening move should be valid",
				theMoves.isValidMove("x", opening.getRow(), opening.getColumn()));
		assertEquals("opening move should land at the column landing row",
				theMoves.landingRow("x", opening.getColumn()), opening.getRow());

		// Once any move has been made, this should return null
		theMoves.add(opening);
		assertNull("should return null when board is not at first move",
				simplePlayer.randomOpeningMoveIfFirst(theMoves));
	}

	@Test
	public void testTacticalPlayerTakesRandomOpeningMoveOnFirstTurn() {
		// Empty board: tactical player should use the shared opening-move helper
		Board emptyBoard = new Board(6, 7);
		tacticalPlayer.takeTurn(theMoves);
		assertEquals("tactical opening should add exactly one move",
				1, theMoves.numberOfMoves());

		Move opening = theMoves.moves().iterator().next();
		assertEquals("opening move should be for player x", "x", opening.player());
		assertTrue("opening move coordinates should be a valid move on empty board",
				emptyBoard.isValidMove("x", opening.getRow(), opening.getColumn()));
		assertEquals("opening move should land at empty-board landing row",
				emptyBoard.landingRow("x", opening.getColumn()), opening.getRow());

		// After the first move, it should no longer be treated as "first move"
		assertNull("shared helper should return null once board has a move",
				tacticalPlayer.randomOpeningMoveIfFirst(theMoves));
	}
}
