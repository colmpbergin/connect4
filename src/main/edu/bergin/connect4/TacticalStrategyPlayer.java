package edu.bergin.connect4;

/**
 * TacticalStrategyPlayer — an enhanced AI that extends SimpleStrategyPlayer's logic with:
 *
 *  1. One-move lookahead: after placing a piece, simulate the opponent's best
 *     response and discard any move that hands them an immediate win.
 *
 *  2. Dead-end pruning: skip columns that are provably unable to contribute to
 *     any four-in-a-row (e.g. the cell and its neighbours are already blocked
 *     by the opponent in every direction).
 *
 *  3. Gap-trap setup: actively seek moves that create a "broken" three-in-a-row
 *     where the winning fourth cell is an interior gap that gravity does NOT
 *     yet fill — a sequence the opponent is likely to overlook.
 *
 * Priority order (highest → lowest):
 *   a) Take an immediate win
 *   b) Block an immediate opponent win
 *   c) Play a gap-trap move (creates a sequence whose winning cell is an interior gap)
 *   d) Play the highest-scoring "build" move that passes the lookahead safety check
 *   e) Fall back to the first safe valid move
 */
public class TacticalStrategyPlayer extends Player {

    private final int thinkTimeMs;

    // -----------------------------------------------------------------------
    // Construction
    // -----------------------------------------------------------------------

    public TacticalStrategyPlayer(String name) {
        this(name, 1000);
    }

    public TacticalStrategyPlayer(String name, int thinkTimeMs) {
        super(name);
        this.thinkTimeMs = thinkTimeMs;
    }

    // -----------------------------------------------------------------------
    // Main turn logic
    // -----------------------------------------------------------------------

