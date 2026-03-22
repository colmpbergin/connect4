package edu.bergin.connect4;

public abstract class Diagonal extends CompletionSet {
  private final int numberOfFieldsToWin;
  private final int size;
  private final int startCol;
  private final int startRow;

  public Diagonal(int startRow, int startCol, int size, Board moves, int numberOfFieldsToWin) {
	super(moves);
    this.numberOfFieldsToWin = numberOfFieldsToWin;
    this.startRow = startRow;
    this.startCol = startCol;
    this.size = size;
  }

  public int size() {
	  return size;
  }

  public int startCol() {
	  return startCol;
  }

  public int startRow() {
	  return startRow;
  }

  public Move findWinningMove(String name) {
	  Move m = null;

	  // Loop thru each possible winning sequence for this diagonal
	  for (int sequenceOffset = 0, outerRow = startRow, outerCol = startCol;
			  sequenceOffset <= size-numberOfFieldsToWin;
			  sequenceOffset++, outerRow = nextRow(outerRow), outerCol = nextCol(outerCol)) {

		  // Count pieces and track empty positions in this potential sequence
		  int pieceCount = 0;
		  int emptyRow = -1;
		  int emptyCol = -1;
		  int emptyCount = 0;

		  int row = outerRow;
		  int col = outerCol;
		  for (int i = 0; i < numberOfFieldsToWin; i++) {
			  if (theBoard().takenBy(name, row, col)) {
				  pieceCount++;
			  } else if (!theBoard().taken(row, col)) {
				  emptyCount++;
				  emptyRow = row;
				  emptyCol = col;
			  } else {
				  // Opponent's piece - this sequence is blocked
				  break;
			  }
			  row = nextRow(row);
			  col = nextCol(col);
		  }

		  // If we have 3 pieces and 1 empty spot, that's a potential winning move
		  if (pieceCount == numberOfFieldsToWin - 1 && emptyCount == 1 && emptyRow >= 0) {
			if (theBoard().isValidMove(name, emptyRow, emptyCol)) {
				m = new Move(name, emptyRow, emptyCol);
				break;
			}
		  }
	  }
	  return m;
  }

  public boolean isTaken() {
		int sequenceCount = 0;
		String name = null;

		// Loop thru each possible winning sequence for this diagonal
		for (int sequenceOffset = 0, outerRow = startRow, outerCol = startCol;
				sequenceOffset <= size-numberOfFieldsToWin;
				sequenceOffset++, outerRow = nextRow(outerRow), outerCol = nextCol(outerCol)) {

			// For this potential winning sequence, loop thru each location as long as the locations are
			// taken by the same player
			for (int row = outerRow, col = outerCol; sequenceCount < numberOfFieldsToWin;
					row = nextRow(row), col = nextCol(col)) {
				if (sequenceCount == 0)
					name = theBoard().playerAt(row, col);
				if (theBoard().takenBy(name, row, col))
					sequenceCount++;
				else
					// The sequence is broken; move to the next one
					break;
			}

			if (sequenceCount == numberOfFieldsToWin)
				break;
			else
				sequenceCount = 0;
		}

		return sequenceCount == numberOfFieldsToWin;
  }

  protected abstract int nextCol(int col);

  protected abstract int nextRow(int row);
}
