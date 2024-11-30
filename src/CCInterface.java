////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCInterface
//Description: The class from which the software executes.
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * The class from which the software executes.
 */
public class CCInterface {
  /** the main window of the software */
  private static JFrame frame;

  /** control bar component of the window */
  private CCControlBar controlBar;
  /** the game board component of the window */
  private CCGameBoard gameBoard;
  /** the status bar component of the window */
  private JLabel statusBar = new JLabel("Welcome to Chinese Chess");

  private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

  /**
   * Constructs a new window of the software.
   */
  private CCInterface() {
    JFrame.setDefaultLookAndFeelDecorated(false);
    frame = new JFrame("Chinese Chess");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gameBoard = new CCGameBoard(); //new game board component
    controlBar = new CCControlBar(gameBoard, frame, statusBar); // new control bar component
    statusBar.setPreferredSize(new Dimension((int) gameBoard.getSize().getWidth(), 16));

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(controlBar, BorderLayout.NORTH);
    frame.getContentPane().add(gameBoard, BorderLayout.CENTER);
    frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

    frame.pack();
    frame.setSize((int) gameBoard.getPreferredSize().getWidth(), (int) frame.getSize().getHeight());
    frame.setLocation(screenSize.width / 2 - (frame.getPreferredSize().width / 2), screenSize.height / 2 - (frame.getPreferredSize().height / 2));
    frame.setResizable(false);
    frame.setVisible(true);

    displaySplashScreen();
  }


  /**
   * Displays a splash screen for a little duration.
   */
  public static void displaySplashScreen() {
    JWindow splashScreen = new JWindow(frame);

    splashScreen.getContentPane().setLayout(new BorderLayout(2, 2));
    splashScreen.getContentPane().add(new JLabel("Chinese Chess v1.0", SwingConstants.CENTER), BorderLayout.NORTH);
    splashScreen.getContentPane().add(new JLabel("Coded By: Miaofei Mei", SwingConstants.CENTER), BorderLayout.SOUTH);

    splashScreen.pack();
    splashScreen.setLocation(screenSize.width / 2 - (splashScreen.getPreferredSize().width / 2), screenSize.height / 2 - (splashScreen.getPreferredSize().height / 2));
    splashScreen.setVisible(true);

    try { //delay for the display of the splash screen
      Thread.sleep(2000);
    } catch (Exception e) {
    }

    splashScreen.dispose();
  }


  /**
   * The start of the software execution.
   */
  public static void main(String args[]) {
    new CCInterface();
  }
}
