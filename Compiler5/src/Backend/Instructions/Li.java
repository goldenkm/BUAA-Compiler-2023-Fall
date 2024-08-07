package Backend.Instructions;

import Backend.MipsBuilder;

public class Li implements MipsInstruction {
    private int regID;
    private int immediate;

    public Li() {
    }

    public Li(int regID) {
        this.regID = regID;
    }

    public Li(int regID, int immediate) {
        this.regID = regID;
        this.immediate = immediate;
    }

    @Override
    public String toMips() {
        return "li " + MipsBuilder.getReg(regID) + ", " + immediate;
    }
}
