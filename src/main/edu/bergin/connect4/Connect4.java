package edu.bergin.connect4;

import java.util.*;

// Connect4 game; the rules....
//
// The game comprises a 6 row x 7 column board,
// A player must place 4 pieces in a row to win,
// Pieces may only be added from the top)
//
// See acceptance tests in Connect4Test for the rules of the game.

public class Connect4 implements BoardGame {

	public static final int defaultColumns = 7;
	public static final int defaultRows = 6;
	public static final int maxPlayers = 2;
	public static final int numberOfFieldsToWin = 4;
	private int numberOfRows, numberOfColumns;
	private final Board theBoard;
	private final View theView;
	private Map<String, Player> thePlayers = new HashMap<String, Player>();

	public Connect4(View view) {
		this(view, defaultRows, defaultColumns);
	}

	// TODO: Make this publicly usable if we want to allow configurable board size.
	// For now only the default size board is supported.
	private Connect4(View view, int rows, int cols) {

		// Create board
		theBoard = new Board(rows, cols);
		theView = view;
		numberOfRows = theBoard.rows();
		numberOfColumns = theBoard.columns();
	}

	public int addPlayer(Player p) {
		if (thePlayers.size() >= maxPlayers)
			throw new IllegalStateException("Can not have more than 2 players");
		thePlayers.put(p.name(), p);
		return thePlayers.size();
	}

	public boolean isOver() {
		return theBoard.isOver();
	}

	// Completes a move if valid
	// Throws if a move would not be valid
	public void move(String player, int row, int col) {
		Set<String> setOfPlayers = thePlayers.keySet();
		if (!setOfPlayers.contains(player)) {
			if (setOfPlayers.size() >= maxPlayers)
				throw new IllegalStateException("too many players!");
		}
		if (isOver())
			throw new IllegalStateException("game over!");
		if (!theBoard.isValidMove(player, row, col))
			throw new IllegalStateException("invalid move!");
		if (!theBoard.isPlayersTurn(player))
			throw new IllegalStateException("not player's turn!");

		theBoard.add(new Move(player, row, col));
	}

	public int numberOfCols() {
		return numberOfColumns;
	}

	public int numberOfMoves() {
		return theBoard.numberOfMoves();
	}

	public int numberOfRows() {
		return numberOfRows;
	}

	// Play and optionally, record the game
	// Returns: the number of moves in the recorded game; 0 if the game wasn't recorded
	public void play(String firstPlayer) {
		Player xPlayer = thePlayers.get("x");
		Player oPlayer = thePlayers.get("o");

		if (xPlayer == null || oPlayer == null || thePlayers.size() != 2)
			throw new IllegalStateException("Must have 2 players: x and o");

		if (firstPlayer == "x")
			xPlayer.takeTurn(theBoard);
		else
			oPlayer.takeTurn(theBoard);
		theView.update(theBoard);

		while (!isOver()) {
			if (theBoard.isPlayersTurn("x"))
				xPlayer.takeTurn(theBoard);
			else
				oPlayer.takeTurn(theBoard);
			theView.update(theBoard);
		}
		return;
	}

	// TODO Allow for insertion of different strategies for playing.
	public Set<String> players() {
		return thePlayers.keySet();
	}

}
