package Frontend.SyntaxComponents.AllStmt;

import java.util.ArrayList;

public class Block implements Stmt {
    private ArrayList<BlockItem> blockItems;
    private int endLine;

    public Block() {
        this.blockItems = new ArrayList<>();
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
}
