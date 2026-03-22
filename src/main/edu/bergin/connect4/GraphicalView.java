package edu.bergin.connect4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphicalView extends JFrame implements View {
    private static final int CELL_SIZE = 80;
    private static final Color EMPTY_COLOR = Color.WHITE;
    private static final Color PLAYER_X_COLOR = Color.RED;
    private static final Color PLAYER_O_COLOR = Color.YELLOW;

    private JPanel boardPanel;
    private JButton[][] cells;
    private JButton[] columnButtons;
    private JLabel statusLabel;
    private Board currentBoard;
    private HumanPlayer humanPlayer;
    private boolean waitingForHumanMove = false;

    public GraphicalView() {
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Connect 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Status panel
        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Welcome to Connect 4!");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Column buttons for dropping pieces
        JPanel buttonPanel = new JPanel(new GridLayout(1, Board.defaultColumns));
        columnButtons = new JButton[Board.defaultColumns];
        for (int col = 0; col < Board.defaultColumns; col++) {
            final int column = col;
            columnButtons[col] = new JButton("Drop");
            columnButtons[col].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (waitingForHumanMove && humanPlayer != null) {
                        humanPlayer.makeMove(column);
                    }
                }
            });
            buttonPanel.add(columnButtons[col]);
        }
        add(buttonPanel, BorderLayout.CENTER);

        // Board panel
        boardPanel = new JPanel(new GridLayout(Board.defaultRows, Board.defaultColumns));
        cells = new JButton[Board.defaultRows][Board.defaultColumns];

        for (int row = 0; row < Board.defaultRows; row++) {
            for (int col = 0; col < Board.defaultColumns; col++) {
                cells[row][col] = new JButton();
                cells[row][col].setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                cells[row][col].setBackground(EMPTY_COLOR);
                cells[row][col].setOpaque(true);
                cells[row][col].setContentAreaFilled(true);
                cells[row][col].setBorderPainted(true);
                cells[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                cells[row][col].setEnabled(false);
                boardPanel.add(cells[row][col]);
            }
        }
        add(boardPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void update(Board board) {
        currentBoard = board;
        SwingUtilities.invokeLater(() -> {
            updateBoardDisplay();
            updateStatus();
        });
    }

    private void updateBoardDisplay() {
        if (currentBoard == null) return;

        // Create a map to track move numbers at each position
        int[][] moveNumbers = new int[Board.defaultRows][Board.defaultColumns];

        // Initialize all positions to 0 (no move)
        for (int row = 0; row < Board.defaultRows; row++) {
            for (int col = 0; col < Board.defaultColumns; col++) {
                moveNumbers[row][col] = 0;
            }
        }

        // Iterate through moves and assign numbers based on their actual position
        int moveNumber = 1;
        for (Move move : currentBoard.moves()) {
            for (int row = 0; row < Board.defaultRows; row++) {
                for (int col = 0; col < Board.defaultColumns; col++) {
                    if (move.isInRow(row) && move.isInColumn(col)) {
                        moveNumbers[row][col] = moveNumber;
                    }
                }
            }
            moveNumber++;
        }

        // Update the display
        for (int row = 0; row < Board.defaultRows; row++) {
            for (int col = 0; col < Board.defaultColumns; col++) {
                String player = currentBoard.playerAt(row, col);

                if (player != null) {
                    // Set background color
                    if (player.equals("x")) {
                        cells[row][col].setBackground(PLAYER_X_COLOR);
                        cells[row][col].setForeground(Color.WHITE);
                    } else if (player.equals("o")) {
                        cells[row][col].setBackground(PLAYER_O_COLOR);
                        cells[row][col].setForeground(Color.BLACK);
                    }

                    // Set move number text
                    if (moveNumbers[row][col] > 0) {
                        cells[row][col].setText(String.valueOf(moveNumbers[row][col]));
                        cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                    }
                } else {
                    // Empty cell
                    cells[row][col].setBackground(EMPTY_COLOR);
                    cells[row][col].setText("");
                }
            }
        }
    }

    private void updateStatus() {
        if (currentBoard == null) {
            statusLabel.setText("Welcome to Connect 4!");
            return;
        }

        if (currentBoard.isOver()) {
            if (currentBoard.numberOfMoves() == currentBoard.columns() * currentBoard.rows()) {
                statusLabel.setText("Game Over - It's a Draw!");
            } else {
                String winner = currentBoard.isPlayersTurn("x") ? "o" : "x";
                String winnerName = winner.equals("x") ? "Red" : "Yellow";
                statusLabel.setText("Game Over - " + winnerName + " Wins!");
            }
            setColumnButtonsEnabled(false);
        } else {
            String currentPlayer = currentBoard.isPlayersTurn("x") ? "x" : "o";
            String playerName = currentPlayer.equals("x") ? "Red" : "Yellow";
            statusLabel.setText(playerName + "'s turn");
        }
    }

    public void setHumanPlayer(HumanPlayer player) {
        this.humanPlayer = player;
    }

    public void setWaitingForHumanMove(boolean waiting) {
        this.waitingForHumanMove = waiting;
        SwingUtilities.invokeLater(() -> {
            setColumnButtonsEnabled(waiting && (currentBoard == null || !currentBoard.isOver()));
        });
    }

    private void setColumnButtonsEnabled(boolean enabled) {
        for (JButton button : columnButtons) {
            button.setEnabled(enabled);
        }
    }
}
