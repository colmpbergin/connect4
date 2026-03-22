package edu.bergin.connect4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for gap-filling feature in Column.findWinningMove()
 */
public class GapFillingColumnTest {
	private Column column;
	private Board board;

	@Before
	public void setUp() throws Exception {
		board = new Board(6, 7);
		column = new Column(0, 6, board, 4);
	}

	@Test
	public void shouldFindWinningMoveAtEnd() {
		// Pattern: XXX_ (traditional case)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 4, 0));
		board.add(new Move("x", 3, 0));

		Move move = column.findWinningMove("x");
		assertNotNull("should find winning move at end of sequence", move);
		assertTrue("winning move should be at (2,0)", move.isInRow(2) && move.isInColumn(0));
	}

	@Test
	public void shouldFindWinningMoveInMiddleGap() {
		// Pattern: XX_X (gap in middle)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 4, 0));
		// Skip row 3
		board.add(new Move("x", 2, 0));

		Move move = column.findWinningMove("x");
		assertNotNull("should find winning move in gap", move);
		assertTrue("winning move should be at (3,0)", move.isInRow(3) && move.isInColumn(0));
	}

	@Test
	public void shouldFindWinningMoveAtStart() {
		// Pattern: _XXX (gap at start)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 4, 0));
		board.add(new Move("x", 3, 0));
		// Row 2 is empty but row 1 has a piece
		board.add(new Move("x", 1, 0));

		Move move = column.findWinningMove("x");
		assertNotNull("should find winning move at start", move);
		// Should find the gap at row 2 (between 3 and 1)
		assertTrue("winning move should be at (2,0)", move.isInRow(2) && move.isInColumn(0));
	}

	@Test
	public void shouldNotFindWinningMoveWhenOpponentBlocks() {
		// Pattern: XXoX (opponent blocks)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 4, 0));
		board.add(new Move("o", 3, 0));
		board.add(new Move("x", 2, 0));

		Move move = column.findWinningMove("x");
		assertNull("should not find winning move when opponent blocks", move);
	}

	@Test
	public void shouldNotFindWinningMoveWithTwoGaps() {
		// Pattern: X_X_ (two gaps)
		board.add(new Move("x", 5, 0));
		// Skip row 4
		board.add(new Move("x", 3, 0));
		// Skip row 2

		Move move = column.findWinningMove("x");
		assertNull("should not find winning move with two gaps", move);
	}

	@Test
	public void shouldFindWinningMoveForOpponent() {
		// Test blocking scenario - find opponent's winning move
		board.add(new Move("o", 5, 0));
		board.add(new Move("o", 4, 0));
		board.add(new Move("o", 3, 0));

		Move move = column.findWinningMove("o");
		assertNotNull("should find opponent's winning move", move);
		assertTrue("winning move should be at (2,0)", move.isInRow(2) && move.isInColumn(0));
	}

	@Test
	public void shouldFindFirstWinningMoveWhenMultipleExist() {
		// Create a scenario with multiple potential wins
		// Bottom sequence: XXX_ at rows 5,4,3,2
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 4, 0));
		board.add(new Move("x", 3, 0));

		Move move = column.findWinningMove("x");
		assertNotNull("should find a winning move", move);
		// Should find the first one (starting from top)
		assertTrue("should find winning move at (2,0)", move.isInRow(2) && move.isInColumn(0));
	}
}
