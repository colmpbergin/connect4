package edu.bergin.connect4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ColumnTest {
	private Column aColumn;
	private Board theBoard;

	@Before
	  public void setUp() throws Exception {
		theBoard = new Board(6, 7);
		aColumn = new Column(0, 6, theBoard, 4);
	  }

	  @Test
	  public void shouldBeOverWhen4ContiguousVerticallyTakenByPlayer() {
		  for (int row = 0; row < 4; row++) {
			  Move aMove = new Move("x", row, 0);
			  theBoard.add(aMove);
		  }
		  assertTrue("should be over when a player has 4 contiguous slots", aColumn.isTaken());
	  }

	  @Test
	  public void canFindWinningMoveInAColumn() {
		  theBoard.add(new Move("x", 5, 0));
		  theBoard.add(new Move("x", 4, 0));
		  theBoard.add(new Move("x", 3, 0));
		  Move move = aColumn.findWinningMove("x");
		  assertNotNull("should find a winning move in column 0", move);
		  assertTrue("winning move should be (2,0)", move.isInRow(2) && move.isInColumn(0));
	  }
}
