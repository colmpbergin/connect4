package edu.bergin.connect4;

public class Column extends CompletionSet {

	private int numberOfFieldsToWin;
	private int m_size;
	private int m_id;

	public Column(int id, int size, Board moves, int numberOfFieldsToWin) {
		super(moves);
		this.numberOfFieldsToWin = numberOfFieldsToWin;
		this.m_size = size;
		this.m_id = id;
	}

	public int column() {
		return m_id;
	}

	public Move findWinningMove(String name) {
		Move m = null;

		// Loop thru each possible winning sequence for this column, starting with the highest row
		for (int sequenceStart = m_size-1;
				sequenceStart >= numberOfFieldsToWin-1; sequenceStart--) {

			// Count pieces and track empty positions in this potential sequence
			int pieceCount = 0;
			int emptyRow = -1;
			int emptyCount = 0;

			for (int row = sequenceStart; row > sequenceStart - numberOfFieldsToWin; row--) {
				if (theBoard().takenBy(name, row, m_id)) {
					pieceCount++;
				} else if (!theBoard().taken(row, m_id)) {
					emptyCount++;
					emptyRow = row;
				} else {
					// Opponent's piece - this sequence is blocked
					break;
				}
			}

			// If we have 3 pieces and 1 empty spot, that's a potential winning move
			if (pieceCount == numberOfFieldsToWin - 1 && emptyCount == 1 && emptyRow >= 0) {
				if (theBoard().isValidMove(name, emptyRow, m_id)) {
					m = new Move(name, emptyRow, m_id);
					break;
				}
			}
		}
		return m;
	}

	// A column is "taken" if 4 vertical adjacent slots are taken by the same player
	public boolean isTaken() {
		int sequenceCount = 0;
		String name = null;

		for (int sequenceStart = 0;
				sequenceStart <= m_size-numberOfFieldsToWin; sequenceCount = 0, sequenceStart++) {
			for (int row = sequenceStart; row < sequenceStart + numberOfFieldsToWin; row++) {
				if (sequenceCount == 0)
					name = theBoard().playerAt(row, m_id);
				if (theBoard().takenBy(name, row, m_id))
						sequenceCount++;
				else
					break;
			}
			if (sequenceCount == numberOfFieldsToWin) break;
		}

		return sequenceCount == numberOfFieldsToWin;
	}
}
