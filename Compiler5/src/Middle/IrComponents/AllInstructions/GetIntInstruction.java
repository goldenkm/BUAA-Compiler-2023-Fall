package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class GetIntInstruction extends Instruction {
    public GetIntInstruction(InstructionType type, Value result) {
        super(type, result);
    }

    @Override
    public String toLlvmIr() {
        return super.getName() + " = call i32 @getint()";
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        used.add(getResult());
        return used;
    }
}
