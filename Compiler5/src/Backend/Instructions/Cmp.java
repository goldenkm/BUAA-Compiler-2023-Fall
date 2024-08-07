package Backend.Instructions;

import Backend.MipsBuilder;
import Middle.IrComponents.AllInstructions.CmpArgs;

public class Cmp implements MipsInstruction {
    private final String type;
    private final int result;
    private final int op1;
    private final int op2;

    public Cmp(CmpArgs arg,int result, int op1, int op2) {
        switch (arg) {
            case NE -> this.type = "sne";
            case EQ -> this.type = "seq";
            default -> this.type = arg.toString().toLowerCase();
        }
        this.result = result;
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toMips() {
        return type + " " + MipsBuilder.getReg(result) + ", "
                + MipsBuilder.getReg(op1) + ", " + MipsBuilder.getReg(op2);
    }
}
