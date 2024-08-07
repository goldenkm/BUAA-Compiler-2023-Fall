package Backend.Instructions;

import Backend.MipsBuilder;

public class Srl implements MipsInstruction {
    private final int targetReg;
    private final int baseReg;
    private final int imm;

    public Srl(int targetReg, int baseReg, int imm) {
        this.targetReg = targetReg;
        this.baseReg = baseReg;
        this.imm = imm;
    }

    @Override
    public String toMips() {
        return "srl " + MipsBuilder.getReg(targetReg) + ", " + MipsBuilder.getReg(baseReg) + ", " + imm;
    }
}
