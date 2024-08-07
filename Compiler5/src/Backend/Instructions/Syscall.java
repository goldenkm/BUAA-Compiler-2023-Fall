package Backend.Instructions;

public class Syscall implements MipsInstruction {

    @Override
    public String toMips() {
        return "syscall";
    }
}
