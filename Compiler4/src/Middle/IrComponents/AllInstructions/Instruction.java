package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

public class Instruction extends Value {
    private InstructionType type;
    private Value result;

    public Instruction(InstructionType type, Value result) {
        this.type = type;
        this.result = result;
    }


    public String toLlvmIr() {
        return "This function should not be called";
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
}