    @Override
    public void takeTurn(Board theBoard) {
        if (!theBoard.isPlayersTurn(m_Name))
            throw new IllegalStateException("Not player's turn");

        if (thinkTimeMs > 0) {
            try {
                Thread.sleep(thinkTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Move opening = randomOpeningMoveIfFirst(theBoard);
        if (opening != null) {
            theBoard.add(opening);
            return;
        }

        String opponent = opponentName();

        // --- (a) Immediate win ---
        Move win = theBoard.findWinningMove(m_Name);
        if (win != null) {
            theBoard.add(win);
            return;
        }

        // --- (b) Block immediate opponent win ---
        Move block = theBoard.findWinningMove(opponent);
        if (block != null) {
            block.setPlayer(m_Name);
            theBoard.add(block);
            return;
        }

        // --- (b2) Block opponent's open-ended two (_XX_), which would otherwise lead to an open-ended three and a guaranteed win two turns later ---
        Move blockOpenEndedTwo = blockOpenEndedTwoMove(theBoard, opponent);
        if (blockOpenEndedTwo != null) {
            theBoard.add(blockOpenEndedTwo);
            return;
        }

        // --- (c) Gap-trap: set up a broken-three where the winning cell is an interior gap ---
        Move trap = findGapTrapMove(theBoard, opponent);
        if (trap != null) {
            theBoard.add(trap);
            return;
        }

        // --- (d) Best "build" move that survives the lookahead safety check ---
        Move build = findBestSafeBuildMove(theBoard, opponent);
        if (build != null) {
            theBoard.add(build);
            return;
        }

        // --- (e) Any safe valid move (last resort) ---
        Move safe = findAnySafeMove(theBoard, opponent);
        if (safe != null) {
            theBoard.add(safe);
            return;
        }

        // Absolute fallback — board is nearly full, take whatever is valid
        for (int col = 0; col < theBoard.columns(); col++) {
            for (int row = theBoard.rows() - 1; row >= 0; row--) {
                if (theBoard.isValidMove(m_Name, row, col)) {
                    theBoard.add(new Move(m_Name, row, col));
                    return;
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Gap-trap detection
    // -----------------------------------------------------------------------

    /**
     * Scan every valid candidate cell and check whether placing there would
     * create a "broken three" — three of our pieces in a window of four that
     * contains exactly one empty interior gap.  An interior gap is any empty
     * cell that is NOT the landing cell we just played (i.e. it lives somewhere
     * in the middle of the sequence and cannot be filled by gravity right now).
     * That gap is the hidden winning threat the opponent may miss.
     *
     * We also require the resulting position to be safe (opponent cannot win
     * immediately on their next move).
     */
    private Move findGapTrapMove(Board theBoard, String opponent) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();

        // All four-cell direction vectors: horizontal, vertical, two diagonals
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int col = 0; col < cols; col++) {
            int landingRow = theBoard.landingRow(m_Name, col);
            if (landingRow < 0) continue;                 // column full

            // Dead-end pruning: skip if this cell can't contribute to any win
            if (!cellHasWinPotential(theBoard, landingRow, col, m_Name)) continue;

            for (int[] d : dirs) {
                int dr = d[0], dc = d[1];

                // Examine every window of 4 that contains (landingRow, col)
                for (int offset = -3; offset <= 0; offset++) {
                    int sr = landingRow + offset * dr;
                    int sc = col + offset * dc;

                    // Count our pieces, opponent pieces, and empty cells in this window
                    int mine = 0, theirs = 0, emptyCount = 0;
                    boolean windowValid = true;
                    int interiorGapRow = -1, interiorGapCol = -1;

                    for (int k = 0; k < 4; k++) {
                        int r = sr + k * dr;
                        int c = sc + k * dc;
                        if (r < 0 || r >= rows || c < 0 || c >= cols) {
                            windowValid = false;
                            break;
                        }
                        String occupant = theBoard.playerAt(r, c);
                        if (occupant == null) {
                            emptyCount++;
                            // Record empty cells that are NOT the cell we are about to play
                            // (those are potential interior gaps)
                            if (!(r == landingRow && c == col)) {
                                interiorGapRow = r;
                                interiorGapCol = c;
                            }
                        } else if (occupant.equals(m_Name)) {
                            mine++;
                        } else {
                            theirs++;
                        }
                    }

                    if (!windowValid || theirs > 0) continue;

                    // After placing at (landingRow, col) we would have mine+1 pieces and
                    // emptyCount-1 empty cells in this window.
                    int newMine = mine + 1;
                    int newEmpty = emptyCount - 1;

                    // We want exactly 3 of ours and 1 empty interior gap
                    if (newMine == 3 && newEmpty == 1 && interiorGapRow >= 0) {
                        // The gap must be an interior gap — not reachable by gravity right now
                        if (!theBoard.isGravityReachable(interiorGapRow, interiorGapCol)) {
                            Move candidate = new Move(m_Name, landingRow, col);
                            // Safety check: opponent cannot win immediately after this move
                            if (isSafeMove(theBoard, candidate, opponent)) {
                                return candidate;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // Open-ended two detection and blocking (prevents open-ended three)
    // -----------------------------------------------------------------------

    /**
     * Detects if the opponent has an "open-ended two" — a sequence of TWO pieces
     * in a row that is open at BOTH ends (i.e., both empty cells at the ends are
     * gravity-reachable). Such a sequence allows the opponent to create an
     * open-ended THREE on their next move, which would then guarantee them a win
     * in the following move.
     *
     * <p>An "open-ended two" is defined as:
     * <ul>
     *   <li>A window of 4 consecutive cells in any direction (horizontal, vertical, diagonal)</li>
     *   <li>The pattern is: _XX_ where X = opponent's piece, _ = empty</li>
     *   <li>Both empty cells must be gravity-reachable (can be played immediately)</li>
     * </ul>
     *
     * <p>Why this is critical: If opponent has _XX_, they can play at either end
     * to create _XXX_ (open-ended three), which guarantees them a win because we
     * can only block one end. By blocking the open-ended two NOW, we prevent them
     * from ever creating the open-ended three.
     *
     * <p>Strategy: If we detect such a pattern, we must block one end immediately
     * to prevent the creation of the open-ended three. We prefer to block the end
     * that is also part of our own building strategy, but any block is critical.
     *
     * @param theBoard the current board state
     * @param opponent the opponent's player name
     * @return a Move to block one end of the open-ended two, or null if none found
     */
    private Move blockOpenEndedTwoMove(Board theBoard, String opponent) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();

        // All four direction vectors: horizontal, vertical, two diagonals
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];

            // Scan every possible starting position for a window of 4
            for (int startRow = 0; startRow < rows; startRow++) {
                for (int startCol = 0; startCol < cols; startCol++) {
                    // Check if we can fit a window of 4 from this starting position
                    int endRow = startRow + 3 * dr;
                    int endCol = startCol + 3 * dc;

                    if (endRow < 0 || endRow >= rows || endCol < 0 || endCol >= cols) {
                        continue; // Window doesn't fit
                    }

                    // Check the pattern: _XX_
                    // Position 0: empty
                    // Positions 1-2: opponent's pieces
                    // Position 3: empty

                    int opponentCount = 0;
                    int emptyCount = 0;
                    int emptyEnd1Row = -1, emptyEnd1Col = -1;
                    int emptyEnd2Row = -1, emptyEnd2Col = -1;
                    boolean hasOurPiece = false;

                    for (int k = 0; k < 4; k++) {
                        int r = startRow + k * dr;
                        int c = startCol + k * dc;
                        String occupant = theBoard.playerAt(r, c);

                        if (occupant == null) {
                            emptyCount++;
                            if (k == 0) {
                                emptyEnd1Row = r;
                                emptyEnd1Col = c;
                            } else if (k == 3) {
                                emptyEnd2Row = r;
                                emptyEnd2Col = c;
                            }
                        } else if (occupant.equals(opponent)) {
                            opponentCount++;
                        } else {
                            // Our piece blocks this window
                            hasOurPiece = true;
                            break;
                        }
                    }

                    // We want exactly 2 opponent pieces and 2 empty cells (both ends)
                    if (hasOurPiece || opponentCount != 2 || emptyCount != 2) {
                        continue;
                    }

                    // Verify both ends are empty (not middle positions)
                    if (emptyEnd1Row == -1 || emptyEnd2Row == -1) {
                        continue;
                    }

                    // Both ends must be gravity-reachable (playable right now)
                    boolean end1Reachable = theBoard.isGravityReachable(emptyEnd1Row, emptyEnd1Col);
                    boolean end2Reachable = theBoard.isGravityReachable(emptyEnd2Row, emptyEnd2Col);

                    if (end1Reachable && end2Reachable) {
                        // Found an open-ended two! Block one end.
                        // Prefer the end that scores better for our own strategy
                        int score1 = scoreCell(theBoard, emptyEnd1Row, emptyEnd1Col, m_Name);
                        int score2 = scoreCell(theBoard, emptyEnd2Row, emptyEnd2Col, m_Name);

                        if (score1 >= score2) {
                            Move block = new Move(m_Name, emptyEnd1Row, emptyEnd1Col);
                            if (isSafeMove(theBoard, block, opponent)) {
                                return block;
                            }
                        }

                        // Try the other end if first choice wasn't safe
                        Move block = new Move(m_Name, emptyEnd2Row, emptyEnd2Col);
                        if (isSafeMove(theBoard, block, opponent)) {
                            return block;
                        }
                    }
                }
            }
        }

        return null;
    }

    // -----------------------------------------------------------------------
    // Best safe build move
    // -----------------------------------------------------------------------

    /**
     * Score every valid candidate cell by how many open windows of four it
     * participates in, then return the highest-scoring cell that is also safe
     * (opponent cannot win immediately after we play there).
     */
    private Move findBestSafeBuildMove(Board theBoard, String opponent) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();
        int bestScore = -1;
        Move bestMove = null;

        for (int col = 0; col < cols; col++) {
            int landingRow = theBoard.landingRow(m_Name, col);
            if (landingRow < 0) continue;

            if (!cellHasWinPotential(theBoard, landingRow, col, m_Name)) continue;

            // Prefer building on top of our own pieces (mirrors SimpleStrategyPlayer)
            String below = (landingRow + 1 < rows) ? theBoard.playerAt(landingRow + 1, col) : null;
            boolean buildsOnOwn = below != null && below.equals(m_Name);

            int score = scoreCell(theBoard, landingRow, col, m_Name)
                    + (buildsOnOwn ? 10 : 0);

            if (score > bestScore) {
                Move candidate = new Move(m_Name, landingRow, col);
                if (isSafeMove(theBoard, candidate, opponent)) {
                    bestScore = score;
                    bestMove = candidate;
                }
            }
        }
        return bestMove;
    }

    // -----------------------------------------------------------------------
    // Fallback: any safe valid move
    // -----------------------------------------------------------------------

    private Move findAnySafeMove(Board theBoard, String opponent) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();

        for (int col = 0; col < cols; col++) {
            for (int row = rows - 1; row >= 0; row--) {
                if (theBoard.isValidMove(m_Name, row, col)) {
                    Move candidate = new Move(m_Name, row, col);
                    if (isSafeMove(theBoard, candidate, opponent)) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    // -----------------------------------------------------------------------
    // One-move lookahead safety check
    // -----------------------------------------------------------------------

    /**
     * Returns true if playing {@code ourMove} does NOT give the opponent a
     * winning response on their very next turn.
     *
     * <p>Strategy: after we occupy {@code (playedRow, playedCol)}, every cell
     * that sits directly on top of an occupied cell (i.e. whose row-below is
     * now filled) becomes newly gravity-reachable for the opponent.  We collect
     * all such newly-exposed cells and test each one: would it complete a
     * four-in-a-row for the opponent on the <em>post-move</em> board?
     *
     * <p>Because we cannot mutate-and-revert the real {@code Board}, we
     * simulate "what would the board look like after our move" by treating
     * {@code (playedRow, playedCol)} as occupied by us inside a manual window
     * scan, rather than calling {@code theBoard.add()} for real.
     *
     * <p>Newly-exposed cells arise in three ways:
     * <ol>
     *   <li>Directly above our piece in the same column — the classic vertical case.</li>
     *   <li>Diagonally adjacent cells (up-left, up-right) whose support cell was
     *       the cell we just played — these are the diagonal threats the previous
     *       implementation missed.</li>
     *   <li>Any other column whose top empty cell happened to already rest on a
     *       filled cell — those were reachable before our move and are unchanged,
     *       so they are not "new" threats created by this move and are ignored
     *       here (the immediate-win block in {@code takeTurn} handles pre-existing
     *       threats).</li>
     * </ol>
     *
     * <p>For each newly-exposed candidate cell we ask: counting our just-played
     * piece as already on the board, does the opponent have exactly 3 pieces in
     * some window-of-four that contains this candidate and no pieces of ours?
     * If so, the opponent can win by dropping there — our move is unsafe.
     */
    private boolean isSafeMove(Board theBoard, Move ourMove, String opponent) {
        int playedRow = ourMove.getRow();
        int playedCol = ourMove.getColumn();
        int rows = theBoard.rows();
        int cols = theBoard.columns();

        // ---- Collect cells that become newly gravity-reachable after our move ----
        //
        // A cell (r, c) is newly reachable iff:
        //   • it was NOT reachable before (i.e. the cell below it, (r+1,c), was empty)
        //   • (r+1, c) == (playedRow, playedCol)  [we just filled that support cell]
        //
        // That gives us at most three candidates:
        //   (playedRow-1, playedCol)   — directly above (same column)
        //   (playedRow-1, playedCol-1) — diagonal up-left  (support is (playedRow, playedCol) for col-1)
        //     Wait — gravity in col-1 is independent; the support for (playedRow-1, playedCol-1)
        //     is (playedRow, playedCol-1), not our played cell.
        //
        // Re-examining: the only column whose stack is affected by our move is
        // playedCol itself.  In every other column gravity is unchanged.
        // HOWEVER, a diagonal winning sequence can have its *last empty cell*
        // land in playedCol at row (playedRow-1) — i.e. directly above our move.
        // We therefore need to test (playedRow-1, playedCol) against ALL four
        // directions, not just vertical.  That single cell is the only newly
        // unlocked cell.
        //
        // Additionally, we must check diagonal cells in OTHER columns if our move
        // fills the cell that acts as a "floor" enabling the opponent's diagonal
        // winning drop.  Concretely: for a \ diagonal, the opponent might need to
        // drop at (playedRow-1, playedCol+1), but that cell's support is
        // (playedRow, playedCol+1) — nothing to do with us.  So the only newly
        // enabled cell is indeed (playedRow-1, playedCol).
        //
        // What we ALSO need to catch is: our piece at (playedRow, playedCol) now
        // acts as the third piece in a diagonal window for the opponent, making a
        // previously non-threatening three-in-a-row into a completed four — but
        // that would have been caught by findWinningMove BEFORE our move (it would
        // already be a winning move for them).  The new danger is when our piece
        // completes a run of two opponent pieces such that the FOURTH cell becomes
        // the newly-exposed (playedRow-1, playedCol).
        //
        // Conclusion: test every window of four that contains the newly exposed
        // cell (playedRow-1, playedCol), treating our just-played piece as present.

        int exposedRow = playedRow - 1;
        int exposedCol = playedCol;

        if (exposedRow < 0) {
            // We played at the very top of the column; nothing newly exposed.
            return true;
        }

        // Only proceed if the exposed cell is actually empty (it should be, but guard anyway)
        if (theBoard.playerAt(exposedRow, exposedCol) != null) {
            return true;
        }

        // All four direction vectors
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];

            // Examine every window of four that contains (exposedRow, exposedCol)
            for (int offset = -3; offset <= 0; offset++) {
                int sr = exposedRow + offset * dr;
                int sc = exposedCol + offset * dc;

                int opponentCount = 0;
                int emptyCount = 0;
                boolean windowValid = true;

                for (int k = 0; k < 4; k++) {
                    int r = sr + k * dr;
                    int c = sc + k * dc;

                    if (r < 0 || r >= rows || c < 0 || c >= cols) {
                        windowValid = false;
                        break;
                    }

                    // Simulate our just-played piece being on the board
                    if (r == playedRow && c == playedCol) {
                        // Our piece — this window is blocked for the opponent
                        windowValid = false;
                        break;
                    }

                    String occupant = theBoard.playerAt(r, c);

                    if (r == exposedRow && c == exposedCol) {
                        // This is the candidate drop cell — treat as empty (opponent would play here)
                        emptyCount++;
                    } else if (occupant == null) {
                        emptyCount++;
                    } else if (occupant.equals(opponent)) {
                        opponentCount++;
                    } else {
                        // Our piece elsewhere in the window — blocked for opponent
                        windowValid = false;
                        break;
                    }
                }

                // Opponent wins if they have 3 pieces and the one empty cell is
                // exactly the exposed drop cell (emptyCount == 1)
                if (windowValid && opponentCount == 3 && emptyCount == 1) {
                    return false; // unsafe — opponent gets a diagonal (or other) win
                }
            }
        }

        return true;
    }

    // -----------------------------------------------------------------------
    // Dead-end pruning
    // -----------------------------------------------------------------------

    /**
     * Returns true if {@code (row, col)} is part of at least one window of
     * four that is not already blocked by the opponent (i.e. could still
     * theoretically contribute to a win for {@code player}).
     */
    private boolean cellHasWinPotential(Board theBoard, int row, int col, String player) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];
            for (int offset = -3; offset <= 0; offset++) {
                int sr = row + offset * dr;
                int sc = col + offset * dc;
                boolean windowBlocked = false;
                boolean windowValid = true;

                for (int k = 0; k < 4; k++) {
                    int r = sr + k * dr;
                    int c = sc + k * dc;
                    if (r < 0 || r >= rows || c < 0 || c >= cols) {
                        windowValid = false;
                        break;
                    }
                    String occupant = theBoard.playerAt(r, c);
                    if (occupant != null && !occupant.equals(player)) {
                        windowBlocked = true;
                        break;
                    }
                }
                if (windowValid && !windowBlocked) return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Cell scoring heuristic
    // -----------------------------------------------------------------------

    /**
     * Count how many unblocked windows of four contain this cell, weighted by
     * how many of our pieces are already in that window.
     *   window with 0 of ours → +1
     *   window with 1 of ours → +2
     *   window with 2 of ours → +4
     *   window with 3 of ours → +8  (one away from winning — findWinningMove handles actual wins)
     */
    private int scoreCell(Board theBoard, int row, int col, String player) {
        int rows = theBoard.rows();
        int cols = theBoard.columns();
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        int score = 0;

        for (int[] d : dirs) {
            int dr = d[0], dc = d[1];
            for (int offset = -3; offset <= 0; offset++) {
                int sr = row + offset * dr;
                int sc = col + offset * dc;
                int mine = 0;
                boolean blocked = false;
                boolean valid = true;

                for (int k = 0; k < 4; k++) {
                    int r = sr + k * dr;
                    int c = sc + k * dc;
                    if (r < 0 || r >= rows || c < 0 || c >= cols) {
                        valid = false;
                        break;
                    }
                    String occupant = theBoard.playerAt(r, c);
                    if (occupant != null && !occupant.equals(player)) {
                        blocked = true;
                        break;
                    }
                    if (occupant != null && occupant.equals(player)) mine++;
                }

                if (valid && !blocked) {
                    score += (1 << mine); // 1, 2, 4, or 8
                }
            }
        }
        return score;
    }

}
