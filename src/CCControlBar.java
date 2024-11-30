////////////////////////////////////////////////////////////////////////////////
//Programmer:  Miaofei Mei
//School:      Victoria Park Collegiate Institute
//IDE Used:    Ready to Program 1.7
//Class:       CCControlBar
//Description: Control bar of the Chinese Chess software
//Modified:    March 19, 2006
////////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;

/**
 * Control bar of the Chinese Chess software.
 */
public class CCControlBar extends JPanel {
  /** gameboard over which the control bar controls */
  private CCGameBoard gameBoard;
  /** the parent of the control bar */
  private JFrame owner;
  /** the status bar that displays the buttons' descriptions */
  private JLabel statusBar;
  /** dialog object of potential pop up dialogs resulting from a command from the control bar */
  private JDialog dialog;

  /** the buttons on this control bar */
  private JButton buttons[] = new JButton[6];
  /** the list of images used by the buttons of this control bar */
  private static URL imageList[] = new URL[6];
  /** list of button descriptions */
  private final static String buttonDescriptions[] = {"Stop current game and start a new game.",
                                                      "Load a game or replay file.", "Undo last move.", "Redo next move.",
                                                      "Save current game as an unfinished game or replay.",
                                                      "Display additional game properties."
                                                     };
  /** button type constants */
  private final static int STOP = 0, LOAD = 1, UNDO = 2, REDO = 3, SAVE = 4, PROPERTIES = 5;

  /**
   * Initializes the URLS in the image list for the images of the buttons.
   */
  public void initializeImageList() {
    try {
      imageList[0] = getClass().getResource("images/control/Stop.png");
      imageList[1] = getClass().getResource("images/control/Load.png");
      imageList[2] = getClass().getResource("images/control/Undo.png");
      imageList[3] = getClass().getResource("images/control/Redo.png");
      imageList[4] = getClass().getResource("images/control/Save.png");
      imageList[5] = getClass().getResource("images/control/Properties.png");
    } catch (Exception e) {
      System.out.println(e);
    }
  }


  /**
   * Constructs this piece.
   *
   * @param  board     the board over which the control bar controls.
   * @param  owner     the parent of the control bar.
   * @param  statusBar the status bar that displays the buttons' descriptions.
   */
  public CCControlBar(CCGameBoard gameBoard, JFrame owner, JLabel statusBar) {
    this.gameBoard = gameBoard;
    this.owner = owner;
    this.statusBar = statusBar;

    initializeImageList();

    setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Color buttonBackground = new Color(193, 137, 88);
    for (int i = 0 ; i < 6 ; i = i + 1) { //setting up the buttons.
      buttons[i] = new JButton(new ImageIcon(imageList[i]));
      buttons[i].setBackground(buttonBackground);
      buttons[i].addMouseListener(new controlButtonListener(i));
      add(buttons[i]);
    }
  }


  /**
   * mouse listener for each of the buttons.
   */
  class controlButtonListener implements MouseListener {
    /** button type identifier */
    int buttonType;

    controlButtonListener(int buttonType) {
      this.buttonType = buttonType;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
      statusBar.setText(buttonDescriptions[buttonType]); //sets the text on the status bar
    }

    public void mouseExited(MouseEvent e) {
      statusBar.setText("");
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
      if (buttonType == STOP) {
        //destroying previous dialog and creating a new create new game dialog
        if (dialog != null) {
          dialog.hide();
          dialog.dispose();
        }

        dialog = new JDialog(owner);
        dialog.setTitle("Create New Game");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JRadioButton humanOne = new JRadioButton("Human", true);
        humanOne.setActionCommand("Human");
        JRadioButton computerOne = new JRadioButton("Computer");
        computerOne.setActionCommand("Computer");
        ButtonGroup playerOne = new ButtonGroup();
        playerOne.add(humanOne);
        playerOne.add(computerOne);

        JRadioButton humanTwo = new JRadioButton("Human", true);
        humanTwo.setActionCommand("Human");
        JRadioButton computerTwo = new JRadioButton("Computer");
        computerTwo.setActionCommand("Computer");
        ButtonGroup playerTwo = new ButtonGroup();
        playerTwo.add(humanTwo);
        playerTwo.add(computerTwo);

        JButton ok = new JButton("OK");
        ok.addMouseListener(new okButtonListener(dialog, playerOne, playerTwo));
        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new cancelButtonListener(dialog));

        dialog.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        dialog.getContentPane().add(new JLabel("Player One:"));
        dialog.getContentPane().add(humanOne);
        dialog.getContentPane().add(computerOne);
        dialog.getContentPane().add(new JLabel("Player Two:"));
        dialog.getContentPane().add(humanTwo);
        dialog.getContentPane().add(computerTwo);
        dialog.getContentPane().add(ok);
        dialog.getContentPane().add(cancel);

        dialog.resize(250, 115);
        dialog.setResizable(false);
        dialog.setVisible(true);
      } else if (buttonType == LOAD) {
        //destroying previous dialog and creating a new load game dialog
        if (dialog != null) {
          dialog.hide();
          dialog.dispose();
        }

        dialog = new JDialog(owner);
        dialog.setTitle("Load Game");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextField textfield = new JTextField(35);
        JButton save = new JButton("Load");
        save.addMouseListener(new saveloadButtonListener(dialog, textfield, false));
        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new cancelButtonListener(dialog));

