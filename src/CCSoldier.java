////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCSoldier
//Description: Digital representation of a Soldier piece
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import javax.swing.JLabel;
import javax.swing.ImageIcon;

/**
 * Digital representation of a Soldier piece.
 */
public class CCSoldier implements CCPieces
{
    /** value of the piece */
    private int PIECE_VALUE = 2;
    /** type of piece */
    private int PIECE_TYPE;

    /** the board on which the piece is */
    private CCPieces gameBoard[] [];
    /** the image of the piece */
    private JLabel icon;

    /** player of the piece */
    private boolean isPlayerOne;
    /** position of the piece on the board */
    private int x, y;

    /**
     * Constructs this piece.
     *
     * @param  board     the board on which the piece is.
     * @param  playerOne the player of the piece
     * @param  x         initial x coordinate of the piece
     * @param  y         initial y coordinate of the piece
     */
    public CCSoldier (CCPieces board[] [], boolean playerOne, int x, int y)
    {
	isPlayerOne = playerOne;
	gameBoard = board;
	this.x = x;
	this.y = y;

	if (playerOne)
	{
	    icon = new JLabel (new ImageIcon (getClass ().getResource ("images/SoldierRed.png")));
	    PIECE_TYPE = CCPieces.SOLDIER1;
	}
	else
	{
	    icon = new JLabel (new ImageIcon (getClass ().getResource ("images/SoldierGreen.png")));
	    PIECE_TYPE = CCPieces.SOLDIER2;
	}

	setLocation (x, y);
    }


    /**
     * Checks to see if the move is valid for this type of piece.
     *
     * @param  board the board on which the piece is.
     * @param  move  the move to be made.
     * @return       true or false for validity.
     */
    public boolean validateMove (CCPieces board[] [], CCMove move)
    {
	int x1 = move.getOriginalPosition () [CCMove.X];
	int y1 = move.getOriginalPosition () [CCMove.Y];
	int x2 = move.getNewPosition () [CCMove.X];
	int y2 = move.getNewPosition () [CCMove.Y];

	//move validating logic
	if ((Math.abs (x2 - x1) != 1 && Math.abs (y2 - y1) != 1) || (Math.abs (x2 - x1) == 1 && Math.abs (y2 - y1) == 1))
	    return false;
	else if ((isPlayerOne && y2 > y1) || (!isPlayerOne && y2 < y1))
	    return false;
	else if ((isPlayerOne && y1 >= 5 && Math.abs (x2 - x1) == 1) || (!isPlayerOne && y1 <= 4 && Math.abs (x2 - x1) == 1))
	    return false;
	else if (board [y2] [x2] != null && isPlayerOne == board [y2] [x2].isPlayerOne ())
	    return false;
	else
	    return true;
    }


    /**
     * Gets the player of the piece
     *
     * @return     true or false for being of player one.
     */
    public boolean isPlayerOne ()
    {
	return isPlayerOne;
    }


    /**
     * Gets the location of the piece.
     *
     * @return       an int array representation of the piece's location.
     */
    public int[] getLocation ()
    {
	int output[] = new int [2];
	output [0] = x;
	output [1] = y;
	return output;
    }


    /**
     * Gets the value of the piece
     *
     * @return     integer of the piece's value.
     */
    public int getPieceValue ()
    {
	return PIECE_VALUE;
    }


    /**
     * Gets the type of the piece
     *
     * @return     integer representation of the piece type.
     */
    public int getPieceType ()
    {
	return PIECE_TYPE;
    }


    /**
     * Sets the location of the piece.
     *
     * @param  x x coordinate.
     * @param  y y coordinate.
     */
    public void setLocation (int x, int y)
    {
	if (!(this.x == -1 && this.y == -1))
	    gameBoard [this.y] [this.x] = null;

	if (!(x == -1 && y == -1))
	    gameBoard [y] [x] = this;

	this.y = y;
	this.x = x;
    }


    /**
     * Gets the image of the piece (used in GameBoard)
     *
     * @return    a JLabel of the image of the piece.
     */
    public JLabel getIcon ()
    {
	return icon;
    }
}
