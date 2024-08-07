package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

public class PtrInstruction extends Instruction {
    private final Value pointer;
    private String index1;
    private String index2;

    public PtrInstruction(InstructionType type, Value result, Value pointer, String index1) {
        super(type, result);
        this.pointer = pointer;
        this.index1 = index1;
    }

    public PtrInstruction(InstructionType type, Value result, Value pointer, String index1, String index2) {
        super(type, result);
        this.pointer = pointer;
        this.index1 = index1;
        this.index2 = index2;
    }

    @Override
    public String toLlvmIr() {
        String type = pointer.typeToLlvm();
        String retType = type.substring(0, type.length() - 1);
        StringBuilder sb;
        if (!isFuncParam()) {
             sb = new StringBuilder(
                    super.getName() + " = getelementptr " + retType
                            + ", " + type + " " + pointer.getName() + ", i32 0, i32 ");
        } else {
            sb = new StringBuilder(super.getName() + " = getelementptr " + retType
                    + ", " + type + " " + pointer.getName() + ", i32 ");
        }
        if (index1 != null && index2 != null) {     // 二维
            sb.append(index1).append(", i32 ").append(index2);
        } else {
            sb.append(index1);
        }
        return sb.toString();
    }

    private boolean isFuncParam() {
        return pointer instanceof MemInstruction;
    }

    @Override
    public int getVarType() {
        return super.getResult().getVarType();
    }

    @Override
    public String typeToLlvm() {
        return super.getResult().typeToLlvm();
    }

    public void setIndex2(String index2) {
        this.index2 = index2;
    }

    public String getIndex1() {
        return index1;
    }

    public String getIndex2() {
        return index2;
    }

    public Value getPointer() {
        return pointer;
    }

    public int getDim2() {
        return pointer.getCol();
    }
}
