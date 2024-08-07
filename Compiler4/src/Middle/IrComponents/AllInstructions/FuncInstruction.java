package Middle.IrComponents.AllInstructions;

import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class FuncInstruction extends Instruction {
    private final boolean isVoid;
    private final String funcName;
    private final ArrayList<String> params = new ArrayList<>();
    private final ArrayList<String> paramType = new ArrayList<>();

    public FuncInstruction(InstructionType type, Value result, boolean isVoid, String funcName) {
        super(type, result);
        this.isVoid = isVoid;
        this.funcName = funcName;
    }

    public void addParam(String param, String type) {
        this.params.add(param);
        this.paramType.add(type);
    }

    @Override
    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder();
        if (isVoid) {
            sb.append("call void @");
        } else {
            sb.append(super.getName()).append(" = call i32 @");
        }
        sb.append(funcName).append("(");
        if (params.size() != paramType.size()) throw new AssertionError();
        for (int i = 0; i < params.size(); i++) {
            sb.append(paramType.get(i)).append(" ").append(params.get(i));
            if (i != params.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public ArrayList<String> getParamType() {
        return paramType;
    }

    public String getFuncName() {
        return funcName;
    }

    public boolean isVoid() {
        return isVoid;
    }
}
