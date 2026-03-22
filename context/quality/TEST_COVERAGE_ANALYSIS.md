# Test Coverage Analysis and Recommendations

## Current Test Coverage

### Well-Tested Classes ✅
- **Board** - BoardTest.java
- **Column** - ColumnTest.java, GapFillingColumnTest.java
- **Row** - RowTest.java, GapFillingRowTest.java
- **Diagonal** - DiagonalTest.java, GapFillingDiagonalTest.java
- **Connect4** - Connect4Test.java
- **Moves** - MovesTest.java
- **Player/SimpleStrategyPlayer** - PlayerTest.java

### Classes with NO Tests ❌
1. **Move** - Core data class with no dedicated tests
2. **HumanPlayer** - GUI player with no tests
3. **GraphicalView** - GUI component with no tests
4. **GraphicalGameController** - GUI controller with no tests
5. **ConsoleView** - Console UI with no tests
6. **GameController** - Console controller with no tests
7. **NullView** - Null object pattern with no tests
8. **Players** - Collection class with no tests
9. **LeftToRightDiagonal** - Specific diagonal type with no dedicated tests
10. **RightToLeftDiagonal** - Specific diagonal type with no dedicated tests

## Recommended New Tests

### Priority 1: Core Logic (High Priority)

#### 1. MoveTest.java
**Why**: Move is a fundamental data class used throughout the system.

**Suggested Tests**:
```java
- testConstructor() - Verify Move creation with player, row, column
- testIsInColumn() - Test column matching
- testIsInRow() - Test row matching
- testPlayedBy() - Test player matching
- testSetPlayer() - Test player modification
- testToString() - Verify string representation format
- testEquality() - Test if two moves with same data are equal (addresses TODO)
- testBoundaryValues() - Test with edge case row/column values
```

#### 2. Board.isValidMove() Enhanced Tests
**Why**: Critical method for game rules, needs comprehensive testing.

**Suggested Tests in BoardTest.java**:
```java
- testIsValidMoveOnBottomRow() - Bottom row moves are always valid
- testIsValidMoveWithSupport() - Move with piece below is valid
- testIsValidMoveWithoutSupport() - Move without support is invalid
- testIsValidMoveOutOfBounds() - Negative or too large row/col
- testIsValidMoveOnOccupiedSpace() - Cannot place on occupied position
- testIsValidMoveAfterPlayerAlreadyMoved() - Player turn validation
```

#### 3. Board.isOver() Enhanced Tests
**Why**: Game ending logic needs thorough testing.

**Suggested Tests in BoardTest.java**:
```java
- testIsOverWhenBoardFull() - All 42 positions filled
- testIsOverWhenVerticalWin() - 4 in a column
- testIsOverWhenHorizontalWin() - 4 in a row
- testIsOverWhenDiagonalWin() - 4 in diagonal
- testIsNotOverWhenGameInProgress() - Game continues
```

#### 4. SimpleStrategyPlayer Enhanced Tests
**Why**: AI logic needs verification for all strategy paths.

**Suggested Tests in PlayerTest.java**:
```java
- testFindsWinningMove() - AI takes winning move when available
- testBlocksOpponentWin() - AI blocks opponent's winning move
- testBuildsOnOwnPieces() - AI stacks on its own pieces
- testPicksAvailableColumn() - AI picks valid column when no strategy applies
- testThinkTime() - Verify 1-second delay exists
- testDoesNotMoveOutOfTurn() - Throws exception when not player's turn
```

### Priority 2: Utility Classes (Medium Priority)

#### 5. PlayersTest.java (NEW)
**Why**: Collection management needs testing.

**Suggested Tests**:
```java
- testAddPlayer() - Add players to collection
- testGetPlayerByName() - Retrieve player by name
- testPlayerCount() - Verify player count
- testIteratePlayers() - Test iteration over players
```

#### 6. LeftToRightDiagonalTest.java (NEW)
**Why**: Specific diagonal direction logic.

**Suggested Tests**:
```java
- testNextRow() - Verify row increments correctly
- testNextCol() - Verify column increments correctly
- testDiagonalPath() - Verify correct diagonal positions
- testStartPosition() - Verify diagonal starts at correct position
```

