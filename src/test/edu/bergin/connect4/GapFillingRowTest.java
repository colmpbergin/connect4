package edu.bergin.connect4;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for gap-filling feature in Row.findWinningMove()
 */
public class GapFillingRowTest {
	private Row row;
	private Board board;

	@Before
	public void setUp() throws Exception {
		board = new Board(6, 7);
		row = new Row(5, 7, board, 4); // Bottom row
	}

	@Test
	public void shouldFindWinningMoveAtEnd() {
		// Pattern: XXX_ (traditional case)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 5, 1));
		board.add(new Move("x", 5, 2));

		Move move = row.findWinningMove("x");
		assertNotNull("should find winning move at end of sequence", move);
		assertTrue("winning move should be at (5,3)", move.isInRow(5) && move.isInColumn(3));
	}

	@Test
	public void shouldFindWinningMoveInMiddleGap() {
		// Pattern: XX_X (gap in middle)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 5, 1));
		// Skip column 2
		board.add(new Move("x", 5, 3));

		Move move = row.findWinningMove("x");
		assertNotNull("should find winning move in gap", move);
		assertTrue("winning move should be at (5,2)", move.isInRow(5) && move.isInColumn(2));
	}

	@Test
	public void shouldFindWinningMoveAtStart() {
		// Pattern: _XXX (gap at start)
		// Skip column 0
		board.add(new Move("x", 5, 1));
		board.add(new Move("x", 5, 2));
		board.add(new Move("x", 5, 3));

		Move move = row.findWinningMove("x");
		assertNotNull("should find winning move at start", move);
		assertTrue("winning move should be at (5,0)", move.isInRow(5) && move.isInColumn(0));
	}

	@Test
	public void shouldNotFindWinningMoveWhenOpponentBlocks() {
		// Pattern: XXoX (opponent blocks)
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 5, 1));
		board.add(new Move("o", 5, 2));
		board.add(new Move("x", 5, 3));

		Move move = row.findWinningMove("x");
		assertNull("should not find winning move when opponent blocks", move);
	}

	@Test
	public void shouldNotFindWinningMoveWithTwoGaps() {
		// Pattern: X_X_ (two gaps)
		board.add(new Move("x", 5, 0));
		// Skip column 1
		board.add(new Move("x", 5, 2));
		// Skip column 3

		Move move = row.findWinningMove("x");
		assertNull("should not find winning move with two gaps", move);
	}

	@Test
	public void shouldFindWinningMoveForOpponent() {
		// Test blocking scenario
		board.add(new Move("o", 5, 0));
		board.add(new Move("o", 5, 1));
		board.add(new Move("o", 5, 2));

		Move move = row.findWinningMove("o");
		assertNotNull("should find opponent's winning move", move);
		assertTrue("winning move should be at (5,3)", move.isInRow(5) && move.isInColumn(3));
	}

	@Test
	public void shouldHandleMultipleSequencesInRow() {
		// Create two separate sequences in the same row
		board.add(new Move("x", 5, 0));
		board.add(new Move("x", 5, 1));
		board.add(new Move("x", 5, 2));
		// Gap at column 3
		board.add(new Move("o", 5, 4)); // Opponent blocks second sequence

		Move move = row.findWinningMove("x");
		assertNotNull("should find winning move in first sequence", move);
		assertTrue("winning move should be at (5,3)", move.isInRow(5) && move.isInColumn(3));
	}

	@Test
	public void shouldFindGapInDifferentPosition() {
		// Pattern: X_XX (gap in second position)
		board.add(new Move("x", 5, 0));
		// Skip column 1
		board.add(new Move("x", 5, 2));
		board.add(new Move("x", 5, 3));

		Move move = row.findWinningMove("x");
		assertNotNull("should find winning move in gap", move);
		assertTrue("winning move should be at (5,1)", move.isInRow(5) && move.isInColumn(1));
	}
}
