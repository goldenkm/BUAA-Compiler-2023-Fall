package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

public class CmpInstruction extends Instruction {
    private CmpArgs cond;
    private String op1;
    private String op2;

    public CmpInstruction(InstructionType type, Value result, CmpArgs cond, String op1, String op2) {
        super(type, result);
        this.cond = cond;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toLlvmIr() {
        return super.getName() + " = icmp " + cond.toString().toLowerCase() + " i32 " + op1 + ", " + op2;
    }
}
