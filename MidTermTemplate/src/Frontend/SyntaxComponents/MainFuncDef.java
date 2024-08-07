package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllStmt.Block;

public class MainFuncDef {
    private Block block;

    public MainFuncDef() {
    }

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
