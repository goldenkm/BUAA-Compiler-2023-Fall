package Backend.Instructions;

import Backend.MipsBuilder;

public class Div implements MipsInstruction {
    private final int op1;
    private final int op2;

    public Div(int op1, int op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toMips() {
        // 如果是divu，则5，6都是50分；如果是div，则5是0分，6是满分
        return "div " + MipsBuilder.getReg(op1) + ", " + MipsBuilder.getReg(op2);
    }
}
