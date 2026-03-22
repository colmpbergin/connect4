package edu.bergin.connect4;

public class Row extends CompletionSet {

	private int numberOfFieldsToWin;
	private int m_size;
	private int m_id;

	public Row(int id, int size, Board moves, int numberOfFieldsToWin) {
		super(moves);
		this.numberOfFieldsToWin = numberOfFieldsToWin;
		this.m_size = size;
		this.m_id = id;
	}

	public Move findWinningMove(String name) {
		Move m = null;

		// Loop thru each possible winning sequence for this row
		for (int sequenceStart = 0;
				sequenceStart <= m_size-numberOfFieldsToWin; sequenceStart++) {

			// Count pieces and track empty positions in this potential sequence
			int pieceCount = 0;
			int emptyCol = -1;
			int emptyCount = 0;

			for (int col = sequenceStart; col < sequenceStart + numberOfFieldsToWin; col++) {
				if (theBoard().takenBy(name, m_id, col)) {
					pieceCount++;
				} else if (!theBoard().taken(m_id, col)) {
					emptyCount++;
					emptyCol = col;
				} else {
					// Opponent's piece - this sequence is blocked
					break;
				}
			}

			// If we have 3 pieces and 1 empty spot, that's a potential winning move
			if (pieceCount == numberOfFieldsToWin - 1 && emptyCount == 1 && emptyCol >= 0) {
				if (theBoard().isValidMove(name, m_id, emptyCol)) {
					m = new Move(name, m_id, emptyCol);
					break;
				}
			}
		}
		return m;
	}

	// A row is "taken" if 4 horizontal adjacent slots are taken by the same player
	public boolean isTaken() {
		int sequenceCount = 0;
		String name = null;

		// Loop thru each possible winning sequence for this row
		for (int sequenceStart = 0;
				sequenceStart <= m_size-numberOfFieldsToWin; sequenceCount = 0, sequenceStart++) {

			// For this potential winning sequence, loop thru each location as long as the locations are
			// taken by the same player
			for (int col = sequenceStart; col < sequenceStart + numberOfFieldsToWin; col++) {
				if (sequenceCount == 0)
					name = theBoard().playerAt(m_id, col);
				if (theBoard().takenBy(name, m_id, col))
						sequenceCount++;
				else
					break;
			}
			if (sequenceCount == numberOfFieldsToWin) break;
		}

		return sequenceCount == numberOfFieldsToWin;
	}

	public int row() {
		return m_id;
	}
}
