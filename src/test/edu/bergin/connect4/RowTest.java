package edu.bergin.connect4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class RowTest {
	private Row aRow;
	private Board theBoard;

	@Test
	  public void canFindWinningMoveInARow() {
		  // Add support pieces at row 2 for pieces at row 1 to be valid
		  theBoard.add(new Move("o", 2, 0)); // Support for (1,0)
		  theBoard.add(new Move("o", 2, 1)); // Support for (1,1)
		  theBoard.add(new Move("o", 2, 2)); // Support for (1,2)
		  theBoard.add(new Move("o", 2, 3)); // Support for winning move at (1,3)

		  theBoard.add(new Move("x", aRow.row(), 0));
		  theBoard.add(new Move("x", aRow.row(), 1));
		  theBoard.add(new Move("x", aRow.row(), 2));
		  Move move = aRow.findWinningMove("x");
		  String s = "should find a winning move in row " + aRow.row();
		  assertNotNull(s, move);
		  s = "winning move should be (" + aRow.row() + ",3)";
		  assertTrue(s, move.isInRow(aRow.row()) && move.isInColumn(3));
	  }

	  @Before
	  public void setUp() throws Exception {
		theBoard = new Board(6, 7);
		aRow = new Row(1, 7, theBoard, 4);
	  }

	  @Test
	  public void shouldBeOverWhen4ContiguousHorizontallyTakenByPlayer() {
		  // Add support pieces at row 2 for pieces at row 1 to be valid
		  for (int col = 0; col < 4; col++) {
			  theBoard.add(new Move("o", 2, col)); // Support pieces
		  }
		  for (int col = 0; col < 4; col++) {
			  Move aMove = new Move("x", 1, col);
			  theBoard.add(aMove);
		  }
		  assertTrue("should be over when a player has 4 contiguous slots", aRow.isTaken());
	  }
}
