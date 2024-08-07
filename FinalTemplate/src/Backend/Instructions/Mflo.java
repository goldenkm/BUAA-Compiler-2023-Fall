package Backend.Instructions;

import Backend.MipsBuilder;

public class Mflo implements MipsInstruction {
    private int reg;

    public Mflo(int reg) {
        this.reg = reg;
    }

    @Override
    public String toMips() {
        return "mflo " + MipsBuilder.getReg(reg);
    }
}
