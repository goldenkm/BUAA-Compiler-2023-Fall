package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

public class CmpInstruction extends Instruction {
    private final CmpArgs cond;
    private final String op1;
    private final String op2;

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

    public CmpArgs getCond() {
        return cond;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }
}
