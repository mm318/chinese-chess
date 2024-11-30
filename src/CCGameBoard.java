////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCGameBoard
//Description: Digital representation of the game board.
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Arrays;
import java.net.URL;

/**
 * Game board of the chinese chess game.
 */
public class CCGameBoard extends JPanel implements MouseInputListener
{
    /** rows and columns constants */
    public static final int NUM_ROWS = 10, NUM_COLS = 9;

    /** layered pane to put the images of the board and pieces on different layers */
    private JLayeredPane layeredPane;

    /** JLabel to graphically represent the game board */
    private JLabel board;

    /** digital represention of the game board */
    private CCPieces gameBoard[] [];
    /** graphic of the piece that is currently being moved */
    private JLabel currentPiece;
    /** the current move being made */
    private CCMove currentMove;

    /** boolean flag for current player's turn */
    private boolean isPlayerOneTurn;
    /** player abstract data type instances */
    private CCPlayer players[] = new CCPlayer [2];
    /** ai thread */
    private CCOOAI ai;

    /** move stacks */
    private LLStack undoHistory = new LLStack ();
    private LLStack redoHistory = new LLStack ();
    /** game properties window */
    private CCGameProperties properties;

    /** boolean flag for game over */
    private boolean isGameOver;
    /** dialog for displaying game over status */
    private JDialog gameOver;

    /**
     * Constructs a new game board.
     */
    public CCGameBoard ()
    {
	layeredPane = new JLayeredPane ();

	ImageIcon boardImage = new ImageIcon (getClass ().getResource ("images/board.png"));
	board = new JLabel (boardImage);
	board.setVerticalAlignment (JLabel.TOP);
	board.setHorizontalAlignment (JLabel.CENTER);
	board.setSize (boardImage.getIconWidth (), boardImage.getIconHeight ());
	board.setLocation (0, 0);
	layeredPane.add (board, new Integer (0));

	setLayout (new BorderLayout ());
	add (layeredPane, BorderLayout.CENTER);
	setPreferredSize (new Dimension (boardImage.getIconWidth (), boardImage.getIconHeight ()));

	isGameOver = true;
    }


    /**
     * initializes a new game.
     *
     * @param  humanPlayerOne specifies whether player one is human or not.
     * @param  humanPlayerTwo specifies whether player two is human or not.
     */
    public void startNewGame (boolean humanPlayerOne, boolean humanPlayerTwo)
    {
	if (gameOver != null)
	    gameOver.dispose ();
	if (ai != null)
	    ai.stopAI ();

	layeredPane.removeAll ();
	layeredPane.removeMouseListener (this);
	layeredPane.removeMouseMotionListener (this);

	layeredPane.add (board, new Integer (0));
	players [0] = new CCPlayer (true, !humanPlayerOne);
	players [1] = new CCPlayer (false, !humanPlayerTwo);

	setUpBoard ();
	undoHistory = new LLStack ();
	redoHistory = new LLStack ();

	if (properties != null)
	    properties.dispose ();
	properties = new CCGameProperties ();

	layeredPane.addMouseListener (this);
	layeredPane.addMouseMotionListener (this);

	isGameOver = false;
	isPlayerOneTurn = false;
	nextTurn ();
    }