        dialog.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        dialog.getContentPane().add(new JLabel("Load game: "));
        dialog.getContentPane().add(textfield);
        dialog.getContentPane().add(save);
        dialog.getContentPane().add(cancel);

        dialog.resize(400, 100);
        dialog.setResizable(false);
        dialog.setVisible(true);
      } else if (buttonType == UNDO) {
        gameBoard.undoMove(); //to undo a move
      } else if (buttonType == REDO) {
        gameBoard.redoMove(); //to redo a move
      } else if (buttonType == SAVE) {
        //destroying previous dialog and creating a new save game dialog
        if (dialog != null) {
          dialog.hide();
          dialog.dispose();
        }

        dialog = new JDialog(owner);
        dialog.setTitle("Save Game");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JTextField textfield = new JTextField(35);
        JButton save = new JButton("Save");
        save.addMouseListener(new saveloadButtonListener(dialog, textfield, true));
        JButton cancel = new JButton("Cancel");
        cancel.addMouseListener(new cancelButtonListener(dialog));

        dialog.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
        dialog.getContentPane().add(new JLabel("Save game as: "));
        dialog.getContentPane().add(textfield);
        dialog.getContentPane().add(save);
        dialog.getContentPane().add(cancel);

        dialog.resize(400, 100);
        dialog.setResizable(false);
        dialog.setVisible(true);
      } else if (buttonType == PROPERTIES) {
        if (gameBoard != null) {
          gameBoard.toggleProperties();
        }
      }
    }
  }


  /**
   * mouse listener for the creating new game dialog, which creates a new game.
   */
  class okButtonListener implements MouseListener {
    JDialog parent;
    ButtonGroup playerOne;
    ButtonGroup playerTwo;

    public okButtonListener(JDialog parent, ButtonGroup playerOne, ButtonGroup playerTwo) {
      this.parent = parent;
      this.playerOne = playerOne;
      this.playerTwo = playerTwo;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
      boolean playerOneHuman = true;
      boolean playerTwoHuman = true;

      if (playerOne.getSelection().getActionCommand().equals("Human")) {
        playerOneHuman = true;
      } else if (playerOne.getSelection().getActionCommand().equals("Computer")) {
        playerOneHuman = false;
      }

      if (playerTwo.getSelection().getActionCommand().equals("Human")) {
        playerTwoHuman = true;
      } else if (playerTwo.getSelection().getActionCommand().equals("Computer")) {
        playerTwoHuman = false;
      }

      gameBoard.startNewGame(playerOneHuman, playerTwoHuman); //starts the new game
      parent.hide();
      parent.dispose();
    }
  }


  /**
   * mouse listener for any button that destroys its parent
   */
  class cancelButtonListener implements MouseListener {
    JDialog parent;

    public cancelButtonListener(JDialog parent) {
      this.parent = parent;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
      parent.hide();
      parent.dispose();
    }
  }


  /**
   * mouse listener the saving or loading a game buttons
   */
  class saveloadButtonListener implements MouseListener {
    JTextField inputter;
    JDialog error;
    JDialog parent;
    boolean save;

    public saveloadButtonListener(JDialog parent, JTextField inputter, boolean save) {
      this.inputter = inputter;
      this.parent = parent;
      this.save = save;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
      try { //tries to load or save a game.
        if (save) {
          gameBoard.saveGame(inputter.getText());
        } else {
          gameBoard.loadGame(inputter.getText());
        }
        parent.hide();
        parent.dispose();
      } catch (Exception exception) { //displays error dialog if catches error.
        if (error != null) {
          error.hide();
          error.dispose();
        }

        error = new JDialog(parent);
        error.setTitle("Error");
        error.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JButton ok = new JButton("OK");
        ok.addMouseListener(new cancelButtonListener(error));

        error.getContentPane().setLayout(new BorderLayout());
        error.getContentPane().add(new JLabel(exception.toString()), BorderLayout.CENTER);
        error.getContentPane().add(ok, BorderLayout.SOUTH);

        error.pack();
        error.setResizable(false);
        error.setVisible(true);
      }
    }
  }
}
