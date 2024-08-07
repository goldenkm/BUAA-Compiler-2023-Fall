package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

public class GetIntInstruction extends Instruction {
    public GetIntInstruction(InstructionType type, Value result) {
        super(type, result);
    }

    @Override
    public String toLlvmIr() {
        return super.getName() + " = call i32 @getint()";
    }
}
