package Backend.Instructions;

import Backend.MipsBuilder;

public class Mul implements MipsInstruction {
    private final int target;
    private final int op1;
    private final int op2;

    public Mul(int target, int op1, int op2) {
        this.target = target;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toMips() {
        return "mul " + MipsBuilder.getReg(target) + ", "
                + MipsBuilder.getReg(op1) + ", " + MipsBuilder.getReg(op2);
    }
}
