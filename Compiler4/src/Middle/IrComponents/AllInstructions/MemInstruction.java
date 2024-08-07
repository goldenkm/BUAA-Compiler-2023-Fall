package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

/**
 * 访存语句
 */
public class MemInstruction extends Instruction {
    private final Value pointer;

    /**
     * LOAD & STORE
     */
    public MemInstruction(InstructionType type, Value result, Value pointer) {
        super(type, result);
        this.pointer = pointer;
    }

    public Value getPointer() {
        return pointer;
    }

    @Override
    public String toLlvmIr() {
        String type = pointer.typeToLlvm();
        String retType = type.substring(0, type.length() - 1);
        switch (super.getType()) {
            case STORE -> {
                return "store " + retType + " " + super.getName() + ", " + type + " " + pointer.getName();
            }
            case LOAD -> {
                return super.getName() + " = load " + retType + ", " + type + " " + pointer.getName();
            }
            default -> {
                return "Error in MemInstruction!";
            }
        }
    }

    @Override
    public String getName() {       // store语句的name应该是pointer
        if (super.getType() == InstructionType.STORE) {
            return pointer.getName();
        } else {
            return super.getName();
        }
    }

    @Override
    public int getVarType() {
        if (super.getType() == InstructionType.STORE) {
            return pointer.getVarType();
        } else {
            return super.getResult().getVarType();
        }
    }

    @Override
    public String typeToLlvm() {
        if (super.getType() == InstructionType.STORE) {
            return pointer.typeToLlvm();
        } else {
            return super.getResult().typeToLlvm();
        }
    }

    @Override
    public int getCol() {
        return super.getResult().getCol();
    }
}
