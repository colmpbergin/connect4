package edu.bergin.connect4;


// A LeftToRightDiagonal is identified by the tuple (start row, start column, size) with columns
// increasing as rows increase.
public class LeftToRightDiagonal extends Diagonal {

  public LeftToRightDiagonal(int startRow, int startCol, int size, Board moves, int numberOfFieldsToWin) {
    super(startRow, startCol, size, moves, numberOfFieldsToWin);
  }

  protected int nextCol(int col) {
    return ++col;
  }

  protected int nextRow(int row) {
    return ++row;
  }

}
