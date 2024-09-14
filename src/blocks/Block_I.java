package blocks;

import display.GameBoard;

import java.awt.*;

public class Block_I extends Block {

    private int rotation = 1;

    public Block_I() {
        super();
        this.color = Color.cyan;
    }

    public Block_I(Block other) {
        super(other);
    }

    @Override
    public void create() {
        this.positions[0] = new int[]{x, y};
        // oxoo
        this.positions[1] = new int[]{x - GameBoard.tileSize, y};
        // xooo
        this.positions[2] = new int[]{x + GameBoard.tileSize, y};
        // ooxo
        this.positions[3] = new int[]{x + 2 * GameBoard.tileSize, y};
        // ooox
    }

    @Override
    public void rotate() {
        int[][] temp = clonePositions();

        int centerX = this.positions[0][0];
        int centerY = this.positions[0][1];

        if (rotation == 1) {
            temp[0][0] = centerX + GameBoard.tileSize;
            temp[0][1] = centerY;
            temp[1][0] = centerX + GameBoard.tileSize;
            temp[1][1] = centerY - GameBoard.tileSize;
            temp[2][0] = centerX + GameBoard.tileSize;
            temp[2][1] = centerY + GameBoard.tileSize;
            temp[3][0] = centerX + GameBoard.tileSize;
            temp[3][1] = centerY + 2 * GameBoard.tileSize;

            temp = canMove(temp, DOWN, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 2;
            }
        }

        else if (rotation == 2) {
            temp[0][0] = centerX;
            temp[0][1] = centerY + GameBoard.tileSize;
            temp[1][0] = centerX + GameBoard.tileSize;
            temp[1][1] = centerY + GameBoard.tileSize;
            temp[2][0] = centerX - GameBoard.tileSize;
            temp[2][1] = centerY + GameBoard.tileSize;
            temp[3][0] = centerX - 2 * GameBoard.tileSize;
            temp[3][1] = centerY + GameBoard.tileSize;

            temp = canMove(temp, LEFT, false, true);
            temp = canMove(temp, RIGHT, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 3;
            }
        }

        else if (rotation == 3) {
            temp[0][0] = centerX - GameBoard.tileSize;
            temp[0][1] = centerY;
            temp[1][0] = centerX - GameBoard.tileSize;
            temp[1][1] = centerY + GameBoard.tileSize;
            temp[2][0] = centerX - GameBoard.tileSize;
            temp[2][1] = centerY - GameBoard.tileSize;
            temp[3][0] = centerX - GameBoard.tileSize;
            temp[3][1] = centerY - 2 * GameBoard.tileSize;

            temp = canMove(temp, DOWN, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 4;
            }
        }

        else if (rotation == 4) {
            temp[0][0] = centerX;
            temp[0][1] = centerY - GameBoard.tileSize;
            temp[1][0] = centerX - GameBoard.tileSize;
            temp[1][1] = centerY - GameBoard.tileSize;
            temp[2][0] = centerX + GameBoard.tileSize;
            temp[2][1] = centerY - GameBoard.tileSize;
            temp[3][0] = centerX + 2 * GameBoard.tileSize;
            temp[3][1] = centerY - GameBoard.tileSize;

            temp = canMove(temp, RIGHT, false, true);
            temp = canMove(temp, LEFT, false, true);
            if (temp != null) {
                this.positions = temp;
                rotation = 1;
            }
        }
    }

    @Override
    public int getWidth() {
        return 4;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
