package Frontend.SyntaxComponents.AllStmt;

import java.util.ArrayList;

public class Block implements Stmt {
    private ArrayList<BlockItem> blockItems;
    private int endLine;
    private final int symbolTableId;

    public Block(int symbolTableId) {
        this.blockItems = new ArrayList<>();
        this.symbolTableId = symbolTableId;
    }

    public void addBlockItem(BlockItem blockItem) {
        this.blockItems.add(blockItem);
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public boolean hasLastReturnStmt() {
        if (blockItems.size() == 0) {
            return false;
        }
        BlockItem lastItem = blockItems.get(blockItems.size() - 1);
        if (lastItem instanceof Stmt) {
            if (lastItem instanceof ReturnStmt) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public int getSymbolTableId() {
        return symbolTableId;
    }
}
