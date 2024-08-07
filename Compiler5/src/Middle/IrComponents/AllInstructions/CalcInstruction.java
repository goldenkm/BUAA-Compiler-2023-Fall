package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

/**
 * 算术语句
 **/
public class CalcInstruction extends Instruction{
    private final Value op1;
    private final Value op2;

    public CalcInstruction(InstructionType type, Value result, Value op1, Value op2) {
        super(type, result);
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toLlvmIr() {
        switch (super.getType()) {
            case ADD -> {
                return super.getName() + " = add i32 " + op1.getName() + ", " + op2.getName();
            }
            case SUB -> {
                return super.getName() + " = sub i32 " + op1.getName() + ", " + op2.getName();
            }
            case MUL -> {
                return super.getName() + " = mul i32 " + op1.getName() + ", " + op2.getName();
            }
            case SDIV -> {
                return super.getName() + " = sdiv i32 " + op1.getName() + ", " + op2.getName();
            }
            case SREM -> {
                return super.getName() + " = srem i32 " + op1.getName() + ", " + op2.getName();
            }
            default -> {
                return "Error in CalInstruction!";
            }
        }
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
