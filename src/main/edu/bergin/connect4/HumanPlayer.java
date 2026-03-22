package edu.bergin.connect4;

public class HumanPlayer extends Player {
    private GraphicalView view;
    private Board currentBoard;
    private volatile boolean moveReady = false;
    private volatile int selectedColumn = -1;

    public HumanPlayer(String name, GraphicalView view) {
        super(name);
        this.view = view;
        view.setHumanPlayer(this);
    }

    @Override
    public void takeTurn(Board theBoard) {
        currentBoard = theBoard;
        moveReady = false;
        selectedColumn = -1;

        // Throw if not this player's turn
        if (!theBoard.isPlayersTurn(m_Name))
            throw new IllegalStateException("Not player's turn");

        // Enable GUI input
        view.setWaitingForHumanMove(true);

        // Wait for user to make a move
        while (!moveReady) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        // Disable GUI input
        view.setWaitingForHumanMove(false);

        // Make the move
        if (selectedColumn >= 0) {
            // Find the lowest available row in the selected column
            for (int row = Board.defaultRows - 1; row >= 0; row--) {
                if (theBoard.isValidMove(m_Name, row, selectedColumn)) {
                    theBoard.add(new Move(m_Name, row, selectedColumn));
                    break;
                }
            }
        }
    }

    public void makeMove(int column) {
        if (currentBoard != null && !moveReady) {
            // Check if the column has space
            boolean hasSpace = false;
            for (int row = Board.defaultRows - 1; row >= 0; row--) {
                if (currentBoard.isValidMove(m_Name, row, column)) {
                    hasSpace = true;
                    break;
                }
            }

            if (hasSpace) {
                selectedColumn = column;
                moveReady = true;
            }
        }
    }
}
