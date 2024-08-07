package Backend.Instructions;

import Backend.MipsBuilder;

public class La implements MipsInstruction {
    private final int regID;
    private final String strLabel;

    public La(int regID, String strLabel) {
        this.regID = regID;
        this.strLabel = strLabel;
    }

    @Override
    public String toMips() {
        return "la " + MipsBuilder.getReg(regID) + ", " + strLabel;
    }
}
