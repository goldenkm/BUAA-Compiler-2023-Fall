package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

/**
 * Return语句
 */
public class RetInstruction extends Instruction {
    private final boolean isVoid;

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

    public boolean isVoid() {
        return isVoid;
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        used.add(getResult());
        return used;
    }
}
