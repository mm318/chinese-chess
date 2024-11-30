////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCMove
//Description: The digital representation of a move
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

/**
 * The digital representation of a move
 */
public class CCMove
{
    /** Constants for the coordinate type of the array's element at the index */
    public static final int X = 0;
    public static final int Y = 1;

    /** integer array of the original position */
    private int position1[];
    /** integer array of the new position */
    private int position2[];

    /** the captured piece of a move */
    private CCPieces capturedPiece;

    /**
     * Constructs a new empty move
     */
    public CCMove ()
    {
	position1 = new int [2];
	position2 = new int [2];
    }


    /**
     * Constructs a new move.
     *
     * @param  x1 x coordinate of the original position
     * @param  y1 y coordinate of the original position
     * @param  x2 x coordinate of the new position
     * @param  y2 y coordinate of the new position
     */
    public CCMove (int x1, int y1, int x2, int y2)
    {
	this ();
	position1 [X] = x1;
	position1 [Y] = y1;
	position2 [X] = x2;
	position2 [Y] = y2;
    }


    /**
     * Sets the captured piece of this move.
     *
     * @param  captured the captured piece.
     */
    public void setCapturedPiece (CCPieces captured)
    {
	capturedPiece = captured;
    }


    /**
     * Sets the coordinates of the original position.
     *
     * @param  x x coordinate of the original position
     * @param  y y coordinate of the original position
     */
    public void setOriginalPosition (int x, int y)
    {
	position1 [X] = x;
	position1 [Y] = y;
    }


    /**
     * Sets the coordinates of the new position.
     *
     * @param  x x coordinate of the new position
     * @param  y y coordinate of the new position
     */
    public void setNewPosition (int x, int y)
    {
	position2 [X] = x;
	position2 [Y] = y;
    }


    /**
     * Gets the coordinates of the original position.
     *
     * @return   integer array of the original position coordinates
     */
    public int[] getOriginalPosition ()
    {
	return position1;
    }


    /**
     * Gets the coordinates of the new position.
     *
     * @return   integer array of the new position coordinates
     */
    public int[] getNewPosition ()
    {
	return position2;
    }


    /**
     * Gets the captured
     *
     * @return   integer array of the new position coordinates
     */
    public CCPieces getCapturedPiece ()
    {
	return capturedPiece;
    }


    /**
     * returns a string representation
     *
     * @return   the string representation
     */
    public String toString ()
    {
	return ("Position 1: (" + position1 [X] + ", " + position1 [Y] + ")\nPosition 2: (" + position2 [X] + ", " + position2 [Y] + ")");
    }
}
