package edu.bergin.connect4;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

// Unit test assertions against the Moves interface

public class MovesTest {

	private Moves theMoves;
	private Players thePlayers;

	@Before
	public void setUp() throws Exception {
		theMoves = new Moves(6, 7);
	}

	@Test
	public void canDetectAnInvalidMove() {
		theMoves.add(new Move("x", 5, 6));
		assertTrue("move should be flagged as valid", theMoves.isValidMove("o", 5, 0));
		assertFalse("move should be flagged as invalid", theMoves.isValidMove("x", 0, 6));
	}

	@Test
	public void canRetrieveListOfPlayers() {
		theMoves.add(new Move("x", 5, 6));
		theMoves.add(new Move("o", 5, 5));
		thePlayers = new Players(theMoves);
		Set<String> playerSet = thePlayers.players();
		assertTrue("wrong number of players", playerSet.size() == 2);
	}
}
