package blocks;

import display.GameBoard;

import java.awt.*;

public class Block_S extends Block {

    private int rotation = 1;

    public Block_S() {
        super();
        this.color = Color.green;
    }

    public Block_S(Block other) {
        super(other);
    }

    @Override
    public void create() {
        this.positions[0] = new int[]{x, y};
        //  oo
        // ox
        this.positions[1] = new int[]{x - GameBoard.tileSize, y};
        //  oo
        // xo
        this.positions[2] = new int[]{x, y - GameBoard.tileSize};
        //  xo
        // oo
        this.positions[3] = new int[]{x + GameBoard.tileSize, y - GameBoard.tileSize};
        //  ox
        // oo
    }

    @Override
    public void rotate() {
        int[][] temp = clonePositions();

        int centerX = this.positions[0][0];
        int centerY = this.positions[0][1];

        if (rotation == 1) {
            temp[1][0] = centerX;
            temp[1][1] = centerY - GameBoard.tileSize;
            temp[2][0] = centerX + GameBoard.tileSize;
            temp[2][1] = centerY;
            temp[3][0] = centerX + GameBoard.tileSize;
            temp[3][1] = centerY + GameBoard.tileSize;

            temp = canMove(temp, DOWN, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 2;
            }
        }

        else if (rotation == 2) {
            temp[1][0] = centerX + GameBoard.tileSize;
            temp[1][1] = centerY;
            temp[2][0] = centerX;
            temp[2][1] = centerY + GameBoard.tileSize;
            temp[3][0] = centerX - GameBoard.tileSize;
            temp[3][1] = centerY + GameBoard.tileSize;

            temp = canMove(temp, LEFT, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 3;
            }
        }

        else if (rotation == 3) {
            temp[1][0] = centerX;
            temp[1][1] = centerY + GameBoard.tileSize;
            temp[2][0] = centerX - GameBoard.tileSize;
            temp[2][1] = centerY;
            temp[3][0] = centerX - GameBoard.tileSize;
            temp[3][1] = centerY - GameBoard.tileSize;

            temp = canMove(temp, DOWN, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 4;
            }
        }

        else if (rotation == 4) {
            temp[1][0] = centerX - GameBoard.tileSize;
            temp[1][1] = centerY;
            temp[2][0] = centerX;
            temp[2][1] = centerY - GameBoard.tileSize;
            temp[3][0] = centerX + GameBoard.tileSize;
            temp[3][1] = centerY - GameBoard.tileSize;

            temp = canMove(temp, RIGHT, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 1;
            }
        }
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 2;
    }
}
