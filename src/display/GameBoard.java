package display;

import audio.Audio;
import blocks.*;
import blocks.Block;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.*;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.Timer;

public class GameBoard extends JPanel implements Runnable, KeyListener {

  private final int delay;
  private Thread checker;

  // bounds for game board and tile size
  public static int leftBound;
  public static int rightBound;
  public static int topBound;
  public static int bottomBound;
  public static int tileSize;

  private Block currentBlock;
  protected Block nextBlock;

  private Map<ArrayList<Integer>, Color> placedBlocks = new LinkedHashMap<>();

  private boolean downKey;
  private boolean dropRotationHoldPrevention;

  private Timer blockMovingTimer;

  private boolean blockStopped;
  private int score;
  private int speed = 1;
  private boolean paused;

  // Player settings
  private boolean showGuideBlock;
  private Block guideBlock;

  private boolean musicOn;

  // Animations
  private boolean layerDeletionAnimationOn;
  private int layerDeletionAnimationDuration;
  private Integer[] layerDeletionLines;

  // Modifiers
  public boolean sticky;

  // Music
  private Audio music;
  private float musicVolume;

  public GameBoard() {
    // setting panel properties
    this.setPreferredSize(new Dimension(GameFrame.boardWidth + 1, GameFrame.boardHeight + 50));
    this.setBackground(Color.black);
    this.addKeyListener(this);
    this.setFocusable(true);

    // setting tile size and bounds
    leftBound = 0;
    rightBound = GameFrame.boardWidth;
    topBound = 25;
    bottomBound = GameFrame.boardHeight + 25;
    tileSize = GameFrame.tileSize;

    // filling map with all tiles on the board and color null
    for (int x = 0; x < GameFrame.boardWidth / tileSize; x++) {
      for (int y = 0; y < GameFrame.boardHeight / tileSize; y++) {
        placedBlocks.put(new ArrayList<>(Arrays.asList(x, y)), null);
      }
    }


    // Player settings
    if (GameFrame.players == null || GameFrame.players.get(GameFrame.playerPos).getShowGuideBlock()) {
      showGuideBlock = true;
    }

    if (GameFrame.players == null || GameFrame.players.get(GameFrame.playerPos).getMusicOn()) {
      musicOn = true;
    }

    // Modifiers settings
    if (display.Main.gameMenu.modifiers[3] == 1) {
      sticky = true;
    }

    // creating first block
    createNewBlock();

    // setting delay for block movement
    switch (GameFrame.level) {
      case 1:
        delay = 1100;
        break;
      case 2:
        delay = 700;
        break;
      case 3:
        delay = 400;
        break;
      default:
        delay = 500;
    }

    // creating timer for moving block
    blockMovingTimer =
        new Timer(
            delay,
            e -> {
              if (!paused) {
                currentBlock.moveDown();
                if (showGuideBlock) dropGuideBlock();
                score++;
              }
            });
    blockMovingTimer.start();

    // timer for changing speed
    new Timer(
            50000,
            e -> {
              blockMovingTimer.setDelay(blockMovingTimer.getDelay() - 20);
              speed++;
            })
        .start();

    // assigning and run thread for checking if block should be placed | and for
    // keyListener (should I make another thread for that?)
    checker = new Thread(this);
    checker.start();

    // play music
    if (musicOn) {
      if (GameFrame.players == null) musicVolume = -37.0f;
      else musicVolume = GameFrame.players.get(GameFrame.playerPos).getMusicVolume();
      try {
        music = new Audio();
        music.playMusic(0, musicVolume);
      } catch (LineUnavailableException e) {
          throw new RuntimeException(e);
        }
      }
    } // teleport bug when clicking left/right and rotate

  // drawing everything on the board
  @Override
  public void paintComponent(Graphics g) {
    // prevents repaint bugs
    super.paintComponent(g);

    // creating graphics2D variable and changing its color
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Color.blue);

    // drawing tiles and border
    // columns
    for (int x = 0; x <= GameFrame.boardWidth; x += tileSize) {
      g2.drawLine(x, 25, x, bottomBound);
    }
    // rows
    for (int y = 25; y <= GameFrame.boardHeight + 25; y += tileSize) {
      g2.drawLine(0, y, rightBound, y);
    }

    // drawing block
    for (int[] i : currentBlock.getPositions()) {
      g2.setColor(currentBlock.getColor());
      g2.fillRect(i[0], i[1], tileSize, tileSize);
      g2.setColor(Color.white);
      g2.drawRect(i[0], i[1], tileSize, tileSize);
    }

    // drawing guide block
    if (showGuideBlock && guideBlock != null) {
      for (int[] i : guideBlock.getPositions()) {
        g2.setColor(new Color(255, 255, 255, 100)); // White with transparency
        g2.fillRect(i[0], i[1], tileSize, tileSize);
        g2.setColor(Color.white); // Outline color
        g2.drawRect(i[0], i[1], tileSize, tileSize);
      }
    }

