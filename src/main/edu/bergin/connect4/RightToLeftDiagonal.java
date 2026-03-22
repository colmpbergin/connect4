package edu.bergin.connect4;

// A RightToLeftDiagonal is identified by the tuple (start row, start column, size) with columns
// decreasing as rows increase.
public class RightToLeftDiagonal extends Diagonal {

  public RightToLeftDiagonal(int startRow, int startCol, int size, Board moves, int numberOfFieldsToWin) {
	    super(startRow, startCol, size, moves, numberOfFieldsToWin);
  }

  protected int nextCol(int col) {
    return --col;
  }

  protected int nextRow(int row) {
    return ++row;
  }

}
