////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCOOAI
//Description: The ai script for playing Chinese Chess
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

/**
 * The ai script for playing Chinese Chess
 */
public class CCOOAI extends Thread
{
    /** the depth to which the ai should calculate relative to its inital depth */
    private final static int DEEPNESS = 1;
    /** the intial depth relative to the DEEPNESS */
    private int firstDepth;
    /** boolean flag for whether ai script should stop or not */
    private boolean stopped = false;

    /** the next move to be made by the ai */
    private CCMove nextMove;
    /** boolean designating whether the ai should think for player one or two */
    private boolean playerOneTurn;
    /** the game board, or game contoller, that invokes this ai */
    private CCGameBoard game;
    /** the game board on which the game is being played, and the ai is to think for */
    private CCPieces gameBoard[] [];
    /** list of player one's pieces */
    private CCPieces playerOnePieces[];
    /** list of player two's pieces */
    private CCPieces playerTwoPieces[];

    /**
     * Constructs a new ai thread.
     *
     * @param  game      the game controller.
     * @param  gameBoard the game board.
     * @param  playerOne the player one of the game.
     * @param  playerTwo the player two of the game.
     */
    public CCOOAI (CCGameBoard game, CCPieces gameBoard[] [], CCPlayer playerOne, CCPlayer playerTwo)
    {
	this.game = game;
	this.gameBoard = gameBoard;
	playerOnePieces = playerOne.getPieces ();
	playerTwoPieces = playerTwo.getPieces ();
    }


    /**
     * run the ai script.
     */
    public void run ()
    {
	for (firstDepth = 1 ; nextMove == null && firstDepth >= -1 ; firstDepth--)
	{
	    if (playerOneTurn)
		findBestMove (gameBoard, firstDepth, playerOnePieces, playerTwoPieces);
	    else
		findBestMove (gameBoard, firstDepth, playerTwoPieces, playerOnePieces);
	}

	if (nextMove == null)
	    game.endGame (!playerOneTurn);
	if (!stopped && nextMove != null)
	    game.makeMove (nextMove);
    }


    /**
     * flag the script to stop
     */
    public void stopAI ()
    {
	stopped = true;
    }


    /**
     * flag the script to resume
     */
    public void resumeAI ()
    {
	stopped = false;
    }


    /**
     * get the ai to determine the next move.
     *
     * @param  playerOneTurn determines whether the ai should think for player one or not.
     */
    public void getNextMove (boolean playerOneTurn)
    {
	nextMove = null;
	resumeAI ();
	this.playerOneTurn = playerOneTurn;

	start ();
    }


