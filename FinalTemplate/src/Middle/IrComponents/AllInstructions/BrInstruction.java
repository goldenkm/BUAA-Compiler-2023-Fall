package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class BrInstruction extends Instruction {
    private Value cond = null;
    private String trueLabel = null;
    private String falseLabel = null;
    private String dest = null;

    public BrInstruction(InstructionType type, Value cond,
                         String trueLabel, String falseLabel) {
        super(type, new Value());
        this.cond = cond;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
    }

    public BrInstruction(InstructionType type, String dest) {
        super(type, new Value());
        this.dest = dest;
    }

    public void setFalseLabel(String falseLabel) {
        this.falseLabel = falseLabel;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getDest() {
        return dest;
    }

    public Value getCond() {
        return cond;
    }

    public String getTrueLabel() {
        return trueLabel;
    }

    public String getFalseLabel() {
        return falseLabel;
    }

    @Override
    public String toLlvmIr() {
        if (cond != null) {
            return "br i1 " + cond.getName() + ", label %" + trueLabel + ", label %" + falseLabel;
        } else {
            return "br label %" + dest;
        }
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        ArrayList<Value> used = new ArrayList<>();
        if (cond != null) {
            used.add(cond);
        }
        return used;
    }
}
