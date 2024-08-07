package Middle.IrComponents;

import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.FuncFParam;

import java.util.ArrayList;

public class IrFunction extends Value {
    private final String funcName;
    private final int funcType;       // 0: void, 1: int
    private final ArrayList<Value> params;

    private final ArrayList<BasicBlock> basicBlocks;

    public IrFunction(String funcName, int funcType, SymbolTable symbolTable) {
        this.funcName = funcName;
        this.funcType = funcType;
        this.params = new ArrayList<>();
        this.basicBlocks = new ArrayList<>();
    }

    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ");
        if (funcType == 0) {
            sb.append("void ");
        } else {
            sb.append("i32 ");
        }
        sb.append("@").append(funcName).append("(");
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).typeToLlvm()).append(" ").append(params.get(i).getName());
            if (i != params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(") #0 {\n");
        for (int i = 0; i < basicBlocks.size(); i++) {
            BasicBlock basicBlock = basicBlocks.get(i);
            if (basicBlock.isEmpty()) {
                continue;
            }
            // 第一个基本块不输出标签
            if (i != 0) {
                sb.append(basicBlock.getLabel()).append(": \n");
            }
            sb.append(basicBlock.toLlvmIr());
        }
        sb.append("}");
        return sb.toString();
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    public void addParams(ArrayList<FuncFParam> funcFParams, SymbolTable symbolTable) {
        int paramCount = 0;
        for (FuncFParam funcFParam : funcFParams) {
            Symbol symbol = symbolTable.findSymbol(funcFParam.getIdentifier());
            String irName = "%local_var_" + paramCount++;
            int varType = 1;
            int dim2 = 0;
            switch (funcFParam.getType()) {
                case 1 -> varType = 2;
                case 2 -> {
                    dim2 = Integer.parseInt(funcFParam.getExp2D().getValue(symbolTable));
                    varType = 4;
                }
            }
            Value value = new Value(irName, varType);
            value.setCol(dim2);
            symbol.setIrValue(value);
            this.params.add(value);
        }
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }

    public String allocLabel() {
        return "%Label_" + this.basicBlocks.size();
    }

    public int getParamsCnt() {
        return this.params.size();
    }

    public String getFuncName() {
        return funcName;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

}
