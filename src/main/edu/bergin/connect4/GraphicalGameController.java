package edu.bergin.connect4;

import javax.swing.*;

public class GraphicalGameController {

    public static void main(String[] args) {
        // Ensure GUI runs on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            startGame();
        });
    }

    private static boolean selectFirstPlayer(GraphicalView view, String player1Name, String player2Name) {
        String[] playerOptions = {player1Name, player2Name};
        int playerChoice = JOptionPane.showOptionDialog(
            view,
            "Who should go first?",
            "Connect 4 - First Player",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            playerOptions,
            playerOptions[0]
        );

        // Return true if first option selected, false if second option
        return (playerChoice == 0);
    }

    private static void startGame() {
        // Create the graphical view
        GraphicalView view = new GraphicalView();

        // Create the game
        BoardGame game = new Connect4(view);

        // Show game mode selection dialog
        String[] options = {"Human vs Computer", "Human vs Human", "Computer vs Computer"};
        int choice = JOptionPane.showOptionDialog(
            view,
            "Select game mode:",
            "Connect 4 - Game Mode",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        // Add players and determine first player based on selection
        String firstPlayer;

        switch (choice) {
            case 0: // Human vs Computer
                // Human is always "x" (red)
                game.addPlayer(new HumanPlayer("x", view));
                game.addPlayer(new TacticalStrategyPlayer("o"));
                // Ask who goes first
                boolean humanFirst = selectFirstPlayer(view, "Human (Red)", "Computer (Yellow)");
                firstPlayer = humanFirst ? "x" : "o";
                break;
            case 1: // Human vs Human
                // Player 1 is always "x" (red), Player 2 is always "o" (yellow)
                game.addPlayer(new HumanPlayer("x", view));
                game.addPlayer(new HumanPlayer("o", view));
                // Ask who goes first
                boolean player1First = selectFirstPlayer(view, "Player 1 (Red)", "Player 2 (Yellow)");
                firstPlayer = player1First ? "x" : "o";
                break;
            case 2: // Computer vs Computer
                game.addPlayer(new SimpleStrategyPlayer("x"));
                game.addPlayer(new TacticalStrategyPlayer("o"));
                firstPlayer = "x"; // Red always starts
                break;
            default:
                System.exit(0);
                return;
        }

        final String startingPlayer = firstPlayer;

        // Start the game in a separate thread to avoid blocking the GUI
        Thread gameThread = new Thread(() -> {
            try {
                game.play(startingPlayer);
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view,
                        "Game error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        });

        gameThread.start();
    }
}
