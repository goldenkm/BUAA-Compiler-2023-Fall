package Backend.Instructions;

import Backend.MipsBuilder;

public class Mult implements MipsInstruction {
    private final int op1;
    private final int op2;

    public Mult(int op1, int op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toMips() {
        return "mult " + MipsBuilder.getReg(op1) + ", " + MipsBuilder.getReg(op2);
    }
}
