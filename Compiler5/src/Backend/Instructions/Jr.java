package Backend.Instructions;

import Backend.MipsBuilder;

public class Jr implements MipsInstruction {
    private final int reg;

    public Jr(int reg) {
        this.reg = reg;
    }

    @Override
    public String toMips() {
        return "jr " + MipsBuilder.getReg(reg);
    }
}
