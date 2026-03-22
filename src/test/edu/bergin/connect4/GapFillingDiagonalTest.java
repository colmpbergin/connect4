package edu.bergin.connect4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for gap-filling feature in Diagonal.findWinningMove()
 *
 * Note: LeftToRightDiagonal starting at (0,0) goes: (0,0), (1,1), (2,2), (3,3), (4,4), (5,5)
 *       RightToLeftDiagonal starting at (0,6) goes: (0,6), (1,5), (2,4), (3,3), (4,2), (5,1)
 */
public class GapFillingDiagonalTest {
	private LeftToRightDiagonal leftDiagonal;
	private RightToLeftDiagonal rightDiagonal;
	private Board board;

	@Before
	public void setUp() throws Exception {
		board = new Board(6, 7);
		// Create a diagonal starting at (0,0) with size 6
		leftDiagonal = new LeftToRightDiagonal(0, 0, 6, board, 4);
		// Create a diagonal starting at (0,6) with size 6
		rightDiagonal = new RightToLeftDiagonal(0, 6, 6, board, 4);
	}

	@Test
	public void shouldFindWinningMoveAtEndOfLeftDiagonal() {
		// Pattern: XXX_ on left-to-right diagonal
		// Positions: (0,0), (1,1), (2,2), (3,3)
		// Add support piece at (4,3) for winning move at (3,3) to be valid
		board.add(new Move("x", 0, 0));
		board.add(new Move("x", 1, 1));
		board.add(new Move("x", 2, 2));
		board.add(new Move("x", 4, 3)); // Support piece below (3,3)

		Move move = leftDiagonal.findWinningMove("x");
		assertNotNull("should find winning move at end of diagonal", move);
		assertTrue("winning move should be at (3,3)", move.isInRow(3) && move.isInColumn(3));
	}

	@Test
	public void shouldFindWinningMoveInMiddleGapOfLeftDiagonal() {
		// Pattern: XX_X on left-to-right diagonal
		// Positions: (0,0), (1,1), skip (2,2), (3,3); add (3,2) to ensure winning move valid
		board.add(new Move("x", 0, 0));
		board.add(new Move("x", 1, 1));
		// Skip (2,2)
		board.add(new Move("x", 3, 3));
		board.add(new Move("x", 3, 2));

		Move move = leftDiagonal.findWinningMove("x");
		assertNotNull("should find winning move in gap", move);
		assertTrue("winning move should be at (2,2)", move.isInRow(2) && move.isInColumn(2));
	}

	@Test
	public void shouldFindWinningMoveAtStartOfLeftDiagonal() {
		// Pattern: _XXX on left-to-right diagonal
		// Skip (0,0), then (1,1), (2,2), (3,3)
		// Add support piece at (1,0) for winning move at (0,0) to be valid
		board.add(new Move("x", 1, 1));
		board.add(new Move("x", 2, 2));
		board.add(new Move("x", 3, 3));
		board.add(new Move("x", 1, 0)); // Support piece below (0,0)

		Move move = leftDiagonal.findWinningMove("x");
		assertNotNull("should find winning move at start", move);
		assertTrue("winning move should be at (0,0)", move.isInRow(0) && move.isInColumn(0));
	}

	@Test
	public void shouldNotFindWinningMoveWhenOpponentBlocksLeftDiagonal() {
		// Pattern: XXoX on left-to-right diagonal
		board.add(new Move("x", 0, 0));
		board.add(new Move("x", 1, 1));
		board.add(new Move("o", 2, 2));
		board.add(new Move("x", 3, 3));

		Move move = leftDiagonal.findWinningMove("x");
		assertNull("should not find winning move when opponent blocks", move);
	}

	@Test
	public void shouldFindWinningMoveAtEndOfRightDiagonal() {
		// Pattern: XXX_ on right-to-left diagonal
		// Positions: (0,6), (1,5), (2,4), (3,3)
		// Add support piece at (4,3) for winning move at (3,3) to be valid
		board.add(new Move("x", 0, 6));
		board.add(new Move("x", 1, 5));
		board.add(new Move("x", 2, 4));
		board.add(new Move("x", 4, 3)); // Support piece below (3,3)

		Move move = rightDiagonal.findWinningMove("x");
		assertNotNull("should find winning move at end of diagonal", move);
		assertTrue("winning move should be at (3,3)", move.isInRow(3) && move.isInColumn(3));
	}

	@Test
	public void shouldFindWinningMoveInMiddleGapOfRightDiagonal() {
		// Pattern: XX_X on right-to-left diagonal
		// Positions: (0,6), (1,5), skip (2,4), (3,3) and add (3,4) to ensure winning move valid
		board.add(new Move("x", 0, 6));
		board.add(new Move("x", 1, 5));
		// Skip (2,4)
		board.add(new Move("x", 3, 3));
		board.add(new Move("x", 3, 4));

		Move move = rightDiagonal.findWinningMove("x");
		assertNotNull("should find winning move in gap", move);
		assertTrue("winning move should be at (2,4)", move.isInRow(2) && move.isInColumn(4));
	}

	@Test
	public void shouldFindWinningMoveAtStartOfRightDiagonal() {
		// Pattern: _XXX on right-to-left diagonal
		// Skip (0,6), then (1,5), (2,4), (3,3)
		// Winning move at (0,6) needs support at (1,6)
		board.add(new Move("x", 1, 5));
		board.add(new Move("x", 2, 4));
		board.add(new Move("x", 3, 3));
		board.add(new Move("x", 1, 6)); // Support piece below (0,6)

		Move move = rightDiagonal.findWinningMove("x");
		assertNotNull("should find winning move at start", move);
		assertTrue("winning move should be at (0,6)", move.isInRow(0) && move.isInColumn(6));
	}

	@Test
	public void shouldNotFindWinningMoveWithTwoGapsInDiagonal() {
		// Pattern: X_X_ on diagonal (two gaps)
		board.add(new Move("x", 0, 0));
		// Skip (1,1)
		board.add(new Move("x", 2, 2));
		// Skip (3,3)

		Move move = leftDiagonal.findWinningMove("x");
		assertNull("should not find winning move with two gaps", move);
	}

	@Test
	public void shouldFindWinningMoveForOpponentOnDiagonal() {
		// Test blocking scenario
		// Winning move at (3,3) needs support at (4,3)
		board.add(new Move("o", 0, 0));
		board.add(new Move("o", 1, 1));
		board.add(new Move("o", 2, 2));
		board.add(new Move("o", 4, 3)); // Support piece below (3,3)

		Move move = leftDiagonal.findWinningMove("o");
		assertNotNull("should find opponent's winning move", move);
		assertTrue("winning move should be at (3,3)", move.isInRow(3) && move.isInColumn(3));
	}

	@Test
	public void shouldHandleSmallerDiagonal() {
		// Test with a smaller diagonal (size 4) starting at (2,0)
		LeftToRightDiagonal smallDiagonal = new LeftToRightDiagonal(2, 0, 4, board, 4);

		// Pattern: XXX_ on positions (2,0), (3,1), (4,2), (5,3)
		board.add(new Move("x", 2, 0));
		board.add(new Move("x", 3, 1));
		board.add(new Move("x", 4, 2));

		Move move = smallDiagonal.findWinningMove("x");
		assertNotNull("should find winning move on smaller diagonal", move);
		assertTrue("winning move should be at (5,3)", move.isInRow(5) && move.isInColumn(3));
	}

}
