package edu.bergin.connect4;

public class GameController {

	/**
	 * A simplistic view/presentation layer
	 */
	public static void main(String[] args) {

		// Play a game of Connect4
		ConsoleView console = new ConsoleView();
		BoardGame game = new Connect4(console);
		game.addPlayer(new SimpleStrategyPlayer("x"));
		game.addPlayer(new SimpleStrategyPlayer("o"));
		game.play("x");
	}
}
