package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllStmt.Block;

import java.util.ArrayList;

public class FuncDef {
    private String identifier;
    private int funcType;
    private ArrayList<FuncFParam> funcFParams;
    private Block block;

    public FuncDef(String identifier, int funcType) {
        this.identifier = identifier;
        this.funcType = funcType;
        this.funcFParams = new ArrayList<>();
    }

    public void addFuncFParam(FuncFParam param) {
        this.funcFParams.add(param);
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public int getEndLine() {
        return this.block.getEndLine();
    }
}
