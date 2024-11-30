////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCPlayer
//Description: A digital representation of a player's data.
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import java.util.LinkedList;

/**
 * A digital representation of a player's data.
 */
public class CCPlayer {
  /** boolean designating whether this player is player one or two */
  private boolean isPlayerOne;
  /** boolean designating whether this player is a human or computer player */
  private boolean isComputer;
  /** list of the player's pieces */
  private LinkedList pieces;

  /**
   * Constructs the current player.
   *
   * @param  playerOne  whether the current player is player one or not.
   * @param  isComputer whether the current player is a computer player or not.
   */
  public CCPlayer(boolean playerOne, boolean isComputer) {
    isPlayerOne = playerOne;
    this.isComputer = isComputer;

    pieces = new LinkedList();
  }


  /**
   * Adds a piece to the current list of player's pieces.
   *
   * @param  piece the piece to be added.
   */
  public void addPiece(CCPieces piece) {
    pieces.add(piece);
  }


  /**
   * Returns if the current player is player one or not.
   *
   * @return   boolean if the player is player one.
   */
  public boolean isPlayerOne() {
    return isPlayerOne;
  }


  /**
   * Returns if the current player is a computer player or not.
   *
   * @return   boolean if the player is a computer player.
   */
  public boolean isComputer() {
    return isComputer;
  }


  /**
   * Gets the player's pieces.
   *
   * @return   an array of the player's pieces.
   */
  public CCPieces[] getPieces() {
    Object[] output1 = pieces.toArray();
    CCPieces[] output2 = new CCPieces[output1.length];

    for (int i = 0 ; i < output1.length ; i++) {
      output2[i] = (CCPieces) output1[i];
    }

    return output2;
  }
}
