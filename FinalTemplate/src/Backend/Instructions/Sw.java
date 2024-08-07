package Backend.Instructions;

import Backend.MipsBuilder;

public class Sw implements MipsInstruction {
    private final int regID;
    private final int baseReg;
    private final int offset;

    public Sw(int regID, int baseReg, int offset) {
        this.regID = regID;
        this.baseReg = baseReg;
        this.offset = offset;
    }

    @Override
    public String toMips() {
        return "sw " + MipsBuilder.getReg(regID) + ", " + offset + "(" + MipsBuilder.getReg(baseReg) + ")";
    }
}
