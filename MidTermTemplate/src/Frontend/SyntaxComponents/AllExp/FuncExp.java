package Frontend.SyntaxComponents.AllExp;

import java.util.ArrayList;

public class FuncExp implements UnaryBase {
    private String funcName;
    private ArrayList<Exp> funcParams;

    public FuncExp(String funcName) {
        this.funcName = funcName;
        this.funcParams = new ArrayList<>();
    }

    public void addFuncParam(Exp funcParam) {
        this.funcParams.add(funcParam);
    }

}
