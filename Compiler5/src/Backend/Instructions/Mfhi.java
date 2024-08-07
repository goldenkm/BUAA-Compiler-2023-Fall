package Backend.Instructions;

import Backend.MipsBuilder;

public class Mfhi implements MipsInstruction {
    private int hi;

    public Mfhi(int hi) {
        this.hi = hi;
    }

    @Override
    public String toMips() {
        return "mfhi " + MipsBuilder.getReg(hi);
    }
}
