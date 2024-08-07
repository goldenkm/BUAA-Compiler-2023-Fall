package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

/**
 * Return语句
 */
public class RetInstruction extends Instruction {
    private boolean isVoid;

    public RetInstruction(InstructionType type, Value result, boolean isVoid) {
        super(type, result);
        this.isVoid = isVoid;
    }

    @Override
    public String toLlvmIr() {
        if (isVoid) {
            return "ret void";
        }
        return "ret i32 " + super.getName();
    }
}
