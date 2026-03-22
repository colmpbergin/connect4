package edu.bergin.connect4;

import java.util.ArrayList;
import java.util.Collection;

// A collection of Move objects; essentially this represents the game board.
// We can ask whether a particular move has already been made and by what player.
public class Moves {
	private Collection<Move> moves = new ArrayList<Move>();
	private Move previousMove;
	private int numberOfColumns, numberOfRows;

	// Public interface

	public Moves(int rows, int columns) {
		numberOfRows = rows;
		numberOfColumns = columns;
	}

	public void add(Move move) {
		moves.add(move);
		previousMove = move;
	}

	public boolean isPlayersTurn(String player) {
		return previousMove == null || !previousMove.player().equals(player);
	}

	// True is the specified Move is valid
	public boolean isValidMove(String p, int row, int col) {

		if (!isPlayersTurn(p))
			return false;

		if (playerAt(row, col) != null)
			return false;

		// In Connect4, pieces are dropped from the top to occupy the next available space in a column
		if ((row < numberOfRows - 1) && playerAt(row+1, col) == null)
			return false;

		return true;
	}

	public Collection<Move> moves() {
		return moves;
	}

	// Return the player that has taken [row,col], if any
	public String playerAt(int row, int col) {
		for (Move move : moves) {
			if (move.isInColumn(col) && move.isInRow(row))
				return move.player();
		}
		return null;
	}

	public int size() {
		return moves.size();
	}

	// True if [row,col] taken by player p
	public boolean takenBy(String p, int row, int col) {
		for (Move move : moves) {
			if (move.playedBy(p) && move.isInColumn(col) && move.isInRow(row))
				return true;
		}
		return false;
	}

	public int getRows() {
		return numberOfRows;
	}

	public int getColumns() {
		return numberOfColumns;
	}
}
