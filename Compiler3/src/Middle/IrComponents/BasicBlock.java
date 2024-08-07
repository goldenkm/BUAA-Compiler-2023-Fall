package Middle.IrComponents;

import Middle.IrComponents.AllInstructions.Instruction;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private String label;
    private ArrayList<Instruction> instructions;

    public BasicBlock(String label) {
        super();
        this.label = label;
        this.instructions = new ArrayList<>();
    }

    public void addInstruction(Value instruction) {
        if (instruction instanceof Instruction) {
            this.instructions.add((Instruction) instruction);
        } else {
            System.out.println("Error in BasicBlock!");
        }
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public Instruction getLastInstruction() {
        return instructions.get(instructions.size() - 1);
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
