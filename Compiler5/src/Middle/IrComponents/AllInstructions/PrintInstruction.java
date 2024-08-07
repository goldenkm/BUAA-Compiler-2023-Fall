package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;
import java.util.LinkedList;

public class PrintInstruction extends Instruction {
    private final String formatString;
    private final ArrayList<Value> params = new ArrayList<>();

    public PrintInstruction(InstructionType type, Value result, String formatString) {
        super(type, result);
        this.formatString = formatString;
    }

    public void addParam(Value param) {
        this.params.add(param);
    }

    @Override
    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder();
        LinkedList<Value> queue = new LinkedList<>(params);
        for (int i = 1; i < formatString.length() - 1; i++) {
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                if (queue.isEmpty()) {
                    continue;
                }
                sb.append("call void @putint(i32 ");
                sb.append(queue.poll().getName());
                i++;
            } else if (formatString.charAt(i) == '\\' && formatString.charAt(i + 1) == 'n') {
                sb.append("call void @putch(i32 10");
                i++;
            } else {
                sb.append("call void @putch(i32 ");
                sb.append((int) formatString.charAt(i));
            }
            sb.append(")\n\t");
        }
        return sb.toString();
    }

    public String getFormatString() {
        return formatString;
    }

    public ArrayList<Value> getParams() {
        return params;
    }

    @Override
    public ArrayList<Value> getUsedValues() {
        return new ArrayList<>(params);
    }
}
