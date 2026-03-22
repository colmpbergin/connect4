package edu.bergin.connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Player implements IPlayer {

	protected String m_Name;

	public Player(String name) {
		m_Name = name;
	}

	public String name() {
		return m_Name;
	}

	@Override
	public abstract void takeTurn(Board theMoves);


    final String opponentName() {
        return m_Name.equals("x") ? "o" : "x";
    }

    /**
     * For the very first move of the game, pick a random legal move from the set of
     * currently available (gravity-reachable) moves.
     *
     * @return a random opening move, or null if this is not the first move
     */
    final Move randomOpeningMoveIfFirst(Board theBoard) {
        if (theBoard.numberOfMoves() != 0) return null;

        List<Move> candidates = new ArrayList<>();
        for (int col = 0; col < theBoard.columns(); col++) {
            int row = theBoard.landingRow(m_Name, col);
            if (row >= 0) {
                candidates.add(new Move(m_Name, row, col));
            }
        }
        if (candidates.isEmpty()) return null;

        int idx = ThreadLocalRandom.current().nextInt(candidates.size());
        return candidates.get(idx);
    }

}
