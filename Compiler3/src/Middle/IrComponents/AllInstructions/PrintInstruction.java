package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class PrintInstruction extends Instruction {
    private String formatString;
    private ArrayList<String> params = new ArrayList<>();

    public PrintInstruction(InstructionType type, Value result, String formatString) {
        super(type, result);
        this.formatString = formatString;
    }

    public void addParam(String param) {
        this.params.add(param);
    }

    @Override
    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < formatString.length() - 1; i++) {
            sb.append("call void ");
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                sb.append("@putint(i32 ");
                sb.append(params.get(0));
                params.remove(0);
                i++;
            } else if (formatString.charAt(i) == '\\' && formatString.charAt(i + 1) == 'n') {
                sb.append("@putch(i32 10");
                i++;
            } else {
                sb.append("@putch(i32 ");
                sb.append((int) formatString.charAt(i));
            }
            sb.append(")\n\t");
        }
        return sb.toString();
    }
}