#### 7. RightToLeftDiagonalTest.java (NEW)
**Why**: Specific diagonal direction logic.

**Suggested Tests**:
```java
- testNextRow() - Verify row increments correctly
- testNextCol() - Verify column decrements correctly
- testDiagonalPath() - Verify correct diagonal positions
- testStartPosition() - Verify diagonal starts at correct position
```

### Priority 3: GUI Components (Lower Priority - Hard to Test)

#### 8. HumanPlayerTest.java (NEW)
**Why**: User input handling needs verification.

**Note**: Requires mocking GraphicalView.

**Suggested Tests**:
```java
- testMakeMoveInValidColumn() - Valid column selection
- testMakeMoveInFullColumn() - Reject full column
- testWaitForUserInput() - Verify waiting mechanism
- testThrowsWhenNotPlayersTurn() - Turn validation
```

#### 9. NullViewTest.java (NEW)
**Why**: Null object pattern should be verified.

**Suggested Tests**:
```java
- testShowDoesNotThrow() - Verify show() doesn't throw
- testReportWinnerDoesNotThrow() - Verify reportWinner() doesn't throw
- testNullObjectBehavior() - Verify all methods are no-ops
```

### Priority 4: Integration Tests

#### 10. Full Game Scenarios
**Why**: End-to-end game flow testing.

**Suggested Tests in Connect4Test.java**:
```java
- testFullGameWithVerticalWin() - Complete game ending in vertical win
- testFullGameWithHorizontalWin() - Complete game ending in horizontal win
- testFullGameWithDiagonalWin() - Complete game ending in diagonal win
- testFullGameWithDraw() - Complete game ending in draw (full board)
- testAlternatingTurns() - Verify players alternate correctly
```

## Test Coverage Metrics

### Current Coverage (Estimated)
- **Core Logic**: ~70% (Board, Column, Row, Diagonal well-tested)
- **Strategy/AI**: ~40% (Basic tests exist, but not comprehensive)
- **Data Classes**: ~20% (Move has no tests)
- **GUI Components**: ~0% (No GUI tests)
- **Controllers**: ~0% (No controller tests)

### Target Coverage
- **Core Logic**: 90%+ (add Board.isValidMove edge cases)
- **Strategy/AI**: 80%+ (add strategy path tests)
- **Data Classes**: 90%+ (add Move tests)
- **GUI Components**: 50%+ (basic smoke tests with mocks)
- **Controllers**: 50%+ (basic flow tests)

## Implementation Priority

1. **Immediate** (This Sprint):
   - MoveTest.java - 30 minutes
   - Enhanced Board.isValidMove tests - 1 hour
   - Enhanced SimpleStrategyPlayer tests - 1 hour

2. **Short Term** (Next Sprint):
   - PlayersTest.java - 30 minutes
   - LeftToRightDiagonalTest.java - 30 minutes
   - RightToLeftDiagonalTest.java - 30 minutes
   - NullViewTest.java - 20 minutes

3. **Medium Term** (Future):
   - HumanPlayerTest.java (requires mocking framework) - 2 hours
   - Full game integration tests - 2 hours

4. **Optional** (Nice to Have):
   - GUI component tests (requires UI testing framework)
   - Controller tests (requires integration test setup)

## Notes

- **GUI Testing**: GraphicalView, GraphicalGameController, and ConsoleView are difficult to unit test without a UI testing framework. Consider manual testing or integration tests instead.

- **Mocking**: HumanPlayer tests would benefit from a mocking framework (Mockito is already in lib/) to mock GraphicalView interactions.

- **Integration Tests**: Consider adding more end-to-end tests that play complete games to verify the entire system works together.

- **Test Data Builders**: Consider creating test data builders for complex setups (e.g., BoardBuilder for creating specific board states).

## Conclusion

The codebase has good coverage for core game logic (Board, Column, Row, Diagonal) and gap-filling features. The main gaps are:

1. **Move class** - No tests for fundamental data class
2. **Board.isValidMove()** - Needs edge case testing
3. **SimpleStrategyPlayer** - Needs strategy path verification
4. **GUI components** - No tests (acceptable for now)
5. **Utility classes** - Players, specific diagonal types need tests

Implementing Priority 1 tests would significantly improve confidence in the codebase.
