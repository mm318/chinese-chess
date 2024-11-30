////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCGameProperties
//Description: Game properties window
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Game properties window
 */
public class CCGameProperties
{
    /** the frame in which the properties are displayed */
    private JFrame frame;
    /** the text area in which player one's move history is displayed */
    private TextArea area1;
    /** the text area in which player two's move history is displayed */
    private TextArea area2;

    /**
     * Constructs a new game properties window
     */
    public CCGameProperties ()
    {
	JFrame.setDefaultLookAndFeelDecorated (false);
	frame = new JFrame ("Game Properties");
	frame.setDefaultCloseOperation (WindowConstants.HIDE_ON_CLOSE);

	frame.getContentPane ().setLayout (new BoxLayout (frame.getContentPane (), BoxLayout.Y_AXIS));
	frame.getContentPane ().add (new JLabel ("Player One Moves", SwingConstants.CENTER));
	area1 = new TextArea ("", 10, 17, TextArea.SCROLLBARS_VERTICAL_ONLY);
	area1.setEditable (false);
	frame.getContentPane ().add (area1);

	frame.getContentPane ().add (new JLabel ("Player Two Moves", SwingConstants.CENTER));
	area2 = new TextArea ("", 10, 17, TextArea.SCROLLBARS_VERTICAL_ONLY);
	area2.setEditable (false);
	frame.getContentPane ().add (area2);

	frame.pack ();
	frame.setResizable (false);
	frame.setVisible (false);
    }


    /**
     * Adds text to the text area, representing the move made.
     *
     * @param  playerOne the player whose text area to which the move should be added.
     * @param  move      the move to be added.
     */
    public void addMove (boolean playerOne, CCMove move)
    {
	int x1 = move.getOriginalPosition () [CCMove.X];
	int y1 = move.getOriginalPosition () [CCMove.Y];
	int x2 = move.getNewPosition () [CCMove.X];
	int y2 = move.getNewPosition () [CCMove.Y];

	if (playerOne)
	    area1.append ((x1 + 1) + ", " + (y1 + 1) + " -> " + (x2 + 1) + ", " + (y2 + 1) + "\n");
	else
	    area2.append ((x1 + 1) + ", " + (y1 + 1) + " -> " + (x2 + 1) + ", " + (y2 + 1) + "\n");
    }


    /**
     * Destroys the display window.
     */
    public void dispose ()
    {
	frame.dispose ();
    }


    /**
     * Toggles the visibility of the window.
     */
    public void showhide ()
    {
	if (frame.isVisible ())
	    frame.setVisible (false);
	else
	    frame.setVisible (true);
    }
}
