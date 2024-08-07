package Backend.Instructions;

import Backend.MipsBuilder;

public class Subu implements MipsInstruction {
    private final int target;
    private final int op1;
    private final int op2;

    public Subu(int target, int op1, int op2) {
        this.target = target;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toMips() {
        return "subu " + MipsBuilder.getReg(target) + ", "
                + MipsBuilder.getReg(op1) + ", " + MipsBuilder.getReg(op2);
    }
}