    /**
     * initializes the board into the standard chinese chess setup.
     */
    public void setUpBoard ()
    {
	//digital board set up.
	gameBoard = new CCPieces [NUM_ROWS] [NUM_COLS];

	players [0].addPiece (new CCCannon (gameBoard, true, 1, 7));
	players [0].addPiece (new CCCannon (gameBoard, true, 7, 7));
	players [1].addPiece (new CCCannon (gameBoard, false, 1, 2));
	players [1].addPiece (new CCCannon (gameBoard, false, 7, 2));

	players [0].addPiece (new CCChariot (gameBoard, true, 0, 9));
	players [0].addPiece (new CCChariot (gameBoard, true, 8, 9));
	players [1].addPiece (new CCChariot (gameBoard, false, 0, 0));
	players [1].addPiece (new CCChariot (gameBoard, false, 8, 0));

	players [0].addPiece (new CCHorse (gameBoard, true, 1, 9));
	players [0].addPiece (new CCHorse (gameBoard, true, 7, 9));
	players [1].addPiece (new CCHorse (gameBoard, false, 1, 0));
	players [1].addPiece (new CCHorse (gameBoard, false, 7, 0));

	players [0].addPiece (new CCSoldier (gameBoard, true, 0, 6));
	players [0].addPiece (new CCSoldier (gameBoard, true, 2, 6));
	players [0].addPiece (new CCSoldier (gameBoard, true, 4, 6));
	players [0].addPiece (new CCSoldier (gameBoard, true, 6, 6));
	players [0].addPiece (new CCSoldier (gameBoard, true, 8, 6));
	players [1].addPiece (new CCSoldier (gameBoard, false, 0, 3));
	players [1].addPiece (new CCSoldier (gameBoard, false, 2, 3));
	players [1].addPiece (new CCSoldier (gameBoard, false, 4, 3));
	players [1].addPiece (new CCSoldier (gameBoard, false, 6, 3));
	players [1].addPiece (new CCSoldier (gameBoard, false, 8, 3));

	players [0].addPiece (new CCElephant (gameBoard, true, 2, 9));
	players [0].addPiece (new CCElephant (gameBoard, true, 6, 9));
	players [1].addPiece (new CCElephant (gameBoard, false, 2, 0));
	players [1].addPiece (new CCElephant (gameBoard, false, 6, 0));

	players [0].addPiece (new CCAdvisor (gameBoard, true, 3, 9));
	players [0].addPiece (new CCAdvisor (gameBoard, true, 5, 9));
	players [1].addPiece (new CCAdvisor (gameBoard, false, 3, 0));
	players [1].addPiece (new CCAdvisor (gameBoard, false, 5, 0));

	players [0].addPiece (new CCGeneral (gameBoard, true, 4, 9));
	players [1].addPiece (new CCGeneral (gameBoard, false, 4, 0));

	CCPieces playerOnePieces[] = players [0].getPieces ();
	CCPieces playerTwoPieces[] = players [1].getPieces ();

	for (int i = 0 ; i < playerOnePieces.length ; i++) //graphical board set up.
	{
	    playerOnePieces [i].getIcon ().setSize (48, 50);
	    playerOnePieces [i].getIcon ().setLocation (24 + playerOnePieces [i].getLocation () [0] * 50, 25 + playerOnePieces [i].getLocation () [1] * 50);
	    layeredPane.add (playerOnePieces [i].getIcon (), new Integer (1));

	    playerTwoPieces [i].getIcon ().setSize (48, 50);
	    playerTwoPieces [i].getIcon ().setLocation (24 + playerTwoPieces [i].getLocation () [0] * 50, 25 + playerTwoPieces [i].getLocation () [1] * 50);
	    layeredPane.add (playerTwoPieces [i].getIcon (), new Integer (1));
	}
    }


