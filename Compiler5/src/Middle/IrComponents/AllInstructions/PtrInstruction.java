package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class PtrInstruction extends Instruction {
    private final Value pointer;
    private final Value index1;
    private Value index2;

    public PtrInstruction(InstructionType type, Value result, Value pointer, Value index1) {
        super(type, result);
        this.pointer = pointer;
        this.index1 = index1;
    }

    public PtrInstruction(InstructionType type, Value result, Value pointer, Value index1, Value index2) {
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
            sb.append(index1.getName()).append(", i32 ").append(index2.getName());
        } else {
            assert index1 != null;
            sb.append(index1.getName());
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

    public String getIndex1() {
        return index1.getName();
    }

    public String getIndex2() {
        if (index2 == null) {
            return null;
        }
        return index2.getName();
    }

    public Value getPointer() {
        return pointer;
    }

    public int getDim2() {
        return pointer.getCol();
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        used.add(pointer);
        used.add(index1);
        if (index2 != null) {
            used.add(index2);
        }
        return used;
    }
}
