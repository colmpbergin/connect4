# Connect4 Graphical User Interface

This project now includes a graphical front-end for the Connect4 game built with Java Swing.

## Features

- **Visual Game Board**: 6x7 grid with colored pieces (Red for X, Yellow for O)
- **Multiple Game Modes**:
  - Human vs Computer (AI)
  - Human vs Human
  - Computer vs Computer (for demonstration)
- **Interactive Gameplay**: Click column buttons to drop pieces
- **Game Status**: Real-time display of current player and game results
- **Win Detection**: Automatic detection of 4-in-a-row wins and draws

## How to Run

### Using Ant (Recommended)
```bash
# Compile and run the graphical version
ant run-gui

# Or compile and run the original console version
ant run
```

### Using Gradle
```bash
# Compile and run the graphical version
gradle runGui

# Or compile and run the original console version
gradle run
```

### Manual Compilation
```bash
# Compile all classes
javac -cp lib/junit-4.9b2.jar -d bin/classes src/main/edu/bergin/connect4/*.java

# Run the GUI version
java -cp bin/classes edu.bergin.connect4.GraphicalGameController

# Run the console version
java -cp bin/classes edu.bergin.connect4.GameController
```

## How to Play

1. **Launch the game** using `ant run-gui`
2. **Select game mode** from the dialog:
   - **Human vs Computer**: You play as Red (X) against the AI
   - **Human vs Human**: Two players take turns
   - **Computer vs Computer**: Watch AI players compete
3. **Make moves** by clicking the "Drop" buttons above each column
4. **Win condition**: Get 4 pieces in a row (horizontal, vertical, or diagonal)
5. **Game ends** when someone wins or the board is full (draw)

## Architecture

The GUI integrates seamlessly with the existing Connect4 architecture:

- **GraphicalView**: Implements the `View` interface to display the game board
- **HumanPlayer**: Extends `Player` class to handle user input from the GUI
- **GraphicalGameController**: Main entry point for the GUI version

The original console-based game remains fully functional and unchanged.

## Game Rules

- Standard Connect4 rules apply
- 6 rows × 7 columns board
- Pieces drop to the lowest available position in a column
- First player to get 4 pieces in a row wins
- Game ends in a draw if the board fills up without a winner

## Technical Details

- Built with Java Swing for cross-platform compatibility
- Follows the existing MVC pattern
- Thread-safe GUI updates using SwingUtilities
- Maintains all original game logic and AI strategies
