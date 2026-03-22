package edu.bergin.connect4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Unit test assertions against the Board interface

public class BoardTest {

	private Board theBoard;

	@Before
	public void setUp() throws Exception {
		theBoard = new Board(6, 7);
	}

	@Test
	public void canDetectAnInvalidMove() {
		theBoard.add(new Move("x", 5, 6));
		assertTrue("move should be flagged as valid", theBoard.isValidMove("o", 5, 0));
		assertFalse("move should be flagged as invalid", theBoard.isValidMove("x", 0, 6));
	}

	@Test
	public void canDetermineWhoseTurnItIs() {
		theBoard.add(new Move("x", 5, 6));
		theBoard.add(new Move("o", 5, 5));
		assertTrue("can determine whose turn it is", theBoard.isPlayersTurn("x"));
	}

	@Test
	public void canFindWinningMove() {
		theBoard.add(new Move("x", 5, 0));
		theBoard.add(new Move("x", 4, 0));
		theBoard.add(new Move("x", 3, 0));
		Move move = theBoard.findWinningMove("x");
		assertNotNull("should find a winning move in column 0", move);
		assertTrue("winning move should be (2,0)", move.isInRow(2) && move.isInColumn(0));
		theBoard.add(new Move("o", 5, 1));
		theBoard.add(new Move("o", 4, 1));
		theBoard.add(new Move("o", 3, 1));
		move = theBoard.findWinningMove("o");
		assertNotNull("should find a winning move in column 1", move);
		assertTrue("winning move should be (2,1)", move.isInRow(2) && move.isInColumn(1));
	}

	@Test
	public void canIdentifyPlayerAtOccupiedLocation() {
		theBoard.add(new Move("x", 5, 6));
		assertTrue("can identify player at occupied location", theBoard.takenBy("x", 5, 6));
	}

	@Test
	public void shouldHaveCorrectNumberOfDiagonals() {
		assertTrue("should have enough diagonals", theBoard.numberOfDiagonals() == 12);
	}

	// Enhanced tests for Board.isValidMove()

	@Test
	public void testIsValidMoveOnBottomRow() {
		// Bottom row moves are always valid (no support needed)
		assertTrue("bottom row move should be valid", theBoard.isValidMove("x", 5, 0));
		assertTrue("bottom row move should be valid", theBoard.isValidMove("x", 5, 3));
		assertTrue("bottom row move should be valid", theBoard.isValidMove("x", 5, 6));
	}

	@Test
	public void testIsValidMoveWithSupport() {
		// Place a piece at bottom
		theBoard.add(new Move("x", 5, 3));
		// Move with piece below should be valid
		assertTrue("move with support should be valid", theBoard.isValidMove("o", 4, 3));
	}

	@Test
	public void testIsValidMoveWithoutSupport() {
		// Try to place piece in mid-air (no support below)
		assertFalse("move without support should be invalid", theBoard.isValidMove("x", 3, 3));
		assertFalse("move without support should be invalid", theBoard.isValidMove("x", 0, 0));
	}

	@Test
	public void testIsValidMoveOnOccupiedSpace() {
		theBoard.add(new Move("x", 5, 3));
		// Cannot place on occupied position
		assertFalse("move on occupied space should be invalid", theBoard.isValidMove("o", 5, 3));
	}

	@Test
	public void testIsValidMoveStackingPieces() {
		// Build a stack in column 2
		theBoard.add(new Move("x", 5, 2));
		theBoard.add(new Move("o", 4, 2));
		theBoard.add(new Move("x", 3, 2));
		// Next piece should go on top
		assertTrue("stacking should be valid", theBoard.isValidMove("o", 2, 2));
		// Cannot skip a row
		assertFalse("cannot skip rows", theBoard.isValidMove("o", 1, 2));
	}

	// Enhanced tests for Board.isOver()

	@Test
	public void testIsOverWhenBoardFull() {
		// Fill all 42 positions (6 rows x 7 columns)
		for (int col = 0; col < 7; col++) {
			for (int row = 5; row >= 0; row--) {
				String player = ((row + col) % 2 == 0) ? "x" : "o";
				theBoard.add(new Move(player, row, col));
			}
		}
		assertTrue("board should be over when full", theBoard.isOver());
	}

	@Test
	public void testIsOverWhenVerticalWin() {
		// Create vertical win in column 0
		theBoard.add(new Move("x", 5, 0));
		theBoard.add(new Move("o", 5, 1));
		theBoard.add(new Move("x", 4, 0));
		theBoard.add(new Move("o", 5, 2));
		theBoard.add(new Move("x", 3, 0));
		theBoard.add(new Move("o", 5, 3));
		theBoard.add(new Move("x", 2, 0));
		assertTrue("game should be over with vertical win", theBoard.isOver());
	}

	@Test
	public void testIsOverWhenHorizontalWin() {
		// Create horizontal win in bottom row
		theBoard.add(new Move("x", 5, 0));
		theBoard.add(new Move("o", 4, 0));
		theBoard.add(new Move("x", 5, 1));
		theBoard.add(new Move("o", 4, 1));
		theBoard.add(new Move("x", 5, 2));
		theBoard.add(new Move("o", 4, 2));
		theBoard.add(new Move("x", 5, 3));
		assertTrue("game should be over with horizontal win", theBoard.isOver());
	}

	@Test
	public void testIsOverWhenDiagonalWin() {
		// Create diagonal win (bottom-left to top-right)
		theBoard.add(new Move("x", 5, 0));
		theBoard.add(new Move("o", 5, 1));
		theBoard.add(new Move("x", 4, 1));
		theBoard.add(new Move("o", 5, 2));
		theBoard.add(new Move("x", 4, 2));
		theBoard.add(new Move("o", 5, 3));
		theBoard.add(new Move("x", 3, 2));
		theBoard.add(new Move("o", 4, 3));
		theBoard.add(new Move("x", 3, 3));
		theBoard.add(new Move("o", 4, 0));
		theBoard.add(new Move("x", 2, 3));
		assertTrue("game should be over with diagonal win", theBoard.isOver());
	}

	@Test
	public void testIsNotOverWhenGameInProgress() {
		theBoard.add(new Move("x", 5, 0));
		theBoard.add(new Move("o", 5, 1));
		theBoard.add(new Move("x", 5, 2));
		assertFalse("game should not be over when in progress", theBoard.isOver());
	}
}