    /**
     * calculates the next best move.
     *
     * @param  gameBoard    the game board on which the game is being played.
     * @param  depth        the depth at which the ai is currently analyzing, relative to DEEPNESS
     * @param  playerPieces the player's pieces
     * @param  enemyPieces  the enemy's pieces
     * @return              the best move's value
     */
    public int findBestMove (CCPieces gameBoard[] [], int depth, CCPieces playerPieces[], CCPieces enemyPieces[])
    {
	CCPieces tempBoard[] [];
	int currentMoveValue;
	int bestMoveValue = -999999;

	for (int i = 0 ; i < playerPieces.length && !stopped ; i++) //checking through the list of pieces.
	{
	    if (playerPieces [i].getLocation () [0] != -1 && playerPieces [i].getLocation () [1] != -1) //if the piece is not dead
	    {
		int x = playerPieces [i].getLocation () [0];
		int y = playerPieces [i].getLocation () [1];

		if (playerPieces [i].getPieceType () == CCPieces.GENERAL1)
		{
		    if (isThreatened (gameBoard, x, y, enemyPieces))
		    {
			nextMove = uncheckGeneral (gameBoard, playerPieces, enemyPieces, x, y);
			bestMoveValue = playerPieces [i].getPieceValue () * 6;
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.CANNON1 || playerPieces [i].getPieceType () == CCPieces.CHARIOT1
			|| playerPieces [i].getPieceType () == CCPieces.CANNON2 || playerPieces [i].getPieceType () == CCPieces.CHARIOT2)
		{
		    for (int k = 0 ; k < CCGameBoard.NUM_ROWS ; k++) //checks the vertical moves of chariots or cannons.
		    {
			currentMoveValue = -999999;
			if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, k)))
			{
			    if (gameBoard [k] [x] != null && isEnemyPiece (gameBoard [k] [x], enemyPieces)) //sees if any enemy piece is taken by moving here.
				currentMoveValue = gameBoard [k] [x].getPieceValue () * 3;
			    if (isThreatened (changeBoard (gameBoard, x, y, x, k), x, k, enemyPieces) && !isProtected (changeBoard (gameBoard, x, y, x, k), x, k, playerPieces)) //is threatened and not protected
				currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue () * 3;
			    else
			    {
				for (int l = 0 ; l < playerPieces.length ; l++) //checks to see if the piece threatens enemy pieces and protects friendly pieces
				{
				    tempBoard = changeBoard (gameBoard, x, y, x, k);
				    if (enemyPieces [l].getLocation () [0] != -1 && enemyPieces [l].getLocation () [1] != -1)
				    {
					if (playerPieces [i].validateMove (tempBoard, new CCMove (x, k, enemyPieces [l].getLocation () [0], enemyPieces [l].getLocation () [1])))
					    currentMoveValue = currentMoveValue + enemyPieces [l].getPieceValue () * 2;
				    }
				    if (playerPieces [l].getLocation () [0] != -1 && playerPieces [l].getLocation () [1] != -1)
				    {
					tempBoard [playerPieces [l].getLocation () [1]] [playerPieces [l].getLocation () [0]] = null;
					if (playerPieces [i].validateMove (tempBoard, new CCMove (x, k, playerPieces [l].getLocation () [0], playerPieces [l].getLocation () [1])))
					    currentMoveValue = currentMoveValue + playerPieces [l].getPieceValue ();
				    }
				}
			    }
			    if (depth > DEEPNESS) //if reached deepness, do not look deeper.
			    {
				if (currentMoveValue > bestMoveValue)
				    bestMoveValue = currentMoveValue;
			    }
			    else //look deeper
			    {
				if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x, k), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
				{
				    bestMoveValue = currentMoveValue;
				    if (depth == firstDepth) //if at initial depth, decide next move.
					nextMove = new CCMove (x, y, x, k);
				}
			    }
			}
		    }
		    for (int k = 0 ; k < CCGameBoard.NUM_COLS ; k++) //checks the horizontal moves of chariots or cannons.
		    {
			currentMoveValue = -999999;
			if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, k, y)))
			{
			    if (gameBoard [y] [k] != null && isEnemyPiece (gameBoard [y] [k], enemyPieces)) //sees if any enemy piece is taken by moving here.
				currentMoveValue = gameBoard [y] [k].getPieceValue () * 3;
			    if (isThreatened (changeBoard (gameBoard, x, y, k, y), k, y, enemyPieces) && !isProtected (changeBoard (gameBoard, x, y, k, y), k, y, playerPieces)) //is threatened and not protected
				currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue () * 3;
			    else
			    {
				for (int l = 0 ; l < playerPieces.length ; l++) //checks to see if the piece threatens enemy pieces and protects friendly pieces
				{
				    tempBoard = changeBoard (gameBoard, x, y, k, y);
				    if (enemyPieces [l].getLocation () [0] != -1 && enemyPieces [l].getLocation () [1] != -1)
				    {
					if (playerPieces [i].validateMove (tempBoard, new CCMove (k, y, enemyPieces [l].getLocation () [0], enemyPieces [l].getLocation () [1])))
					    currentMoveValue = currentMoveValue + enemyPieces [l].getPieceValue () * 2;
				    }
				    if (playerPieces [l].getLocation () [0] != -1 && playerPieces [l].getLocation () [1] != -1)
				    {
					tempBoard [playerPieces [l].getLocation () [1]] [playerPieces [l].getLocation () [0]] = null;
					if (playerPieces [i].validateMove (tempBoard, new CCMove (k, y, playerPieces [l].getLocation () [0], playerPieces [l].getLocation () [1])))
					    currentMoveValue = currentMoveValue + playerPieces [l].getPieceValue ();
				    }
				}
			    }
			    if (depth > DEEPNESS) //if reached deepness, do not look deeper.
			    {
				if (currentMoveValue > bestMoveValue)
				    bestMoveValue = currentMoveValue;
			    }
			    else //look deeper
			    {
				if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, k, y), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
				{
				    bestMoveValue = currentMoveValue;
				    if (depth == firstDepth) //if at initial depth, decide next move.
					nextMove = new CCMove (x, y, k, y);
				}
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.HORSE1 || playerPieces [i].getPieceType () == CCPieces.HORSE2)
		{
		    for (int k = x - 2 ; k <= x + 2 ; k++)
		    {
			for (int l = y - 2 ; l <= y + 2 ; l++)
			{
			    currentMoveValue = -999999;
			    if (k >= 0 && k <= 8 && l >= 0 && l <= 9 && ((Math.abs (l - i) == 2 && Math.abs (k - x) == 1) || (Math.abs (l - y) == 1 && Math.abs (k - x) == 2)))
			    {
				if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, k, l)))
				{
				    if (gameBoard [l] [k] != null && isEnemyPiece (gameBoard [l] [k], enemyPieces))
					currentMoveValue = gameBoard [l] [k].getPieceValue () * 3;
				    if (isThreatened (changeBoard (gameBoard, x, y, k, l), k, l, enemyPieces) && !isProtected (changeBoard (gameBoard, x, y, k, l), k, l, playerPieces))
					currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue () * 3;
				    else
				    {
					for (int m = 0 ; m < playerPieces.length ; m++)
					{
					    tempBoard = changeBoard (gameBoard, x, y, k, l);
					    if (enemyPieces [m].getLocation () [0] != -1 && enemyPieces [m].getLocation () [1] != -1)
					    {
						if (playerPieces [i].validateMove (tempBoard, new CCMove (k, l, enemyPieces [m].getLocation () [0], enemyPieces [m].getLocation () [1])))
						    currentMoveValue = currentMoveValue + enemyPieces [m].getPieceValue () * 2;
					    }
					    if (playerPieces [m].getLocation () [0] != -1 && playerPieces [m].getLocation () [1] != -1)
					    {
						tempBoard [playerPieces [m].getLocation () [1]] [playerPieces [m].getLocation () [0]] = null;
						if (playerPieces [i].validateMove (tempBoard, new CCMove (k, l, playerPieces [m].getLocation () [0], playerPieces [m].getLocation () [1])))
						    currentMoveValue = currentMoveValue + playerPieces [m].getPieceValue ();
					    }
					}
				    }
				    if (depth > DEEPNESS)
				    {
					if (currentMoveValue > bestMoveValue)
					    bestMoveValue = currentMoveValue;
				    }
				    else
				    {
					if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, k, l), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
					{
					    bestMoveValue = currentMoveValue;
					    if (depth == firstDepth)
						nextMove = new CCMove (x, y, k, l);
					}
				    }
				}
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.SOLDIER1 || playerPieces [i].getPieceType () == CCPieces.SOLDIER2)
		{
		    if (x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y] [x - 1] != null && isEnemyPiece (gameBoard [y] [x - 1], enemyPieces))
			    currentMoveValue = gameBoard [y] [x - 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x - 1, y), x - 1, y, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x - 1, y);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x - 1, y), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x - 1, y);
			    }
			}
		    }
		    if (x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y] [x + 1] != null && isEnemyPiece (gameBoard [y] [x + 1], enemyPieces))
			    currentMoveValue = gameBoard [y] [x + 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x + 1, y), x + 1, y, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x + 1, y);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x + 1, y), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x + 1, y);
			    }
			}
		    }
		    if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, y - 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y - 1] [x] != null && isEnemyPiece (gameBoard [y - 1] [x], enemyPieces))
			    currentMoveValue = gameBoard [y - 1] [x].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x, y - 1), x, y - 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x, y - 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x, y - 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x, y - 1);
			    }
			}
		    }
		    if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, y + 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y + 1] [x] != null && isEnemyPiece (gameBoard [y + 1] [x], enemyPieces))
			    currentMoveValue = gameBoard [y + 1] [x].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x, y + 1), x, y + 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x, y + 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x, y + 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x, y + 1);
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.ADVISOR1 || playerPieces [i].getPieceType () == CCPieces.ADVISOR2)
		{
		    if (y - 1 >= 0 && x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y - 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y - 1] [x - 1] != null && isEnemyPiece (gameBoard [y - 1] [x - 1], enemyPieces))
			    currentMoveValue = gameBoard [y - 1] [x - 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x - 1, y - 1), x - 1, y - 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x - 1, y - 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x - 1, y - 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x - 1, y - 1);
			    }
			}
		    }
		    if (y - 1 >= 0 && x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y - 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y - 1] [x + 1] != null && isEnemyPiece (gameBoard [y - 1] [x + 1], enemyPieces))
			    currentMoveValue = gameBoard [y - 1] [x + 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x + 1, y - 1), x + 1, y - 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x + 1, y - 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x + 1, y - 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x + 1, y - 1);
			    }
			}
		    }
		    if (y + 1 <= 9 && x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y + 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y + 1] [x + 1] != null && isEnemyPiece (gameBoard [y + 1] [x + 1], enemyPieces))
			    currentMoveValue = gameBoard [y + 1] [x + 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x + 1, y + 1), x + 1, y + 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x + 1, y + 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x + 1, y + 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x + 1, y + 1);
			    }
			}
		    }
		    if (y + 1 <= 9 && x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y + 1)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y + 1] [x - 1] != null && isEnemyPiece (gameBoard [y + 1] [x - 1], enemyPieces))
			    currentMoveValue = gameBoard [y + 1] [x - 1].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x - 1, y + 1), x - 1, y + 1, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x - 1, y + 1);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x - 1, y + 1), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x - 1, y + 1);
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.ELEPHANT1 || playerPieces [i].getPieceType () == CCPieces.ELEPHANT2)
		{
		    if (y - 2 >= 0 && x - 2 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 2, y - 2)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y - 2] [x - 2] != null && isEnemyPiece (gameBoard [y - 2] [x - 2], enemyPieces))
			    currentMoveValue = gameBoard [y - 2] [x - 2].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x - 2, y - 2), x - 2, y - 2, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x - 2, y - 2);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x - 2, y - 2), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x - 2, y - 2);
			    }
			}
		    }
		    if (y - 2 >= 0 && x + 2 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 2, y - 2)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y - 2] [x + 2] != null && isEnemyPiece (gameBoard [y - 2] [x + 2], enemyPieces))
			    currentMoveValue = gameBoard [y - 2] [x + 2].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x + 2, y - 2), x + 2, y - 2, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x + 2, y - 2);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x + 2, y - 2), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x + 2, y - 2);
			    }
			}
		    }
		    if (y + 2 <= 9 && x + 2 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 2, y + 2)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y + 2] [x + 2] != null && isEnemyPiece (gameBoard [y + 2] [x + 2], enemyPieces))
			    currentMoveValue = gameBoard [y + 2] [x + 2].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x + 2, y + 2), x + 2, y + 2, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x + 2, y + 2);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x + 2, y + 2), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x + 2, y + 2);
			    }
			}
		    }
		    if (y + 2 <= 9 && x - 2 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 2, y + 2)))
		    {
			currentMoveValue = -999999;
			if (gameBoard [y + 2] [x - 2] != null && isEnemyPiece (gameBoard [y + 2] [x - 2], enemyPieces))
			    currentMoveValue = gameBoard [y + 2] [x - 2].getPieceValue () * 3;
			if (isThreatened (changeBoard (gameBoard, x, y, x - 2, y + 2), x - 2, y + 2, enemyPieces))
			    currentMoveValue = currentMoveValue - playerPieces [i].getPieceValue ();
			else
			{
			    for (int j = 0 ; j < playerPieces.length ; j++)
			    {
				tempBoard = changeBoard (gameBoard, x, y, x - 2, y + 2);
				if (enemyPieces [j].getLocation () [0] != -1 && enemyPieces [j].getLocation () [1] != -1)
				{
				    if (playerPieces [j].validateMove (tempBoard, new CCMove (x, y, enemyPieces [j].getLocation () [0], enemyPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + enemyPieces [j].getPieceValue () * 2;
				}
				if (playerPieces [j].getLocation () [0] != -1 && playerPieces [j].getLocation () [1] != -1)
				{
				    tempBoard [playerPieces [j].getLocation () [1]] [playerPieces [j].getLocation () [0]] = null;
				    if (playerPieces [i].validateMove (tempBoard, new CCMove (x, y, playerPieces [j].getLocation () [0], playerPieces [j].getLocation () [1])))
					currentMoveValue = currentMoveValue + playerPieces [j].getPieceValue ();
				}
			    }
			}
			if (depth > DEEPNESS)
			{
			    if (currentMoveValue > bestMoveValue)
				bestMoveValue = currentMoveValue;
			}
			else
			{
			    if ((currentMoveValue = currentMoveValue - findBestMove (changeBoard (gameBoard, x, y, x - 2, y + 2), depth + 1, enemyPieces, playerPieces)) > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				if (depth == firstDepth)
				    nextMove = new CCMove (x, y, x - 2, y + 2);
			    }
			}
		    }
		}
	    }

	    //System.out.println (bestMoveValue);
	    //if (nextMove != null)
	    //    System.out.println (nextMove.toString ());
	}

	return bestMoveValue;
    }


    /**
     * calculates the best move for getting out of check
     *
     * @param  gameBoard    the game board on which the game is being played.
     * @param  playerPieces the player's pieces
     * @param  enemyPieces  the enemy's pieces
     * @param  generalX     the general's x coordinate
     * @param  generalY     the general's y coordinate
     * @return              the best move for getting out of check.
     */
    public static CCMove uncheckGeneral (CCPieces gameBoard[] [], CCPieces playerPieces[], CCPieces enemyPieces[], int generalX, int generalY)
    {
	int currentMoveValue;         //current move's value
	int bestMoveValue = -9999999; //the best move's value
	CCMove bestMove = null;

	for (int i = 0 ; i < playerPieces.length ; i++) //goes through each of the player's pieces
	{
	    if (playerPieces [i].getLocation () [0] != -1 && playerPieces [i].getLocation () [1] != -1)
	    {
		int x = playerPieces [i].getLocation () [0];
		int y = playerPieces [i].getLocation () [1];

		if (playerPieces [i].getPieceValue () == CCPieces.ELEPHANT1 || playerPieces [i].getPieceValue () == CCPieces.ELEPHANT2)
		{
		    if (x - 2 >= 0 && y - 2 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 2, y - 2)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x - 2, y - 2), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y - 2] [x - 2] != null && isEnemyPiece (gameBoard [y - 2] [x - 2], enemyPieces))
				currentMoveValue = gameBoard [y - 2] [x - 2].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x - 2, y - 2);
			    }
			}
		    }
		    if (x - 2 >= 0 && y + 2 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 2, y - 2)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x + 2, y - 2), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y - 2] [x + 2] != null && isEnemyPiece (gameBoard [y - 2] [x + 2], enemyPieces))
				currentMoveValue = gameBoard [y - 2] [x + 2].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x + 2, y - 2);
			    }
			}
		    }
		    if (y + 2 <= 9 && x - 2 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 2, y + 2)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x - 2, y + 2), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y + 2] [x - 2] != null && isEnemyPiece (gameBoard [y + 2] [x - 2], enemyPieces))
				currentMoveValue = gameBoard [y + 2] [x - 2].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x - 2, y + 2);
			    }
			}
		    }
		    if (y + 2 <= 9 && x + 2 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 2, y + 2)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x + 2, y + 2), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y + 2] [x + 2] != null && isEnemyPiece (gameBoard [y + 2] [x + 2], enemyPieces))
				currentMoveValue = gameBoard [y + 2] [x + 2].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x + 2, y + 2);
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.ADVISOR1 || playerPieces [i].getPieceType () == CCPieces.ADVISOR2)
		{
		    if (y - 1 >= 0 && x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y - 1)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x - 1, y - 1), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y - 1] [x - 1] != null && isEnemyPiece (gameBoard [y - 1] [x - 1], enemyPieces))
				currentMoveValue = gameBoard [y - 1] [x - 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x - 1, y - 1);
			    }
			}
		    }
		    if (y - 1 <= 9 && x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y - 1)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x + 1, y - 1), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y - 1] [x + 1] != null && isEnemyPiece (gameBoard [y - 1] [x + 1], enemyPieces))
				currentMoveValue = gameBoard [y - 1] [x + 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x + 1, y - 1);
			    }
			}
		    }
		    if (y + 1 <= 9 && x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y + 1)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x - 1, y + 1), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y + 1] [x - 1] != null && isEnemyPiece (gameBoard [y + 1] [x - 1], enemyPieces))
				currentMoveValue = gameBoard [y + 1] [x - 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x - 1, y + 1);
			    }
			}
		    }
		    if (y + 1 <= 9 && x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y + 1)))
		    {
			currentMoveValue = -9999;
			if (!isThreatened (changeBoard (gameBoard, x, y, x + 1, y + 1), generalX, generalY, enemyPieces))
			{
			    if (gameBoard [y + 1] [x + 1] != null && isEnemyPiece (gameBoard [y + 1] [x + 1], enemyPieces))
				currentMoveValue = gameBoard [y + 1] [x + 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x + 1, y + 1);
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.HORSE1 || playerPieces [i].getPieceType () == CCPieces.HORSE2)
		{
		    for (int k = y - 2 ; k <= y + 2 ; k++) //rows
		    {
			for (int l = x - 2 ; l <= x + 2 ; l++) //columns
			{
			    if (l >= 0 && l <= 8 && k >= 0 && k <= 9 && ((Math.abs (x - l) == 2 && Math.abs (y - k) == 1) || (Math.abs (x - l) == 1 && Math.abs (y - k) == 2)))
			    {
				currentMoveValue = -99999;
				if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, l, k))
					&& !isThreatened (changeBoard (gameBoard, x, y, l, k), generalX, generalY, enemyPieces))
				{
				    if (gameBoard [k] [l] != null && isEnemyPiece (gameBoard [k] [l], enemyPieces))
					currentMoveValue = gameBoard [k] [l].getPieceValue ();
				    if (currentMoveValue > bestMoveValue)
				    {
					bestMoveValue = currentMoveValue;
					bestMove = new CCMove (x, y, l, k);
				    }
				}
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.CANNON1 || playerPieces [i].getPieceType () == CCPieces.CANNON2 ||
			playerPieces [i].getPieceType () == CCPieces.CHARIOT1 || playerPieces [i].getPieceType () == CCPieces.CHARIOT2)
		{
		    for (int k = 0 ; k < CCGameBoard.NUM_ROWS ; k++)
		    {
			if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, k))
				&& !isThreatened (changeBoard (gameBoard, x, y, x, k), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [k] [x] != null && isEnemyPiece (gameBoard [k] [x], enemyPieces))
				currentMoveValue = gameBoard [k] [x].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x, k);
			    }
			}
		    }
		    for (int k = 0 ; k < CCGameBoard.NUM_COLS ; k++)
		    {
			if (playerPieces [i].validateMove (gameBoard, new CCMove (x, y, k, y))
				&& !isThreatened (changeBoard (gameBoard, x, y, k, y), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [y] [k] != null && isEnemyPiece (gameBoard [y] [k], enemyPieces))
				currentMoveValue = gameBoard [y] [k].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, k, y);
			    }
			}
		    }
		}

		if (playerPieces [i].getPieceType () == CCPieces.GENERAL1 || playerPieces [i].getPieceType () == CCPieces.GENERAL2)
		{
		    if (y - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, y - 1)))
		    {
			if (!isThreatened (changeBoard (gameBoard, x, y, x, y - 1), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [y - 1] [x] != null && isEnemyPiece (gameBoard [y - 1] [x], enemyPieces))
				currentMoveValue = gameBoard [y - 1] [x].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x, y - 1);
			    }
			}
		    }
		    if (x + 1 <= 8 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x + 1, y)))
		    {
			if (!isThreatened (changeBoard (gameBoard, x, y, x + 1, y), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [y] [x + 1] != null && isEnemyPiece (gameBoard [y] [x + 1], enemyPieces))
				currentMoveValue = gameBoard [y] [x + 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x + 1, y);
			    }
			}
		    }
		    if (y + 1 <= 9 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x, y + 1)))
		    {
			if (!isThreatened (changeBoard (gameBoard, x, y, x, y + 1), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [y + 1] [x] != null && isEnemyPiece (gameBoard [y + 1] [x], enemyPieces))
				currentMoveValue = gameBoard [y + 1] [x].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x, y + 1);
			    }
			}
		    }
		    if (x - 1 >= 0 && playerPieces [i].validateMove (gameBoard, new CCMove (x, y, x - 1, y)))
		    {
			if (!isThreatened (changeBoard (gameBoard, x, y, x - 1, y), generalX, generalY, enemyPieces))
			{
			    currentMoveValue = -999999;
			    if (gameBoard [y] [x - 1] != null && isEnemyPiece (gameBoard [y] [x - 1], enemyPieces))
				currentMoveValue = gameBoard [y] [x - 1].getPieceValue ();
			    if (currentMoveValue > bestMoveValue)
			    {
				bestMoveValue = currentMoveValue;
				bestMove = new CCMove (x, y, x - 1, y);
			    }
			}
		    }
		}
	    }
	}

	return bestMove;
    }


    /**
     * creates a new game board with minor changes from the original for calculating future board situations.
     *
     * @param  gameBoard the game board with the original positions.
     * @param  x1        the x coordinate of the original position.
     * @param  y1        the y coordinate of the original position.
     * @param  x2        the x coordinate of the new position.
     * @param  y2        the y coordinate of the new position.
     * @return           the new game board.
     */
    public static CCPieces[] [] changeBoard (CCPieces gameBoard[] [], int x1, int y1, int x2, int y2)
    {
	CCPieces newGameBoard[] [] = new CCPieces [CCGameBoard.NUM_ROWS] [CCGameBoard.NUM_COLS];

	for (int i = 0 ; i < CCGameBoard.NUM_ROWS ; i++)
	{
	    for (int j = 0 ; j < CCGameBoard.NUM_COLS ; j++)
		newGameBoard [i] [j] = gameBoard [i] [j];
	}

	newGameBoard [y2] [x2] = newGameBoard [y1] [x1];
	newGameBoard [y1] [x1] = null;

	return newGameBoard;
    }


    /**
     * determines whether the piece is an enemy piece.
     *
     * @param  piece       the piece to be determined.
     * @param  enemyPieces the list of enemy pieces.
     * @return             boolean whether the piece is an enemy piece or not.
     */
    public static boolean isEnemyPiece (CCPieces piece, CCPieces enemyPieces[])
    {
	for (int i = 0 ; i < enemyPieces.length ; i++)
	{
	    if (piece == enemyPieces [i])
		return true;
	}

	return false;
    }


    /**
     * determines whether the piece is protected
     *
     * @param  gameBoard    the game board.
     * @param  x            the x coordinate of the piece.
     * @param  y            the y coordinate of the piece.
     * @param  playerPieces the player's friendly pieces that can potentially protect the piece
     * @return              boolean whether the piece is protected.
     */
    public boolean isProtected (CCPieces gameBoard[] [], int x, int y, CCPieces playerPieces[])
    {
	gameBoard = changeBoard (gameBoard, 0, 0, 0, 0); //creates copy of gameBoard
	new CCSoldier (gameBoard, !(playerPieces == playerOnePieces), x, y);

	boolean playerOne = playerPieces [0].isPlayerOne ();

	for (int i = 0 ; i < CCGameBoard.NUM_ROWS ; i++)
	{
	    for (int j = 0 ; j < CCGameBoard.NUM_COLS ; j++)
	    {
		if (gameBoard [i] [j] != null && gameBoard [i] [j].isPlayerOne () == playerOne)
		{
		    if (gameBoard [i] [j].validateMove (gameBoard, new CCMove (j, i, x, y)))
			return true;
		}
	    }
	}

	return false;
    }


    /**
     * determines whether the piece is threatened
     *
     * @param  gameBoard   the game board.
     * @param  x           the x coordinate of the piece.
     * @param  y           the y coordinate of the piece.
     * @param  enemyPieces the enemy's pieces that can potentially threaten the piece
     * @return             boolean whether the piece is threatened.
     */
    public static boolean isThreatened (CCPieces gameBoard[] [], int x, int y, CCPieces enemyPieces[])
    {
	boolean playerOne = enemyPieces [0].isPlayerOne ();

	for (int i = 0 ; i < CCGameBoard.NUM_ROWS ; i++)
	{
	    for (int j = 0 ; j < CCGameBoard.NUM_COLS ; j++)
	    {
		if (gameBoard [i] [j] != null && gameBoard [i] [j].isPlayerOne () == playerOne)
		{
		    if (gameBoard [i] [j].validateMove (gameBoard, new CCMove (j, i, x, y)))
			return true;
		}
	    }
	}

	return false;
    }
}
