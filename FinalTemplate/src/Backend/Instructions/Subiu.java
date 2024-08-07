package Backend.Instructions;

import Backend.MipsBuilder;

public class Subiu implements MipsInstruction {
    private final int target;
    private final int regOp;
    private final int imm;

    public Subiu(int target, int regOp, int imm) {
        this.target = target;
        this.regOp = regOp;
        this.imm = imm;
    }

    @Override
    public String toMips() {
        return "subiu " + MipsBuilder.getReg(target) + ", " + MipsBuilder.getReg(regOp) + ", " + imm;
    }
}
