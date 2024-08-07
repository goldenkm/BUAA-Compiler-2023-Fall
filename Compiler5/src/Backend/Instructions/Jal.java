package Backend.Instructions;

public class Jal implements MipsInstruction {
    private final String label;

    public Jal(String label) {
        this.label = label;
    }

    @Override
    public String toMips() {
        return "jal " + label;
    }
}