    // drawing placed blocks
    Set<ArrayList<Integer>> keys = placedBlocks.keySet();
    for (ArrayList<Integer> key : keys) {
      if (placedBlocks.get(key) == null) continue;
      g2.setColor(placedBlocks.get(key));
      g2.fillRect(key.get(0) * tileSize, key.get(1) * tileSize + 25, tileSize, tileSize);
      g2.setColor(Color.white);
      g2.drawRect(key.get(0) * tileSize, key.get(1) * tileSize + 25, tileSize, tileSize);
    }

    // Animation for layer deletion
    if (layerDeletionAnimationOn) {
      for (Integer y : layerDeletionLines) {
        // Calculate the current opacity based on the remaining duration
        int alpha = (int) (255 * (layerDeletionAnimationDuration / (float) 15));
        alpha = Math.max(0, Math.min(255, alpha)); // Clamp value between 0 and 255

        // Create a color with the calculated alpha (semi-transparent white)
        Color fadeColor = new Color(255, 0, 0, alpha);
        g2.setColor(fadeColor);

        // Draw a rectangle over the entire row
        g2.fillRect(0, y * tileSize + topBound, rightBound, tileSize);
      }

      layerDeletionAnimationDuration--;

      if (layerDeletionAnimationDuration == 0) {
        layerDeletionAnimationOn = false;
        destroyLayer(layerDeletionLines);
      }
    }

