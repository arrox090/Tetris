package blocks;

import display.GameBoard;

import java.awt.*;

public class Block_O extends Block {

    public Block_O() {
        super();
        this.color = Color.yellow;
    }

    public Block_O(Block other) {
        super(other);
    }

    @Override
    public void create() {
        this.positions[0] = new int[]{x, y};
        //  oo
        //  xo
        this.positions[1] = new int[]{x + GameBoard.tileSize, y};
        //  oo
        //  ox
        this.positions[2] = new int[]{x, y - GameBoard.tileSize};
        //  xo
        //  oo
        this.positions[3] = new int[]{x + GameBoard.tileSize, y - GameBoard.tileSize};
        //  ox
        //  oo
    }

    @Override
    public void rotate() {}

    @Override
    public int getWidth() {
        return 2;
    }

    @Override
    public int getHeight() {
        return 2;
    }
}
