# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Sensitive Files - Never Read
These files contain sensitive information and should never be included in AI context or conversation.

- .env, .env.*, *.env files
- *.pem, *.key, *_rsa, *_ed25519 files
- *.tfvars files
- Any files containing "secret", "password" or "credential"
- credentials/, keys/, secrets/ directories

## Build System

The project was migrated from Ant to Gradle. Use Gradle for all builds (Ant's `build.xml` is kept for reference).

```bash
./gradlew build          # compile and package to lib/connect4.jar
./gradlew test           # run all tests (JUnit 4)
./gradlew run            # build and run console version
./gradlew runGui         # build and run GUI version
./gradlew jarGui         # build GUI jar only (lib/connect4-gui.jar)
./gradlew clean          # clean all build artifacts
./gradlew coverage       # run tests with JaCoCo coverage report
./gradlew jacocoTestReport  # generate coverage HTML to junit/coverage/
```

Run a single test class:
```bash
./gradlew test --tests "edu.bergin.connect4.BoardTest"
```

- Source: `src/main/` (main), `src/test/` (tests)
- Java 17 toolchain (updated from 11; only Java 17 is installed on this machine)
- Test reports: `junit/html/`, `junit/data/`
- Console JAR: `lib/connect4.jar` (Main-Class: `GameController`); GUI JAR: `lib/connect4-gui.jar` (Main-Class: `GraphicalGameController`), both built by Gradle

## Tests

11 test classes covering all major components:
- `BoardTest` — board mechanics: valid/invalid moves, gravity, winning move detection
- `Connect4Test` — game-level acceptance tests: player limits, game-over, illegal moves after win
- `PlayerTest` — turn mechanics and double-turn prevention
- `RowTest`, `ColumnTest`, `DiagonalTest` — win detection for each line type
- `GapFillingRowTest`, `GapFillingColumnTest`, `GapFillingDiagonalTest` — gap-filling patterns
- `OpenEndedTwoDefenseTest` — TacticalStrategyPlayer's `_XX_` blocking behaviour
- `MovesTest` — legacy Moves collection

## Architecture

The game follows an MVC pattern with pluggable views and players.

**Core interfaces:**
- `BoardGame` — game lifecycle (`addPlayer`, `play`); implemented by `Connect4`
- `View` — `update(Board)` called after every move; implementations: `ConsoleView`, `GraphicalView`, `NullView` (no-op view for headless/automated testing)
- `IPlayer` — `takeTurn(Board)`; `Player` is the abstract base class

**Game flow (`Connect4.play`):** alternates turns between two `Player` instances (always named `"x"` and `"o"`), calling `theView.update(theBoard)` after each move.

**Win detection:** `Board` holds 25 `CompletionSet` objects (6 rows + 7 columns + 6 `LeftToRightDiagonal` + 6 `RightToLeftDiagonal`). After each move, `Board.isOver()` checks if any `CompletionSet` is fully taken by one player. `Board.findWinningMove(player)` delegates to each `CompletionSet` to find a one-move win, including gap-filling (completing a sequence that has an empty cell in the middle), used by `SimpleStrategyPlayer`. `CompletionSet` subclasses: `Row`, `Column`, abstract `Diagonal`, `LeftToRightDiagonal`, `RightToLeftDiagonal`. `Diagonal` provides `nextRow()`/`nextCol()` abstractions; the two concrete subclasses differ only in direction.

**Board constraint:** only a 6×7 board is supported; `Board` throws if any other size is requested.

**Board gravity helpers:** `Board.landingRow(name, col)` returns the row a piece dropped in `col` would land on; `Board.isGravityReachable(row, col)` returns true if the cell is accessible (i.e. all cells below it are occupied).

**AI players:**
- `SimpleStrategyPlayer` — prioritizes: take own winning move → block opponent's winning move → build on existing piece → pick next available column
- `TacticalStrategyPlayer` — extends `Player` directly (not `SimpleStrategyPlayer`) with: one-move lookahead safety (never hand opponent an immediate win), dead-end pruning (skip columns that can't contribute to any win), gap-trap setup (broken-three with an interior gap gravity won't fill), open-ended two blocking (`_XX_` patterns), and a scored build heuristic; configurable think time (default 1000 ms)

**`Player` (abstract base):** provides `randomOpeningMoveIfFirst(Board)` (plays a random valid column on move 1) and `opponentName()` (returns the other player's name, used by all strategies for blocking logic).

**Legacy classes (avoid using):** `Moves` and `Players` — these predate the current architecture and may be removed in future. Prefer reading `Board` state directly.

**Entry points:**
- `GameController` — console mode (SimpleStrategy vs SimpleStrategy)
- `GraphicalGameController` — Swing GUI with mode selection dialog (Human vs AI, Human vs Human, AI vs AI)

**GUI additions** (`GraphicalView`, `HumanPlayer`, `GraphicalGameController`): `HumanPlayer` blocks on a column-click queue fed by the `GraphicalView` button panel; GUI updates go through `SwingUtilities.invokeLater`.

## Additional Documentation

- `context/` — maintained architecture, decisions, patterns, and tech-debt docs. Read before major refactors.
- `context/architecture/WINNING_MOVE_GAP_FILLING.md` — detailed spec for the gap-filling algorithm
- `context/architecture/OPEN_ENDED_TWO_DEFENSE.md` — detailed spec for TacticalStrategyPlayer's `_XX_` defense
- `context/guides/BUILD_CONFIGURATION.md` — additional build detail
- `context/guides/GUI_USAGE.md` — GUI-specific notes
