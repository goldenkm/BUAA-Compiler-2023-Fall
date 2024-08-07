package Frontend.SyntaxComponents;

import java.util.ArrayList;

public class Block implements Stmt{
    private ArrayList<BlockItem> blockItems;

    public Block() {
        this.blockItems = new ArrayList<>();
    }

    public void addBlockItem(BlockItem blockItem) {
        this.blockItems.add(blockItem);
    }
}
