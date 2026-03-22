package edu.bergin.connect4;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Unit test assertions against the Diagonal interface

public class DiagonalTest {

	private final int numberOfFieldsToWin = 4;
	private Board theBoard;

	@Before
	public void setUp() throws Exception {
		theBoard = new Board(6, 7);
	}

	// Test the diagonal at row 0, col 0, size 6, left-to-right, taking an interior sequence
	@Test
	public void shouldBeOverWhen006LtoRDiagonalTakenByPlayer() {
		LeftToRightDiagonal aLeftToRightDiagonal = new LeftToRightDiagonal(0, 0, 6, theBoard, 4);
		takeDiagonal(aLeftToRightDiagonal, 2, 2);
		assertTrue("should be over when a player has taken a diagonal", aLeftToRightDiagonal.isTaken());
	}

	// Test the diagonal at row 0, col 5, size 6, right-to-left
	@Test
	public void shouldBeOverWhen056RtoLDiagonalTakenByPlayer() {
		RightToLeftDiagonal aRightToLeftDiagonal = new RightToLeftDiagonal(0, 5, 6, theBoard, 4);
		takeDiagonal(aRightToLeftDiagonal, 0, 5);
		assertTrue("should be over when a player has 4 contiguous slots",
				aRightToLeftDiagonal.isTaken());
	}

	// Test the diagonal at row 1, col 6, size 5, right-to-left
	@Test
	public void shouldBeOverWhen165RtoLDiagonalTakenByPlayer() {
		RightToLeftDiagonal aRightToLeftDiagonal = new RightToLeftDiagonal(1, 6, 5, theBoard, 4);
		takeDiagonal(aRightToLeftDiagonal, 1, 6);
		assertTrue("should be over when a player has 4 contiguous slots",
				aRightToLeftDiagonal.isTaken());
	}

	// Test the diagonal at row 2, col 0, size 4, left-to-right
	@Test
	public void shouldBeOverWhen204LRDiagonalTakenByPlayer() {
		LeftToRightDiagonal aLeftToRightDiagonal = new LeftToRightDiagonal(2, 0, 4, theBoard, 4);
		takeDiagonal(aLeftToRightDiagonal, 2, 0);
		assertTrue("should be over when a player has taken a diagonal", aLeftToRightDiagonal.isTaken());
	}

	// Test the diagonal at row 0, col 0, size 6, left-to-right
	// Populate (0,0), (1,1), (2,2) and (4,3) so that winning move at (3,3) is valid
	@Test
	public void canFindWinningMoveAtEndOfALtoRDiagonal() {
		// Arrange:
		// - Create a Move below what will be the winning move
		// - Create the Move that should be the winner, for later comparison
		// - Create the 3 Moves on the same Diagonal that set up the winning move
		int row = 0;
		int col = 0;
		LeftToRightDiagonal aLeftToRightDiagonal = new LeftToRightDiagonal(row, col, 6, theBoard, 4);
		theBoard.add(new Move("x", row+4, col+3));
		Move theWinningMove = new Move("x", 3, 3);

		// Create 3 Moves on the same Diagonal
		for (int moveCount = 1; moveCount <= numberOfFieldsToWin-1;
				moveCount++, row = aLeftToRightDiagonal.nextRow(row), col = aLeftToRightDiagonal.nextCol(col)) {
			Move aMove = new Move("x", row, col);
			theBoard.add(aMove);
		}

		// Act
		Move move = aLeftToRightDiagonal.findWinningMove("x");

		//Assert
		String s = "failed to find winning move at " + theWinningMove.toString();
		assertNotNull(s, move);
		assertTrue(s, move.isInRow(3) && move.isInColumn(3));
	}

