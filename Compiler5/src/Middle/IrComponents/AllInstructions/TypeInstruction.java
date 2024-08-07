package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class TypeInstruction extends Instruction {
    private final Value value;

    public TypeInstruction(InstructionType type, Value result, Value value) {
        super(type, result);
        this.value = value;
    }

    @Override
    public String toLlvmIr() {
        switch (super.getType()) {
            case ZEXT -> {
                return super.getName() + " = zext i1 " + value.getName() + " to i32";
            }
            case TRUNC -> {
                return super.getName() + " = trunc i32 " + value.getName() + " to i1";
            }
            default -> throw new RuntimeException("Error in TypeInstruction");
        }
    }

    public Value getValue() {
        return value;
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        used.add(value);
        return used;
    }
}
