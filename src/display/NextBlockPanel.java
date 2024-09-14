package display;

import blocks.Block_I;
import blocks.Block_J;
import blocks.Block_Z;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

import static display.GameFrame.tileSize;

public class NextBlockPanel extends JPanel {

    private GameBoard gameBoard;

    public NextBlockPanel(GameBoard gameBoard) {
        setBounds(25, 430, 150, 150);
        setBackground(Color.black);
        this.gameBoard = gameBoard;
    }


    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.white);
        g2.drawRect(0, 0, 149, 149);
        gameBoard.nextBlock.create();

        if (gameBoard.nextBlock != null) {
            g2.setStroke(new BasicStroke(1));
            int x = (150 - (tileSize * gameBoard.nextBlock.getWidth())) / 2;
            int y = (150 - (tileSize * gameBoard.nextBlock.getHeight())) / 2 + tileSize;
            int[] lastPos = new int[] {-1, -1};
            int xdiff = 0;
            int ydiff = 0;

            int[][] positions = gameBoard.nextBlock.getPositions();
            Arrays.sort(positions, new Comparator<int[]>() {
                @Override
                public int compare(int[] a, int[] b) {
                    // Compare based on the first element of each row
                    return Integer.compare(a[0], b[0]);
                }
            });
            if (gameBoard.nextBlock instanceof Block_I || gameBoard.nextBlock instanceof Block_Z) {
                y -= tileSize;
            }

            for (int[] i : positions) {
                g2.setColor(gameBoard.nextBlock.getColor());
                if (lastPos[0] == -1) {
                    g2.fillRect(x, y, tileSize, tileSize);
                    g2.setColor(Color.white);
                    g2.drawRect(x, y, tileSize, tileSize);

                    lastPos = i;
                    continue;
                }
                xdiff += i[0] - lastPos[0];
                ydiff += i[1] - lastPos[1];

                g2.fillRect(x + xdiff, y + ydiff, tileSize, tileSize);
                g2.setColor(Color.white);
                g2.drawRect(x + xdiff, y + ydiff, tileSize, tileSize);

                lastPos = i;
            }
        }
    }

}
