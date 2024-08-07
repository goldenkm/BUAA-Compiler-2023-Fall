package Middle.IrComponents;

import Middle.IrComponents.AllInstructions.BrInstruction;
import Middle.IrComponents.AllInstructions.Instruction;
import Middle.IrComponents.AllInstructions.RetInstruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final String label;
    private final ArrayList<Instruction> instructions;
    private boolean hasEnd;

    public BasicBlock(String label) {
        super();
        this.label = label;
        this.instructions = new ArrayList<>();
        this.hasEnd = false;
    }

    public void addInstruction(Value instruction) {
        if (instruction instanceof Instruction) {
            // 一个基本块的末尾一定是一条跳转语句或者return语句
            if (hasEnd) {
                return;
            }
            if (instruction instanceof BrInstruction || instruction instanceof RetInstruction) {
                hasEnd = true;
            }
            this.instructions.add((Instruction) instruction);
        } else {
            System.out.println("Error in BasicBlock!");
        }
    }

    public void addAllInstr(ArrayList<Instruction> instructions) {
        this.instructions.addAll(instructions);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public Instruction getLastInstruction() {
        return instructions.get(instructions.size() - 1);
    }

    public void removeLastInstruction() {
        instructions.remove(instructions.size() - 1);
    }

    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder();
        for (Instruction instruction : instructions) {
            sb.append("\t");
            sb.append(instruction.toLlvmIr());
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getLabel() {
        return label;
    }

    public boolean isEmpty() {
        return this.instructions.size() == 0;
    }

    @Override
    public String getName() {
        return label;
    }
}
