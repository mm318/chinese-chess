////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCPieces
//Description: Interface of a digital representation of a piece
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import javax.swing.JLabel;

/**
 * Interface of a digital representation of a piece
 */
public interface CCPieces {
  /** piece type constants */
  public static final int ADVISOR1 = 1, CANNON1 = 2, CHARIOT1 = 3, ELEPHANT1 = 4, GENERAL1 = 5, HORSE1 = 6, SOLDIER1 = 7,
                          ADVISOR2 = 8, CANNON2 = 9, CHARIOT2 = 10, ELEPHANT2 = 11, GENERAL2 = 12, HORSE2 = 13, SOLDIER2 = 14;

  /**
   * Checks to see if the move is valid for this type of piece.
   *
   * @param  board the board on which the piece is.
   * @param  move  the move to be made.
   * @return       true or false for validity.
   */
  public boolean validateMove(CCPieces board[][], CCMove move);

  /**
   * Gets the player of the piece
   *
   * @return     true or false for being of player one.
   */
  public boolean isPlayerOne();

  /**
   * Gets the location of the piece.
   *
   * @return       an int array representation of the piece's location.
   */
  public int[] getLocation();

  /**
   * Sets the location of the piece.
   *
   * @param  x x coordinate.
   * @param  y y coordinate.
   */
  public void setLocation(int x, int y);

  /**
   * Gets the type of the piece
   *
   * @return     integer representation of the piece type.
   */
  public int getPieceType();

  /**
   * Gets the value of the piece
   *
   * @return     integer of the piece's value.
   */
  public int getPieceValue();

  /**
   * Gets the image of the piece(used in GameBoard)
   *
   * @return    a JLabel of the image of the piece.
   */
  public JLabel getIcon();
}
