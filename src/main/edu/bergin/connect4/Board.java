package edu.bergin.connect4;

import java.util.ArrayList;
import java.util.Collection;

// The game Board is a collection of Moves.

public class Board {
	public static final int defaultColumns = 7;
	public static final int defaultRows = 6;
	public static final int numberOfFieldsToWin = 4;
	private int numberOfRows, numberOfColumns, numberOfDiagonals;
	private final Collection<CompletionSet> completionSets = new ArrayList<CompletionSet>();
	private Collection<Move> moves = new ArrayList<Move>();
	private Move previousMove;

	private void create6x7Board() {
		// Create the diagonals -- only those that are large enough to accommodate a
		// winning move -- then count them
		completionSets.add(new LeftToRightDiagonal(0, 0, 6, this, numberOfFieldsToWin));
		completionSets.add(new LeftToRightDiagonal(0, 1, 6, this, numberOfFieldsToWin));
		completionSets.add(new LeftToRightDiagonal(0, 2, 5, this, numberOfFieldsToWin));
		completionSets.add(new LeftToRightDiagonal(0, 3, 4, this, numberOfFieldsToWin));
		completionSets.add(new LeftToRightDiagonal(1, 0, 5, this, numberOfFieldsToWin));
		completionSets.add(new LeftToRightDiagonal(2, 0, 4, this, numberOfFieldsToWin));

		completionSets.add(new RightToLeftDiagonal(0, 3, 4, this, numberOfFieldsToWin));
		completionSets.add(new RightToLeftDiagonal(0, 4, 5, this, numberOfFieldsToWin));
		completionSets.add(new RightToLeftDiagonal(0, 5, 6, this, numberOfFieldsToWin));
		completionSets.add(new RightToLeftDiagonal(0, 6, 6, this, numberOfFieldsToWin));
		completionSets.add(new RightToLeftDiagonal(1, 6, 5, this, numberOfFieldsToWin));
		completionSets.add(new RightToLeftDiagonal(2, 6, 4, this, numberOfFieldsToWin));

		numberOfDiagonals = completionSets.size();

		// Create the rows and columns
		for (int columnId = 0; columnId < numberOfColumns; columnId++)
			completionSets.add(new Column(columnId, numberOfRows, this, numberOfFieldsToWin));
		for (int rowId = 0; rowId < numberOfRows; rowId++)
			completionSets.add(new Row(rowId, numberOfColumns, this, numberOfFieldsToWin));
	}

	// Public interface

	public Board(int rows, int columns) {
		if (rows != defaultRows || columns != defaultColumns)
			throw new IllegalStateException("Only a 6x7 board is currently supported");
		numberOfRows = defaultRows;
		numberOfColumns = defaultColumns;
		create6x7Board();
	}

	public Board (Board original) {
		numberOfRows = original.rows();
		numberOfColumns = original.columns();
		create6x7Board();
		moves = original.moves();
	}

	public void add(Move move) {
		moves.add(move);
		previousMove = move;
	}

	public int columns() {
		return numberOfColumns;
	}

	public Move findWinningMove(String name) {
		Move m = null;
		for (CompletionSet completionSet : completionSets)
			if ((m = completionSet.findWinningMove(name)) != null)
				break;
		return m;
	}

	// The game is over if any row, column or diagonal is "taken" or if there are no more moves available.
	public boolean isOver() {
		for (CompletionSet completionSet : completionSets)
			if (completionSet.isTaken()) return true;
		return numberOfMoves() == numberOfColumns * numberOfRows;
	}

	public boolean isPlayersTurn(String player) {
		return previousMove == null || !previousMove.player().equals(player);
	}

	// The specified Move is valid if
	//	The space at the Move coordinates is not already occupied
	//	The coordinates are within the boundaries of the Board
	//	The row below the specified row is occupied, except in the case of the last row
	//
	// isValid() does NOT check for correct order of play, only for constraints on Board state.
	public boolean isValidMove(String p, int row, int col) {
		if (playerAt(row, col) != null)
			return false;

		if ((row < 0) || (row >= numberOfRows) || (col < 0) || (col >= numberOfColumns))
			return false;

		// In Connect4, pieces are dropped from the top to occupy the next available space in a column
		if ((row < numberOfRows - 1) && playerAt(row+1, col) == null)
			return false;

		return true;
	}

    /**
     * A cell is "gravity reachable right now" if every cell directly below it
     * (in the same column) is already occupied — meaning a piece dropped into
     * that column would land there.
     */
    final boolean isGravityReachable(int row, int col) {
        for (int r = row + 1; r < numberOfRows; r++) {
            if (playerAt(r, col) == null) return false;
        }
        return true;
    }

	public Collection<Move> moves() {
		return moves;
	}

	public int numberOfDiagonals() {
		return numberOfDiagonals;
	}

	// Return the player that has taken [row,col], if any
	public String playerAt(int row, int col) {
		for (Move move : moves) {
			if (move.isInColumn(col) && move.isInRow(row))
				return move.player();
		}
		return null;
	}

	public int rows() {
		return numberOfRows;
	}

	public int numberOfMoves() {
		return moves.size();
	}

	public boolean taken(int row, int col) {
		for (Move move : moves) {
			if (move.isInColumn(col) && move.isInRow(row))
				return true;
		}
		return false;
	}

	// True if [row,col] taken by player p
	public boolean takenBy(String p, int row, int col) {
		return (taken(row, col) && playerAt(row, col).equals(p));
	}

	/** The row index where a piece would land in {@code col}, or -1 if full. */
    public int landingRow(String name, int col) {
        for (int row = numberOfRows - 1; row >= 0; row--) {
            if (isValidMove(name, row, col)) return row;
        }
        return -1;
    }
}
