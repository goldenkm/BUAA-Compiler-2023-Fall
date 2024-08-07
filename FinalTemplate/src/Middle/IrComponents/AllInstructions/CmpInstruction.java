package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class CmpInstruction extends Instruction {
    private final CmpArgs cond;
    private final Value op1;
    private final Value op2;

    public CmpInstruction(InstructionType type, Value result, CmpArgs cond, Value op1, Value op2) {
        super(type, result);
        this.cond = cond;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toLlvmIr() {
        return super.getName() + " = icmp " + cond.toString().toLowerCase() + " i32 "
                + op1.getName() + ", " + op2.getName();
    }

    public CmpArgs getCond() {
        return cond;
    }

    public Value getOp1() {
        return op1;
    }

    public Value getOp2() {
        return op2;
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        used.add(op1);
        used.add(op2);
        return used;
    }
}
