package Backend.Instructions;

import Backend.MipsBuilder;

public class Bne implements MipsInstruction {
    private final int reg1;
    private final int reg2;
    private final String label;

    public Bne(int reg1, int reg2, String label) {
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.label = label;
    }

    @Override
    public String toMips() {
        return "bne " + MipsBuilder.getReg(reg1) + ", " + MipsBuilder.getReg(reg2) + ", " + label;
    }
}
