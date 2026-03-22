package edu.bergin.connect4;

public class SimpleStrategyPlayer extends Player {
	private final int thinkTimeMs;

	public SimpleStrategyPlayer(String name) {
		this(name, 1000); // Default 1 second for GUI
	}

	public SimpleStrategyPlayer(String name, int thinkTimeMs) {
		super(name);
		this.thinkTimeMs = thinkTimeMs;
	}

	// A simple strategy will at least check for a move that adds value
	@Override
	public void takeTurn(Board theBoard) {        // Throw if not this player's turn
        if (!theBoard.isPlayersTurn(m_Name))
            throw new IllegalStateException("Not player's turn");

		// Add think time before computer move (configurable)
		if (thinkTimeMs > 0) {
			try {
				Thread.sleep(thinkTimeMs);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		Move opening = randomOpeningMoveIfFirst(theBoard);
		if (opening != null) {
			theBoard.add(opening);
			return;
		}

		Move m;
		String otherName = m_Name == "x" ? "o" : "x";

		// Look for a winning move
		if ((m = theBoard.findWinningMove(m_Name)) != null) {
			theBoard.add(m);
			return;
		}

		// Look for a blocking move by looking for a winning move for opponent
		if ((m = theBoard.findWinningMove(otherName)) != null) {
			m.setPlayer(m_Name);
			theBoard.add(m);
			return;
		}

		// Try to build a sequence
		while (theBoard.isPlayersTurn(m_Name)) {

			// See if there's piece to build on top of
			// TODO Enhance to only select moves that might produce a winning sequence
			for (int col = 0; col < theBoard.columns(); col++) {
				for (int row = theBoard.rows()-1; row > 0; row--)
					if (theBoard.playerAt(row, col) == m_Name)
						if (theBoard.isValidMove(m_Name, row-1, col)) {
							theBoard.add(new Move(m_Name, row-1, col));
							return;
						}
			}

			// Pick next available column
			for (int col = 0; col < theBoard.columns(); col++) {
				for (int row = theBoard.rows()-1; row > 0; row--)
					if (theBoard.isValidMove(m_Name, row, col)) {
						theBoard.add(new Move(m_Name, row, col));
						return;
					}
			}
		}
	}

}
