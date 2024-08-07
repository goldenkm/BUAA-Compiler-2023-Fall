package Backend.Instructions;

import Backend.MipsBuilder;

public class Sll implements MipsInstruction {
    private final int targetReg;
    private final int baseReg;
    private final int imm;

    public Sll(int targetReg, int baseReg, int imm) {
        this.targetReg = targetReg;
        this.baseReg = baseReg;
        this.imm = imm;
    }

    @Override
    public String toMips() {
        return "sll " + MipsBuilder.getReg(targetReg) + ", " + MipsBuilder.getReg(baseReg) + ", " + imm;
    }
}
