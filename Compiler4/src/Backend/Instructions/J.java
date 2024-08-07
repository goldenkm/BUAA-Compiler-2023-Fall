package Backend.Instructions;

public class J implements MipsInstruction {
    private String label;

    public J(String label) {
        this.label = label;
    }

    @Override
    public String toMips() {
        return "j " + label;
    }
}
