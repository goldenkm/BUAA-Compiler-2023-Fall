package Backend.Instructions;

import Backend.MipsBuilder;

public class Lw implements MipsInstruction {
    private final int regId;
    private final int baseReg;
    private final int offset;

    public Lw(int regId, int baseReg, int offset) {
        this.regId = regId;
        this.baseReg = baseReg;
        this.offset = offset;
    }

    public String toMips() {
        return "lw " + MipsBuilder.getReg(regId) + ", " + offset + "(" + MipsBuilder.getReg(baseReg) + ")";
    }
}
