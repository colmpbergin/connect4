package edu.bergin.connect4;

import java.io.IOException;

public class ConsoleView implements View {

	@Override
	public void update(Board board) {
		final char ESC = '\u001B';
		final String CLEAR_SCREEN = ESC + "[2J";

		System.out.println("Press RETURN to continue....");
		System.out.flush();

		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.print(CLEAR_SCREEN);
		for (int row = 0; row < board.rows(); row++)
			for (int col = 0; col < board.columns(); col++) {
				if (col == 0)
					System.out.println();
				System.out.print((board.playerAt(row, col) != null ? board
						.playerAt(row, col) : "_") + " ");
			}
		System.out.println();

		if (board.isOver())
			if (board.numberOfMoves() == board.columns() * board.rows())
				System.out.println("Draw!");
			else
				System.out.println("The winner is "
						+ (board.isPlayersTurn("x") ? "o" : "x"));

	}

}
