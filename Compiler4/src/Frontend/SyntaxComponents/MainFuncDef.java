package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllStmt.Block;

public class MainFuncDef{
    private Block block;
    private int sonTableId;

    public MainFuncDef() {
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public void setSonTableId(int sonTableId) {
        this.sonTableId = sonTableId;
    }

    public int getSonTableId() {
        return sonTableId;
    }
}
