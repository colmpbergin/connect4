package edu.bergin.connect4;

public abstract class CompletionSet {
	private Board m_Moves;

	public CompletionSet(Board moves) {
		this.m_Moves = moves;
	}

	public abstract Move findWinningMove(String name);

	public abstract boolean isTaken();

	protected Board theBoard() {
		return m_Moves;
	}
}
