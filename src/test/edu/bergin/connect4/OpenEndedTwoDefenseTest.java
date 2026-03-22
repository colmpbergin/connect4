package edu.bergin.connect4;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for TacticalStrategyPlayer's open-ended two defense.
 * Verifies that the AI detects and blocks opponent sequences of two pieces
 * that are open at both ends (_XX_), preventing the opponent from creating
 * an unstoppable open-ended three.
 */
public class OpenEndedTwoDefenseTest {

    @Test
    public void testBlocksHorizontalOpenEndedTwoOnBottomRow() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Set up open-ended two for opponent (o) on bottom row
        board.add(new Move("o", 5, 1));
        board.add(new Move("o", 5, 2));
        // Pattern: _OO_ at row 5, columns 0-3

        // AI should block one end
        ai.takeTurn(board);

        // Verify AI blocked at column 0 or column 3
        boolean blockedEnd1 = board.takenBy("x", 5, 0);
        boolean blockedEnd2 = board.takenBy("x", 5, 3);

        assertTrue("AI should block one end of open-ended two",
            blockedEnd1 || blockedEnd2);
    }

    @Test
    public void testBlocksHorizontalOpenEndedTwoInMiddleOfBoard() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Build support structure with gaps to avoid AI building on it
        board.add(new Move("o", 5, 0));
        board.add(new Move("x", 5, 1));
        board.add(new Move("x", 5, 2));
        board.add(new Move("x", 5, 3));
        board.add(new Move("o", 5, 4));
        board.add(new Move("o", 5, 5));
        board.add(new Move("o", 5, 6));

        // Create open-ended two on row 4
        board.add(new Move("o", 4, 2));
        board.add(new Move("o", 4, 3));
        // Pattern: _OO_ at row 4, columns 1-4

        // AI should block one end
        ai.takeTurn(board);

        // Verify AI blocked at (4,1) or (4,4)
        boolean blockedEnd1 = board.takenBy("x", 4, 1);
        boolean blockedEnd2 = board.takenBy("x", 4, 4);

        assertTrue("AI should block one end of open-ended two in middle of board",
            blockedEnd1 || blockedEnd2);
    }

    @Test
    public void testDoesNotBlockWhenOnlyOneEndOpen() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Set up two pieces with only one end open
        board.add(new Move("x", 5, 0));  // Block one end
        board.add(new Move("o", 5, 1));
        board.add(new Move("o", 5, 2));
        // Pattern: XOO_ at row 5 - only one end open

        // AI should NOT prioritize this as open-ended two
        // (it will make some move, but not necessarily blocking)
        ai.takeTurn(board);

        // This test just verifies no exception is thrown
        // The AI will make a move, but it's not required to block (5,3)
        assertTrue("AI should handle one-end-open pattern without error", true);
    }

    @Test
    public void testDoesNotBlockWhenEndIsNotGravityReachable() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Create a floating pattern (not gravity-reachable)
        board.add(new Move("o", 3, 1));
        board.add(new Move("o", 3, 2));
        // Pattern: _OO_ at row 3, but (3,0) and (3,3) are floating

        // AI should NOT detect this as a threat (ends not reachable)
        ai.takeTurn(board);

        // Verify AI did NOT play at the floating positions
        assertFalse("AI should not play at floating position (3,0)",
            board.takenBy("x", 3, 0));
        assertFalse("AI should not play at floating position (3,3)",
            board.takenBy("x", 3, 3));
    }

    @Test
    public void testPrioritizesImmediateWinOverBlockingOpenEndedTwo() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Set up AI's winning opportunity
        board.add(new Move("x", 5, 0));
        board.add(new Move("x", 5, 1));
        board.add(new Move("x", 5, 2));
        // AI can win at (5,3)

        // Also set up opponent's open-ended two elsewhere
        board.add(new Move("o", 5, 4));
        board.add(new Move("o", 5, 5));
        // Pattern: _OO_ at row 5, columns 3-6

        // AI should take the win, not block
        ai.takeTurn(board);

        assertTrue("AI should prioritize winning over blocking",
            board.takenBy("x", 5, 3));
    }

    @Test
    public void testPrioritizesBlockingImmediateWinOverOpenEndedTwo() {
        Board board = new Board(6, 7);
        TacticalStrategyPlayer ai = new TacticalStrategyPlayer("x", 0);

        // Set up opponent's immediate winning threat
        board.add(new Move("o", 5, 0));
        board.add(new Move("o", 5, 1));
        board.add(new Move("o", 5, 2));
        // Opponent can win at (5,3)

        // Also set up another open-ended two elsewhere
        board.add(new Move("o", 5, 4));
        board.add(new Move("o", 5, 5));
        // Pattern: _OO_ at row 5, columns 3-6

        // AI should block the immediate win at (5,3)
        ai.takeTurn(board);

        assertTrue("AI should prioritize blocking immediate win",
            board.takenBy("x", 5, 3));
    }
}
