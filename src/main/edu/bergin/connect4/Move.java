package edu.bergin.connect4;

// A data class with trivial getters and setters; no unit tests.
public class Move {
  private String player;
  private int row;
  private int column;

  public Move(String player, int row, int col) {
    this.player = player;
    this.row = row;
    this.column = col;
  }

  public boolean isInColumn(int column) {
    return this.column == column;
  }

  public boolean isInRow(int row) {
    return this.row == row;
  }

  public boolean playedBy(String player) {
    return this.player.equals(player);
  }

  public String player() {
    return player;
  }

  public void setPlayer(String s) {
	  player = s;
  }

  public String toString() {
    return player + ":(" + row + "," + column + ")";
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

}
