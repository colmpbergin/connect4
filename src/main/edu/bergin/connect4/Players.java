package edu.bergin.connect4;

import java.util.HashSet;
import java.util.Set;

// Identifies a set of players in a collection of Moves
// TODO see if this class could simply be replaced by a method on Moves
public class Players {

  private Moves moves;

  public Players(Moves moves) {
    this.moves = moves;
  }

  // Return the set of (names of) players found in the Moves collection
  public Set<String> players() {
    Set<String> players = new HashSet<String>();
    for (Move move : moves.moves()) {
      players.add(move.player());
    }
    return players;
  }

}
