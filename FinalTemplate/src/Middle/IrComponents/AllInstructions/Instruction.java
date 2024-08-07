package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class Instruction extends Value {
    private final InstructionType type;
    private final Value result;

    public Instruction(InstructionType type, Value result) {
        this.type = type;
        this.result = result;
    }


    public String toLlvmIr() {
        return "This function should not be called";
    }

    public ArrayList<Value> getUsedValues() {
        System.out.println("This function should not be called!");
        return new ArrayList<>();
    }

    public InstructionType getType() {
        return type;
    }

    @Override
    public String getName() {
        return result.getName();
    }

    public Value getResult() {
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Instruction) {
            return ((Instruction) obj).toLlvmIr().equals(this.toLlvmIr());
        }
        return false;
    }
}
