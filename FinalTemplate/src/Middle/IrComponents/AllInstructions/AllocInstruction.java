package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class AllocInstruction extends Instruction {
    public AllocInstruction(InstructionType type, Value result) {
        super(type, result);
    }

    @Override
    public String toLlvmIr() {
        String type = super.getResult().typeToLlvm();
        if (type.charAt(type.length() - 1) == '*') {
            return super.getName() + " = alloca " + type.substring(0, type.length() - 1);
        }
        return super.getName() + " = alloca " + type;
    }

    @Override
    public int getVarType() {
        return super.getResult().getVarType();
    }

    @Override
    public String typeToLlvm() {
        return super.getResult().typeToLlvm();
    }

    @Override
    public int getRow() {
        return getResult().getRow();
    }

    @Override
    public int getCol() {
        return super.getResult().getCol();
    }

    @Override
    public ArrayList<Value> getUsedValues() {   // 没有用到的value
        return new ArrayList<>();
    }
}
