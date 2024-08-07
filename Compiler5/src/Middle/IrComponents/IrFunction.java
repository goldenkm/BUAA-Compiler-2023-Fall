package Middle.IrComponents;

import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.FuncFParam;
import Middle.IrComponents.AllInstructions.BrInstruction;
import Middle.IrComponents.AllInstructions.FuncInstruction;
import Middle.IrComponents.AllInstructions.Instruction;
import Middle.IrComponents.AllInstructions.PrintInstruction;
import Middle.IrComponents.AllInstructions.RetInstruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        for (int i = 0; i < basicBlocks.size(); i++) {      // 删除空的基本块
            if (basicBlocks.get(i).isEmpty()) {
                basicBlocks.remove(i);
                i--;
            }
        }
        ArrayList<BasicBlock> optBlocks = optimizeBasicBlock();
        for (int i = 0; i < optBlocks.size(); i++) {
            BasicBlock basicBlock = optBlocks.get(i);
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

    public int getParamsCnt() {
        return this.params.size();
    }

    public String getFuncName() {
        return funcName;
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    //基本块优化
    private ArrayList<BasicBlock> optimizeBasicBlock() {
        // 构建前驱集合
        HashMap<BasicBlock, ArrayList<BasicBlock>> pre = new HashMap<>();
        // 构建后继集合
        HashMap<BasicBlock, ArrayList<BasicBlock>> after = new HashMap<>();
        for (BasicBlock basicBlock : basicBlocks) {
            pre.put(basicBlock, new ArrayList<>());
            after.put(basicBlock, new ArrayList<>());
        }
        // 构建数据块之间的流图
        for (int i = 0; i < basicBlocks.size() - 1; i++) {
            BasicBlock block = basicBlocks.get(i);
            Instruction brInstr = block.getLastInstruction();
            if (brInstr instanceof BrInstruction) {
                if (((BrInstruction) brInstr).getCond() == null) {
                    String dst = ((BrInstruction) brInstr).getDest();
                    pre.get(getBlockByLabel(dst)).add(block);
                    after.get(block).add(getBlockByLabel(dst));
                } else {
                    BasicBlock trueDst = getBlockByLabel(((BrInstruction) brInstr).getTrueLabel());
                    BasicBlock falseDst = getBlockByLabel(((BrInstruction) brInstr).getFalseLabel());
                    pre.get(trueDst).add(block);
                    after.get(block).add(trueDst);
                    pre.get(falseDst).add(block);
                    after.get(block).add(falseDst);
                }
            } else if (!(brInstr instanceof RetInstruction)) {
                System.out.println(block.getLabel()+"!");
                throw new RuntimeException("基本块的最后一条语句不是跳转语句！");
            }
        }
        ArrayList<BasicBlock> mergedBasicBlocks = new ArrayList<>();
        for (int i = 0; i < basicBlocks.size(); i++) {
            BasicBlock block = basicBlocks.get(i);
            if (pre.get(block).size() == 0 && i > 0) {      // 删除没有前驱的基本块
                continue;
            }
            if (after.get(block).size() != 1) {      // 只有无条件跳转语句有用
                mergedBasicBlocks.add(block);
                continue;
            }
            BasicBlock afterBlock = after.get(block).get(0);
            // 只有该基本块和他的后继块相邻，并且该后继块只有它一个前驱时才能合并
            if (isAdjacent(block, afterBlock) && pre.get(afterBlock).size() == 1) {
                basicBlocks.remove(i + 1);
                mergeTwoBlock(block, afterBlock);
                //将该基本块的后继设置为后继块的后继
                after.put(block, after.get(afterBlock));
                i--;
                continue;
            }
            mergedBasicBlocks.add(block);
        }
        return mergedBasicBlocks;
    }

    private BasicBlock getBlockByLabel(String label) {
        for (BasicBlock basicBlock : basicBlocks) {
            if (basicBlock.getLabel().equals(label)) {
                return basicBlock;
            }
        }
        throw new RuntimeException("该标签没有对应的基本块：" + label);
    }

    private boolean isAdjacent(BasicBlock block1, BasicBlock block2) {
        for (int i = 0; i < basicBlocks.size() - 1; i++) {
            if (basicBlocks.get(i).equals(block1)) {
                return block2.equals(basicBlocks.get(i + 1));
            }
        }
        return false;
    }

    private void mergeTwoBlock(BasicBlock block1, BasicBlock block2) {
        block1.removeLastInstruction();
        block1.addAllInstr(block2.getInstructions());
    }

    private HashMap<Instruction, Boolean> deleteDeadCode(ArrayList<Instruction> instructions) {
        ArrayList<Instruction> usefulCode = new ArrayList<>();
        HashMap<Instruction, Boolean> visited = new HashMap<>();
        for (Instruction instruction : instructions) {
            if (instruction instanceof RetInstruction || instruction instanceof BrInstruction
                    || instruction instanceof PrintInstruction) {
                usefulCode.add(instruction);
                visited.put(instruction, false);
            } else if (instruction instanceof FuncInstruction) {
                if (((FuncInstruction) instruction).isVoid()) {
                    usefulCode.add(instruction);
                    visited.put(instruction, false);
                }
            }
        }
        while (visited.containsValue(false)) {
            for (int i = 0; i < usefulCode.size(); i++) {
                Instruction instruction = usefulCode.get(i);
                if (visited.get(instruction)) {
                    continue;
                }
                for (Value usedValue : instruction.getUsedValues()) {
                    if (usedValue instanceof Instruction) {     // 扩大有用代码集
                        usefulCode.add((Instruction) usedValue);
                        visited.put((Instruction) usedValue, false);
                    }
                }
                visited.put(instruction, true);
            }
        }
        return visited;
    }
}