    // drawing "PAUSED" text if paused
    if (paused) {
      g2.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setFont(new Font("Arial Black", Font.PLAIN, 50));
      g2.drawString("PAUSED", rightBound / 2 - 113, GameFrame.boardHeight / 2 + 25);
    }
  }

  // checker thread
  @Override
  public void run() {

    // setting delay before block is placed after touching the ground or another
    // block
    int slideTime = 0;
    if (GameFrame.level == 1) {
      slideTime = 25;
    } else if (GameFrame.level == 2) {
      slideTime = 10;
    } else if (GameFrame.level == 3) {
      slideTime = 5;
    }

    // creating variables for game loop
    double checkInterval = (double) 1000000000 / 60; // 60 FPS
    double deltaCheck = 0;
    double moveInterval = (double) 1000000000 / 30; // 30 FPS
    double deltaMove = 0;
    long lastTime = System.nanoTime();
    long currentTime;

    // game loop
    while (checker != null) {
      currentTime = System.nanoTime();

      deltaCheck += (currentTime - lastTime) / checkInterval;
      deltaMove += (currentTime - lastTime) / moveInterval;

      lastTime = currentTime;

      if (deltaMove >= 1) {
        if (downKey) {
          currentBlock.moveDown();
          if (showGuideBlock) dropGuideBlock();
          score += 2;
        }
        deltaMove--;
      }

      if (deltaCheck >= 1) {
        if (paused) {
          continue;
        }
        repaint();

        if (sticky) {
          if (currentBlock.checkSticky()) {
            placeBlock();
          }
        }

        if (blockStopped) {
          placeBlock();
        }

        deltaCheck--;
      }
    }
  }

  protected void close() {
    checker = null;
    if (musicOn) music.stopMusic();
    placedBlocks.clear();
  }

  private void placeBlock() {
    // Play block place sound effect
    Audio.playEffect(2);

    Set<Integer> destroySet = new HashSet<>();
    for (int[] i : currentBlock.getPositions()) {
      int x = i[0] / tileSize;
      int y = (i[1] - 25) / tileSize;
      placedBlocks.put(new ArrayList<>(Arrays.asList(x, y)), currentBlock.getColor());
      if (checkLayer(y)) destroySet.add(y);
    }

    if (!destroySet.isEmpty()) {
      layerDeletionLines = (destroySet.toArray(new Integer[0]));
      layerDeletionAnimationOn = true;
      layerDeletionAnimationDuration = 15;
      Audio.playEffect(1);
    }
    if (checkGameOver()) gameOver();
    else createNewBlock();
    score += 8;

    // bug fix
    downKey = false;
  }

  private void gameOver() {
    if (musicOn) music.stopMusic();
    blockMovingTimer.stop();
    this.removeKeyListener(this);
    downKey = false;
    if (GameFrame.players != null) {
      if (GameFrame.players
              .get(GameFrame.playerPos)
              .getHighScore(GameFrame.level, GameFrame.gameMode)
          < score) {
        GameFrame.players
            .get(GameFrame.playerPos)
            .setHighScore(score, GameFrame.level, GameFrame.gameMode);
      }
      GameFrame.players
          .get(GameFrame.playerPos)
          .increaseGamesPlayed(score, GameFrame.level, GameFrame.gameMode);
      MenuFrame.serialize(GameFrame.players);
    }
    int answer =
        JOptionPane.showConfirmDialog(
            null, "Play again?", "you scored " + score, JOptionPane.YES_NO_OPTION);
    if (answer == 0) {
      playAgain();
    } else paused = true;
  }

  protected void playAgain() {
    placedBlocks.replaceAll((k, v) -> null);
    score = 0;
    speed = 1;
    currentBlock = null;
    this.removeKeyListener(this);
    this.addKeyListener(this);

    createNewBlock();

    blockMovingTimer.setDelay(delay);
    blockMovingTimer.start();
    if (musicOn) music.resetMusic();
  }

  private boolean checkGameOver() {
    for (int x = 0; x < rightBound / tileSize; x++) {
      if (placedBlocks.get(new ArrayList<>(Arrays.asList(x, 0))) != null) return true;
    }
    return false;
  }

  private boolean checkLayer(int layer) {
    int counter = 0;
    for (int x = 0; x < rightBound / tileSize; x++) {
      if (placedBlocks.get(new ArrayList<>(Arrays.asList(x, layer))) == null) break;
      counter++;
    }
    // System.out.println(
    // "Layer: " + layer + "\nCounter: " + counter + "\nR/T: " + rightBound /
    // tileSize);
    return counter == rightBound / tileSize;
  }

  private void destroyLayer(Integer[] layers) {
    int lines = layers.length;
    int max = layers[0];
    for (int i : layers) {
      for (int x = 0; x < rightBound / tileSize; x++) {
        placedBlocks.put(new ArrayList<>(Arrays.asList(x, i)), null);
      }
      if (i > max) max = i;
    }

    for (int i = max - lines; i >= 0; i--) {
      for (int x = 0; x < rightBound / tileSize; x++) {
        placedBlocks.put(
                new ArrayList<>(Arrays.asList(x, i + lines)),
                placedBlocks.get(new ArrayList<>(Arrays.asList(x, i))));
      }
    }
    score += lines * 40;
  }

  private void createNewBlock() {
    if (nextBlock != null) {
      currentBlock = nextBlock;
      currentBlock.create();
    }

    int blockNumber = new Random().nextInt(7);

    switch (blockNumber) {
      case 0:
        nextBlock = new Block_L();
        break;
      case 1:
        nextBlock = new Block_J();
        break;
      case 2:
        nextBlock = new Block_Z();
        break;
      case 3:
        nextBlock = new Block_S();
        break;
      case 4:
        nextBlock = new Block_T();
        break;
      case 5:
        nextBlock = new Block_O();
        break;
      case 6:
        nextBlock = new Block_I();
    }

    if (currentBlock == null) {
      createNewBlock();
    }
    if (GameFrame.nextBlockView != null) {
      GameFrame.nextBlockView.repaint();
    }

    if (showGuideBlock) createGuideBlock();

    flipBlockStopped();
  }

  private void createGuideBlock() {
    if (currentBlock instanceof Block_I) {
      guideBlock = new Block_I(currentBlock);
    } else if (currentBlock instanceof Block_L) {
      guideBlock = new Block_L(currentBlock);
    } else if (currentBlock instanceof Block_J) {
      guideBlock = new Block_J(currentBlock);
    } else if (currentBlock instanceof Block_Z) {
      guideBlock = new Block_Z(currentBlock);
    } else if (currentBlock instanceof Block_S) {
      guideBlock = new Block_S(currentBlock);
    } else if (currentBlock instanceof Block_T) {
      guideBlock = new Block_T(currentBlock);
    } else if (currentBlock instanceof Block_O) {
      guideBlock = new Block_O(currentBlock);
    }

    dropGuideBlock();
  }

  private void dropGuideBlock() {
    guideBlock.setPositions(currentBlock.clonePositions());

    // Drop the guide block to the bottom instantly
    if (guideBlock != null) {
      guideBlock.moveGuideBlockToPosition();
    }
  }


  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (!paused) {
      switch (e.getKeyCode()) {
        case 38: // up arrow - rotate
          if (dropRotationHoldPrevention) break;
          currentBlock.rotate();
          if (showGuideBlock) dropGuideBlock();
          dropRotationHoldPrevention = true;
          break;
        case 37: // left arrow - move left
          currentBlock.moveLeft();
          if (showGuideBlock) dropGuideBlock();
          break;
        case 39: // right arrow - move right
          currentBlock.moveRight();
          if (showGuideBlock) dropGuideBlock();
          break;
        case 40: // down arrow - speed up
          downKey = true;
          break;
        case 32: // space - instant drop
          if (dropRotationHoldPrevention) break;
          score += 3 * currentBlock.instantDrop();
          placeBlock();
          dropRotationHoldPrevention = true;
      }
    }
    if (e.getKeyCode() == 80) { // p letter - pause the game
      paused = !paused;
      if (musicOn) {
        if (paused) music.stopMusic();
        else music.resumeMusic();
      }
      repaint();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == 40) {
      downKey = false;
    }
    if (e.getKeyCode() == 38 || e.getKeyChar() == 32) {
      dropRotationHoldPrevention = false;
    }
  }

  public int getScore() {
    return this.score;
  }

  public int getSpeed() {
    return this.speed;
  }

  public void flipBlockStopped() {
    blockStopped = !blockStopped;
  }

  public boolean getBlockStopped() {
    return blockStopped;
  }

  public Map<ArrayList<Integer>, Color> getPlacedBlocks() {
    return placedBlocks;
  }
}
