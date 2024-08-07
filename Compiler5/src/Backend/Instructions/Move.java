package Backend.Instructions;

import Backend.MipsBuilder;

public class Move implements MipsInstruction {
    private int srcReg;
    private int dstReg;

    public Move(int srcReg, int dstReg) {
        this.srcReg = srcReg;
        this.dstReg = dstReg;
    }

    @Override
    public String toMips() {
        return "move " + MipsBuilder.getReg(dstReg) + ", " + MipsBuilder.getReg(srcReg);
    }
}