    /**
     * makes a move to the current game.
     *
     * @param  move the move to be made.
     */
    public void makeMove (CCMove move)
    {
	int x1 = move.getOriginalPosition () [CCMove.X];
	int y1 = move.getOriginalPosition () [CCMove.Y];
	int x2 = move.getNewPosition () [CCMove.X];
	int y2 = move.getNewPosition () [CCMove.Y];

	int gameOver = 0;

	if (gameBoard [y2] [x2] != null)
	{
	    if (gameBoard [y2] [x2].getPieceType () == CCPieces.GENERAL1)
		gameOver = 2;
	    else if (gameBoard [y2] [x2].getPieceType () == CCPieces.GENERAL2)
		gameOver = 1;

	    move.setCapturedPiece (gameBoard [y2] [x2]);
	    layeredPane.remove (gameBoard [y2] [x2].getIcon ());
	    gameBoard [y2] [x2].setLocation (-1, -1);
	}
	gameBoard [y1] [x1].getIcon ().setLocation (24 + x2 * 50, 25 + y2 * 50);
	layeredPane.setLayer (gameBoard [y1] [x1].getIcon (), 1);
	gameBoard [y1] [x1].setLocation (x2, y2);

	undoHistory.push (move);
	redoHistory = new LLStack ();
	properties.addMove (isPlayerOneTurn, move);
	currentPiece = null;
	currentMove = null;

	if (gameOver == 1)
	    endGame (true);
	else if (gameOver == 2)
	    endGame (false);
	else
	    nextTurn ();
    }


    /**
     * undoes the previous move made to the current game.
     *
     * @return  returns boolean whether the move was undone or not.
     */
    public boolean undoMove ()
    {
	if (undoHistory.isEmpty ())
	    return false;
	else
	{
	    if (ai != null)
		ai.stopAI ();

	    CCMove move = undoHistory.pop ();
	    int x1 = move.getOriginalPosition () [CCMove.X];
	    int y1 = move.getOriginalPosition () [CCMove.Y];
	    int x2 = move.getNewPosition () [CCMove.X];
	    int y2 = move.getNewPosition () [CCMove.Y];

	    gameBoard [y2] [x2].getIcon ().setLocation (24 + x1 * 50, 25 + y1 * 50);
	    gameBoard [y2] [x2].setLocation (x1, y1);

	    if (move.getCapturedPiece () != null)
	    {
		layeredPane.add (move.getCapturedPiece ().getIcon ());
		move.getCapturedPiece ().getIcon ().setLocation (24 + x2 * 50, 25 + y2 * 50);
		move.getCapturedPiece ().setLocation (x2, y2);
	    }

	    redoHistory.push (move);
	    nextTurn ();

	    return true;
	}
    }


    /**
     * redoes the previous undone move to the current game.
     *
     * @return  returns boolean whether the move was redone or not.
     */
    public boolean redoMove ()
    {
	if (redoHistory.isEmpty ())
	    return false;
	else
	{
	    if (ai != null)
		ai.stopAI ();

	    CCMove move = redoHistory.pop ();
	    int x1 = move.getOriginalPosition () [CCMove.X];
	    int y1 = move.getOriginalPosition () [CCMove.Y];
	    int x2 = move.getNewPosition () [CCMove.X];
	    int y2 = move.getNewPosition () [CCMove.Y];

	    if (gameBoard [y2] [x2] != null)
	    {
		move.setCapturedPiece (gameBoard [y2] [x2]);
		layeredPane.remove (gameBoard [y2] [x2].getIcon ());
		gameBoard [y2] [x2].setLocation (-1, -1);
	    }

	    gameBoard [y1] [x1].getIcon ().setLocation (24 + x2 * 50, 25 + y2 * 50);
	    gameBoard [y1] [x1].setLocation (x2, y2);

	    undoHistory.push (move);
	    nextTurn ();

	    return true;
	}
    }


    /**
     * shifts the turn to the next player.
     */
    public void nextTurn ()
    {
	isPlayerOneTurn = !isPlayerOneTurn;

	if (ai != null)
	    ai.stopAI ();
	if ((isPlayerOneTurn && players [0].isComputer ()) || (!isPlayerOneTurn && players [1].isComputer ()))
	{
	    ai = new CCOOAI (this, gameBoard, players [0], players [1]);
	    ai.getNextMove (isPlayerOneTurn);
	}
    }


