package Backend.Instructions;

public class Label implements MipsInstruction {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    public String toMips() {
        return "\n" + label + ":";
    }
}
