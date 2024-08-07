package Frontend.SyntaxComponents.AllExp;

import java.util.ArrayList;

public class FuncExp implements UnaryBase {
    private boolean isVoid;
    private String funcName;
    private ArrayList<Exp> funcParams;

    public FuncExp(String funcName, boolean isVoid) {
        this.funcName = funcName;
        this.funcParams = new ArrayList<>();
        this.isVoid = isVoid;
    }

    public void addFuncParam(Exp funcParam) {
        this.funcParams.add(funcParam);
    }

    public boolean isVoid() {
        return isVoid;
    }

    public String getFuncName() {
        return funcName;
    }

    public ArrayList<Exp> getFuncParams() {
        return funcParams;
    }
}