    /**
     * ends the current game, displaying a game over dialog.
     *
     * @param  playerOneWinner whether the winner is player one or not.
     */
    public void endGame (boolean playerOneWinner)
    {
	isGameOver = true;
	if (ai != null)
	    ai.stopAI ();

	layeredPane.removeMouseListener (this);
	layeredPane.removeMouseMotionListener (this);

	gameOver = new JDialog ();
	gameOver.setTitle ("Game Over");
	gameOver.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

	String text;
	if (playerOneWinner)
	    text = "Player one wins!";
	else
	    text = "Player two wins!";

	gameOver.getContentPane ().setLayout (new FlowLayout (FlowLayout.CENTER));
	gameOver.getContentPane ().add (new JLabel (text));

	gameOver.pack ();
	gameOver.setResizable (false);
	gameOver.setVisible (true);
    }


    /**
     * saves the current game.
     *
     * @param  filename    the name with or without a directory at which of the file to be saved.
     * @throws IOException if file cannot be saved for some reason.
     */
    public void saveGame (String filename) throws IOException
    {
	if (!undoHistory.isEmpty ())
	{
	    BufferedWriter out = new BufferedWriter (new FileWriter (filename));

	    if (isGameOver) //determines whether the saved file is a replay or an unfinished game
		out.write (1);
	    else
	    {
		out.write (0);

		if (players [0].isComputer ())
		    out.write (0);
		else
		    out.write (1);
		if (players [1].isComputer ())
		    out.write (0);
		else
		    out.write (1);
	    }

	    LLStack outputStack = new LLStack ();
	    while (!undoHistory.isEmpty ())
		outputStack.push (undoHistory.pop ());

	    CCMove move;
	    while (!outputStack.isEmpty ()) //writing the current game's moves to file.
	    {
		move = outputStack.pop ();
		out.write (move.getOriginalPosition () [CCMove.X]);
		out.write (move.getOriginalPosition () [CCMove.Y]);
		out.write (move.getNewPosition () [CCMove.X]);
		out.write (move.getNewPosition () [CCMove.Y]);
		undoHistory.push (move);
	    }
	    out.close ();
	}
    }


    /**
     * loads a game into the current game.
     *
     * @param  filename    the name with or without a directory at which of the file to be loaded.
     * @throws IOException if file cannot be loaded for some reason.
     */
    public void loadGame (String filename) throws IOException
    {
	BufferedReader in = new BufferedReader (new FileReader (filename));

	try //loads the game
	{
	    boolean isReplay = false;
	    if (in.read () != 0)
		isReplay = true;

	    int input;
	    if (isReplay)
	    {
		while ((input = in.read ()) != -1)
		    makeMove (new CCMove (input, in.read (), in.read (), in.read ()));
		while (undoMove ())
		{
		}
	    }
	    else
	    {
		startNewGame (in.read () != 0, in.read () != 0);
		while ((input = in.read ()) != -1)
		    makeMove (new CCMove (input, in.read (), in.read (), in.read ()));
	    }
	    in.close ();
	}
	catch (Exception e)  //displays error dialog if error occurs
	{
	    if (ai != null)
		ai.stopAI ();

	    layeredPane.removeAll ();
	    layeredPane.removeMouseListener (this);
	    layeredPane.removeMouseMotionListener (this);
	    layeredPane.add (board, new Integer (0));

	    JDialog error = new JDialog ();
	    error.setTitle ("Error");
	    error.setDefaultCloseOperation (WindowConstants.DISPOSE_ON_CLOSE);

	    JButton ok = new JButton ("OK");
	    ok.addMouseListener (new cancelButtonListener (error));

	    error.getContentPane ().setLayout (new BorderLayout ());
	    error.getContentPane ().add (new JLabel ("Unable to load corrupted replay."), BorderLayout.CENTER);
	    error.getContentPane ().add (ok, BorderLayout.SOUTH);

	    error.pack ();
	    error.setResizable (false);
	    error.setVisible (true);
	}
    }


    /**
     * mouse listener for any button that destroys its parent
     */
    class cancelButtonListener implements MouseListener
    {
	JDialog parent;

