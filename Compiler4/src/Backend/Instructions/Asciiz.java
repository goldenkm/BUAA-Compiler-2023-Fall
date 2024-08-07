package Backend.Instructions;

public class Asciiz implements MipsInstruction {
    private final String label;
    private final String content;

    public Asciiz(String label, String content) {
        this.label = label;
        this.content = content;
    }

    @Override
    public String toMips() {
        return label + ": .asciiz \"" + content + "\"";
    }
}
