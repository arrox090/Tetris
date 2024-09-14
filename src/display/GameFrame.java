package display;

import data.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;

public class GameFrame {

  public static int gameMode;
  protected static NextBlockPanel nextBlockView;
  private JFrame frame;
  protected static final ImageIcon LOGO = new ImageIcon("app_logo.png");
  protected static int level;
  protected static int playerPos;
  protected static ArrayList<Player> players;

  public static int boardHeight;
  public static int boardWidth;
  protected static int tileSize;

  public static GameBoard gameBoard;

  public GameFrame(int level, int gameMode, ArrayList<Player> players, int playerPos) {
    frame = new JFrame("Tetris");
    frame.setIconImage(LOGO.getImage());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(Color.black);

    if (gameMode == 1) setupModifiers();
    else {
      boardHeight = 600;
      boardWidth = 300;
      tileSize = 30;
    }

    GameFrame.level = level;
    GameFrame.gameMode = gameMode;
    GameFrame.players = players;
    GameFrame.playerPos = playerPos;

    JMenuBar menuBar = new JMenuBar();
    frame.add(menuBar);
    frame.setJMenuBar(menuBar);

    JMenu game = new JMenu("Game");
    menuBar.add(game);

    JPanel rightPanel = new JPanel();
    rightPanel.setPreferredSize(new Dimension(200, boardHeight + 50));
    rightPanel.setLayout(null);
    rightPanel.setBackground(Color.black);
    frame.add(rightPanel, BorderLayout.EAST);

    JLabel scoreView = new JLabel("Score: 0");
    scoreView.setFont(new Font(null, Font.BOLD, 20));
    scoreView.setForeground(Color.white);
    scoreView.setBounds(0, 20, 200, 30);
    scoreView.setHorizontalAlignment(JLabel.CENTER);
    rightPanel.add(scoreView);

    JLabel speedView = new JLabel("Speed: 1");
    speedView.setFont(new Font(null, Font.BOLD, 20));
    speedView.setForeground(Color.white);
    speedView.setBounds(0, 70, 200, 30);
    speedView.setHorizontalAlignment(JLabel.CENTER);
    rightPanel.add(speedView);

    gameBoard = new GameBoard();
    frame.add(gameBoard, BorderLayout.CENTER);

    if (players == null || players.get(playerPos).getShowNextBlock()) {
      JLabel nextBlockText = new JLabel("Next Block");
      nextBlockText.setBounds(25, 390, 150, 40);
      nextBlockText.setFont(new Font(null, Font.BOLD, 27));
      nextBlockText.setForeground(Color.white);
      rightPanel.add(nextBlockText);

      nextBlockView = new NextBlockPanel(gameBoard);
      rightPanel.add(nextBlockView);
    }

    JMenuItem restart = new JMenuItem("Restart", KeyEvent.VK_R);
    restart.addActionListener(e -> gameBoard.playAgain());
    game.add(restart);

    JMenuItem goMenu = new JMenuItem("Menu", KeyEvent.VK_M);
    goMenu.addActionListener(
        e -> {
          gameBoard.close();
          Main.gameMenu.rerun();
          frame.dispose();
        });
    game.add(goMenu);

    JPanel leftPanel = new JPanel();
    leftPanel.setPreferredSize(new Dimension(150, boardHeight + 50));
    leftPanel.setBackground(Color.black);
    frame.add(leftPanel, BorderLayout.WEST);

    Timer timer =
        new Timer(
            200,
            e -> {
              scoreView.setText("Score: " + gameBoard.getScore());
              speedView.setText("Speed: " + gameBoard.getSpeed());
            });
    timer.start();

    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  } // block places in the air after coming back to menu and starting new game

  private void setupModifiers() {
    if (Main.gameMenu.modifiers[1] == 0) {
      switch (Main.gameMenu.modifiers[0]) {
        case 0:
          boardWidth = 300;
          break;
        case 1:
          boardWidth = 450;
          break;
        case 2:
          boardWidth = 600;
      }
      boardHeight = 600;
      tileSize = 30;
    } else if (Main.gameMenu.modifiers[1] == 1) {
      switch (Main.gameMenu.modifiers[0]) {
        case 0:
          boardWidth = 250;
          break;
        case 1:
          boardWidth = 375;
          break;
        case 2:
          boardWidth = 500;
      }
      boardHeight = 625;
      tileSize = 25;
    } else if (Main.gameMenu.modifiers[1] == 2) {
      switch (Main.gameMenu.modifiers[0]) {
        case 0:
          boardWidth = 200;
          break;
        case 1:
          boardWidth = 300;
          break;
        case 2:
          boardWidth = 400;
      }
      boardHeight = 600;
      tileSize = 20;
    }

    // more modifiers
  }
}