	public cancelButtonListener (JDialog parent)
	{
	    this.parent = parent;
	}

	public void mouseClicked (MouseEvent e)
	{
	}

	public void mouseEntered (MouseEvent e)
	{
	}

	public void mouseExited (MouseEvent e)
	{
	}

	public void mousePressed (MouseEvent e)
	{
	}

	public void mouseReleased (MouseEvent e)
	{
	    parent.hide ();
	    parent.dispose ();
	}
    }


    /**
     * toggles the game properites display
     */
    public void toggleProperties ()
    {
	if (properties != null)
	    properties.showhide ();
    }


    /**
     * interprets the user's mouse input as a move
     *
     * @param  e the mouse input
     */
    public void mouseClicked (MouseEvent e)
    {
	if ((isPlayerOneTurn && !players [0].isComputer ()) || (!isPlayerOneTurn && !players [1].isComputer ()))
	{
	    if (currentPiece == null) //if the piece has not been selected yet
	    {
		int x1 = (e.getX () - 24) / 50;
		int y1 = (e.getY () - 25) / 50;

		currentMove = new CCMove ();

		currentMove.setOriginalPosition (x1, y1);
		if (x1 >= 0 && x1 <= 8 && y1 >= 0 && y1 <= 9 && gameBoard [y1] [x1] != null &&
			((gameBoard [y1] [x1].isPlayerOne () && isPlayerOneTurn) || (!gameBoard [y1] [x1].isPlayerOne () && !isPlayerOneTurn)))
		{
		    currentPiece = gameBoard [y1] [x1].getIcon ();
		    layeredPane.setLayer (currentPiece, 2);
		    currentPiece.setLocation (e.getX () - 24, e.getY () - 25);
		}
		else
		    currentMove = null;
	    }
	    else //if a piece already has been selected.
	    {
		int x1 = currentMove.getOriginalPosition () [CCMove.X];
		int y1 = currentMove.getOriginalPosition () [CCMove.Y];
		int x2 = (e.getX () - 24) / 50;
		int y2 = (e.getY () - 25) / 50;

		currentMove.setNewPosition (x2, y2);
		if (x2 >= 0 && x2 <= 8 && y2 >= 0 && y2 <= 9 && !Arrays.equals (currentMove.getOriginalPosition (), currentMove.getNewPosition ())
			&& gameBoard [y1] [x1].validateMove (gameBoard, currentMove))
		    makeMove (currentMove);
		else
		{
		    currentPiece.setLocation (24 + x1 * 50, 25 + y1 * 50);
		    layeredPane.setLayer (currentPiece, 1);
		    currentPiece = null;
		    currentMove = null;
		}
	    }
	}
    }


    public void mouseEntered (MouseEvent e)
    {
    }


    public void mouseExited (MouseEvent e)
    {
    }


    /**
     * interprets the user's mouse input as a move
     *
     * @param  e the mouse input
     */
    public void mousePressed (MouseEvent e)
    {
	if ((isPlayerOneTurn && !players [0].isComputer ()) || (!isPlayerOneTurn && !players [1].isComputer ()))
	{
	    if (currentPiece == null) //if the piece has not been selected yet
	    {
		int x1 = (e.getX () - 24) / 50;
		int y1 = (e.getY () - 25) / 50;

		currentMove = new CCMove ();

		currentMove.setOriginalPosition (x1, y1);
		if (x1 >= 0 && x1 <= 8 && y1 >= 0 && y1 <= 9 && gameBoard [y1] [x1] != null &&
			((gameBoard [y1] [x1].isPlayerOne () && isPlayerOneTurn) || (!gameBoard [y1] [x1].isPlayerOne () && !isPlayerOneTurn)))
		{
		    currentPiece = gameBoard [y1] [x1].getIcon ();
		    layeredPane.setLayer (currentPiece, 2);
		    currentPiece.setLocation (e.getX () - 24, e.getY () - 25);
		}
		else
		    currentMove = null;
	    }
	    else //if a piece already has been selected.
	    {
		int x1 = currentMove.getOriginalPosition () [CCMove.X];
		int y1 = currentMove.getOriginalPosition () [CCMove.Y];
		int x2 = (e.getX () - 24) / 50;
		int y2 = (e.getY () - 25) / 50;

		currentMove.setNewPosition (x2, y2);
		if (x2 >= 0 && x2 <= 8 && y2 >= 0 && y2 <= 9 && !Arrays.equals (currentMove.getOriginalPosition (), currentMove.getNewPosition ())
			&& gameBoard [y1] [x1].validateMove (gameBoard, currentMove))
		    makeMove (currentMove);
		else
		{
		    currentPiece.setLocation (24 + x1 * 50, 25 + y1 * 50);
		    layeredPane.setLayer (currentPiece, 1);
		    currentPiece = null;
		    currentMove = null;
		}
	    }
	}
    }