	// Test the diagonal at row 0, col 0, size 6, left-to-right
	// Populate (3,3), (4,4), (5,5) and (3,2) so that winning move at (2,2) is valid
	@Test
	public void canFindWinningMoveInALtoRDiagonal() {
		// Arrange:
		// - Create a Move below what will be the winning move
		// - Create the Move that should be the winner, for later comparison
		// - Create the 3 Moves on the same Diagonal that set up the winning move
		int row = 3;
		int col = 3;
		LeftToRightDiagonal aLeftToRightDiagonal = new LeftToRightDiagonal(0, 0, 6, theBoard, 4);
		theBoard.add(new Move("x", row, col-1));
		Move theWinningMove = new Move("x", row-1, col-1);

		for (int moveCount = 1; moveCount <= numberOfFieldsToWin-1;
				moveCount++, row = aLeftToRightDiagonal.nextRow(row), col = aLeftToRightDiagonal.nextCol(col)) {
			Move aMove = new Move("x", row, col);
			theBoard.add(aMove);
		}

		// Act
		Move move = aLeftToRightDiagonal.findWinningMove("x");

		//Assert
		String s = "failed to find winning move at " + theWinningMove.toString();
		assertNotNull(s, move);
		assertTrue(s, (move.getRow() == theWinningMove.getRow()) && (move.getColumn() == theWinningMove.getColumn()));
	}

	@Test
    public void detectsDiagonalIsTaken() {
        // Arrange
        Board board = new Board(6, 7);
        LeftToRightDiagonal diagonal = new LeftToRightDiagonal(0, 0, 6, board, 4);
		// Add pieces at (2,2), (3,3), (4,4), (5,5)
        board.add(new Move("x", 2, 2));
        board.add(new Move("x", 3, 3));
        board.add(new Move("x", 4, 4));
        board.add(new Move("x", 5, 5));

        // Act
        boolean result = diagonal.isTaken();

        // Assert
        assertTrue("Diagonal should be taken", result);
    }

	@Test
    public void testWinningMoveAtStartOfSequence() {
        Board board = new Board(6, 7);
        LeftToRightDiagonal diagonal = new LeftToRightDiagonal(0, 0, 6, board, 4);

        // Add pieces at (3,3), (4,4), (5,5)
        board.add(new Move("x", 3, 3));
        board.add(new Move("x", 4, 4));
        board.add(new Move("x", 5, 5));

        assertFalse("Should not be taken yet", diagonal.isTaken());

        // Now add the winning move at (2,2) to complete the sequence
        board.add(new Move("x", 2, 2));

        // This should now be a winning sequence: (2,2), (3,3), (4,4), (5,5)
        assertTrue("Should be taken after completing sequence at the start", diagonal.isTaken());
    }

    @Test
    public void testWinningMoveInMiddleOfSequence() {
        Board board = new Board(6, 7);
        LeftToRightDiagonal diagonal = new LeftToRightDiagonal(0, 0, 6, board, 4);

        // Add pieces at (2,2), (3,3), (5,5) - gap at (4,4)
        board.add(new Move("x", 2, 2));
        board.add(new Move("x", 3, 3));
        board.add(new Move("x", 5, 5));

        assertFalse("Should not be taken yet", diagonal.isTaken());

        // Now add the winning move at (4,4) to complete the sequence
        board.add(new Move("x", 4, 4));

        // This should now be a winning sequence: (2,2), (3,3), (4,4), (5,5)
        assertTrue("Should be taken after completing sequence in the middle", diagonal.isTaken());
    }

	// Create a winning sequence, starting at specified row and column
	// NOTE: no bounds checking
	private void takeDiagonal(Diagonal aDiagonal, int startRow, int startCol) {
		int row = startRow;
		int col = startCol;

		// Create 4 contiguous moves along the diagonal
		for (int moveCount = 1; moveCount <= numberOfFieldsToWin;
				moveCount++, row = aDiagonal.nextRow(row), col = aDiagonal.nextCol(col)) {
			Move aMove = new Move("x", row, col);
			theBoard.add(aMove);
		}
	}
}
