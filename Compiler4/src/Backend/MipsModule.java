package Backend;

import Backend.Instructions.MipsInstruction;

import java.util.ArrayList;

public class MipsModule {
    private final ArrayList<MipsInstruction> dataSection;
    private final ArrayList<MipsInstruction> globalVars;
    private final ArrayList<MipsInstruction> textSection;

    public MipsModule() {
        this.dataSection = new ArrayList<>();
        this.globalVars = new ArrayList<>();
        this.textSection = new ArrayList<>();
    }

    public ArrayList<String> getMipsOutput() {
        ArrayList<String> output = new ArrayList<>();
        output.add(".data");
        for (MipsInstruction instr : dataSection) {
            output.add(instr.toMips());
        }
        output.add(".text");
        for (MipsInstruction globalVar : globalVars) {
            output.add("\t" + globalVar.toMips());
        }
        for (MipsInstruction instr : textSection) {
            output.add("\t" + instr.toMips());
        }
        return output;
    }

    public void addGlobalVar(MipsInstruction mipsInstruction) {
        this.globalVars.add(mipsInstruction);
    }

    public void addText(MipsInstruction mipsInstruction) {
        this.textSection.add(mipsInstruction);
    }

    public void addData(MipsInstruction mipsInstruction) {
        this.dataSection.add(mipsInstruction);
    }
}