    /**
     * interprets the user's mouse input as a move
     *
     * @param  e the mouse input
     */
    public void mouseReleased (MouseEvent e)
    {
	if ((isPlayerOneTurn && !players [0].isComputer ()) || (!isPlayerOneTurn && !players [1].isComputer ()))
	{
	    if (currentPiece == null) //if the piece has not been selected yet
	    {
		int x1 = (e.getX () - 24) / 50;
		int y1 = (e.getY () - 25) / 50;

		currentMove = new CCMove ();

		currentMove.setOriginalPosition (x1, y1);
		if (x1 >= 0 && x1 <= 8 && y1 >= 0 && y1 <= 9 && gameBoard [y1] [x1] != null &&
			((gameBoard [y1] [x1].isPlayerOne () && isPlayerOneTurn) || (!gameBoard [y1] [x1].isPlayerOne () && !isPlayerOneTurn)))
		{
		    currentPiece = gameBoard [y1] [x1].getIcon ();
		    layeredPane.setLayer (currentPiece, 2);
		    currentPiece.setLocation (e.getX () - 24, e.getY () - 25);
		}
		else
		    currentMove = null;
	    }
	    else //if a piece already has been selected.
	    {
		int x1 = currentMove.getOriginalPosition () [CCMove.X];
		int y1 = currentMove.getOriginalPosition () [CCMove.Y];
		int x2 = (e.getX () - 24) / 50;
		int y2 = (e.getY () - 25) / 50;

		currentMove.setNewPosition (x2, y2);
		if (x2 >= 0 && x2 <= 8 && y2 >= 0 && y2 <= 9 && !Arrays.equals (currentMove.getOriginalPosition (), currentMove.getNewPosition ())
			&& gameBoard [y1] [x1].validateMove (gameBoard, currentMove))
		    makeMove (currentMove);
		else
		{
		    currentPiece.setLocation (24 + x1 * 50, 25 + y1 * 50);
		    layeredPane.setLayer (currentPiece, 1);
		    currentPiece = null;
		    currentMove = null;
		}
	    }
	}
    }


    /**
     * graphical output of the user's mouse input
     *
     * @param  e the mouse input
     */
    public void mouseDragged (MouseEvent e)
    {
	if ((isPlayerOneTurn && !players [0].isComputer ()) || (!isPlayerOneTurn && !players [1].isComputer ()))
	{
	    if (currentPiece != null)
		currentPiece.setLocation (e.getX () - 24, e.getY () - 25);
	}
    }


    /**
     * graphical output of the user's mouse input
     *
     * @param  e the mouse input
     */
    public void mouseMoved (MouseEvent e)
    {
	if ((isPlayerOneTurn && !players [0].isComputer ()) || (!isPlayerOneTurn && !players [1].isComputer ()))
	{
	    if (currentPiece != null)
		currentPiece.setLocation (e.getX () - 24, e.getY () - 25);
	}
    }
}
