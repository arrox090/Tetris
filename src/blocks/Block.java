package blocks;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import display.*;


public abstract class Block {
    protected int x, y;
    protected int[][] positions;
    protected Color color;

    protected final int DOWN = 1;
    protected final int RIGHT = 2;
    protected final int LEFT = 3;

    public Block() {
        int temp = GameFrame.boardWidth / GameBoard.tileSize;
        this.x = (temp / 2) * GameBoard.tileSize - GameBoard.tileSize;
        this.y = 25 + GameBoard.tileSize;
        this.positions = new int[4][1];
    }

    // Guide Block constructor
    public Block(Block other) {
        this.color = other.color;
        this.positions = other.clonePositions();
    }

    public abstract void create();
    public abstract void rotate();
    public abstract int getWidth();
    public abstract int getHeight();

    public int[][] clonePositions() {
        int[][] positionsCopy = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            positionsCopy[i] = this.positions[i].clone(); // Clone each position
        }
        return positionsCopy;
    }

    public int instantDrop()  {
        int counter = 0;
        if (GameFrame.gameBoard == null) {
            return counter;
        }
        while (!GameFrame.gameBoard.getBlockStopped()) {
            this.moveDown();
            if (GameFrame.gameBoard.sticky) {
                if (this.checkSticky()) {
                    break;
                }
            }
            counter++;
        }
        return counter;
    }

    public void moveGuideBlockToPosition()  {
        if (GameFrame.gameBoard == null) {
            return;
        }
        while (this.moveGuide()) {
            if (GameFrame.gameBoard.sticky) {
                if (this.checkSticky()) {
                    return;
                }
            }
        }
    }

    public boolean moveGuide() {
        int[][] temp = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            temp[i] = this.positions[i].clone();
            temp[i][1] += GameBoard.tileSize;
        }
        temp = canMove(temp, DOWN, false, true);
        if (temp == null) return false;
        else this.positions = temp;
        return true;
    }

    public void moveDown() {
        int[][] temp = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            temp[i] = this.positions[i].clone();
            temp[i][1] += GameBoard.tileSize;
        }
        temp = canMove(temp, DOWN, false, true);
        if (temp != null) this.positions = temp;
        else GameFrame.gameBoard.flipBlockStopped();
    }

    public void moveRight() {
        int[][] temp = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            temp[i] = this.positions[i].clone();
            temp[i][0] += GameBoard.tileSize;
        }
        temp = canMove(temp, RIGHT, false, true);
        if (temp != null) this.positions = temp;
    }

    public void moveLeft() {
        int[][] temp = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            temp[i] = this.positions[i].clone();
            temp[i][0] -= GameBoard.tileSize;
        }
        temp = canMove(temp, LEFT, false, true);
        if (temp != null) this.positions = temp;
    }

    protected int[][] canMove(int[][] pos, int direction, boolean checkedOpposite, boolean first) {
        int[][] temp = new int[this.positions.length][1];
        for (int i = 0; i < this.positions.length; i++) {
            temp[i] = pos[i].clone();
        }

        // down
        if (direction == DOWN) {
            for (int[] i : pos) {
                ArrayList<Integer> tile = new ArrayList<>(Arrays.asList(i[0] / GameBoard.tileSize, (i[1] - 25) / GameBoard.tileSize));
                if (i[1] >= GameBoard.bottomBound || GameFrame.gameBoard.getPlacedBlocks().get(tile) != null) {
                    return null;
                }
            }
        }

        // right
        else if (direction == RIGHT) {
            boolean invades = false;
            for (int[] i : temp) {
                ArrayList<Integer> tile = new ArrayList<>(Arrays.asList(i[0] / GameBoard.tileSize, (i[1] - 25) / GameBoard.tileSize));
                if (i[0] >= GameBoard.rightBound || GameFrame.gameBoard.getPlacedBlocks().get(tile) != null) {
                    invades = true;
                }
                i[0] -= GameBoard.tileSize;
            }
            if (invades) {
                if (checkedOpposite) return null;
                return canMove(temp, RIGHT, false, false);
            }
            else {
                if (checkedOpposite || first) return pos;
                return canMove(pos, LEFT, true, false);
            }
        }

        // left
        else if (direction == LEFT) {
            boolean invades = false;
            for (int[] i : temp) {
                ArrayList<Integer> tile = new ArrayList<>(Arrays.asList(i[0] / GameBoard.tileSize, (i[1] - 25) / GameBoard.tileSize));
                if (i[0] < GameBoard.leftBound || GameFrame.gameBoard.getPlacedBlocks().get(tile) != null) {
                    invades = true;
                }
                i[0] += GameBoard.tileSize;
            }
            if (invades) {
                if (checkedOpposite) return null;
                return canMove(temp, LEFT, false, false);
            }
            else {
                if (checkedOpposite || first) return pos;
                return canMove(pos, RIGHT, true, false);
            }
        }
        return pos;
    }


    // modifiers
    public boolean checkSticky() {
        ArrayList<ArrayList<Integer>> b = new ArrayList<>();

        for (int[] i : positions) {
            b.add(new ArrayList<>(Arrays.asList(i[0], i[1])));
        }

        for (ArrayList<Integer> arr : b) {
            int temp2 = (arr.get(0) - GameBoard.tileSize) / GameBoard.tileSize;
            Color temp = GameFrame.gameBoard.getPlacedBlocks().get(new ArrayList<>(Arrays.asList(temp2, arr.get(1) / GameBoard.tileSize)));

            if (temp != null & temp2 >= 0 && !b.contains(Arrays.asList(temp2 * GameBoard.tileSize, arr.get(1))) && temp.equals(color)) {
                return true;
            }
            temp2 = (arr.get(0) + GameBoard.tileSize) / GameBoard.tileSize;
            temp = GameFrame.gameBoard.getPlacedBlocks().get(new ArrayList<>(Arrays.asList(temp2, arr.get(1) / GameBoard.tileSize)));

            if (temp != null && temp2 < GameBoard.rightBound / GameBoard.tileSize && !b.contains(Arrays.asList(temp2 * GameBoard.tileSize, arr.get(1))) && temp.equals(color)) {
                return true;
            }
            temp2 = (arr.get(1) - GameBoard.tileSize) / GameBoard.tileSize;
            temp = GameFrame.gameBoard.getPlacedBlocks().get(new ArrayList<>(Arrays.asList(arr.get(0) / GameBoard.tileSize, temp2)));

            if (temp != null && temp2 >= 0 && !b.contains(Arrays.asList(arr.get(0), temp2 * GameBoard.tileSize)) && temp.equals(color)) {
                return true;
            }
            temp2 = (arr.get(1) + GameBoard.tileSize) / GameBoard.tileSize;
            temp = GameFrame.gameBoard.getPlacedBlocks().get(new ArrayList<>(Arrays.asList(arr.get(0) / GameBoard.tileSize, temp2)));

            if (temp != null && temp2 < GameBoard.bottomBound / GameBoard.tileSize  && !b.contains(Arrays.asList(arr.get(0), temp2 * GameBoard.tileSize)) && temp.equals(color)) {
                return true;
            }
        }

        return false;
    }


    public int[][] getPositions() {
        return this.positions;
    }

    public Color getColor() {
        return this.color;
    }


    public void setPositions(int[][] ints) {
        this.positions = ints;
    }
}
