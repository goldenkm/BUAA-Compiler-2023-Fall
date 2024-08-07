package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllExp.Exp;

public class Lvalue {
    private String identifier;
    private int type;       // 0, 1, 2, -1: error
    private Exp exp1;
    private Exp exp2;

    public Lvalue(String identifier) {
        this.identifier = identifier;
    }

    public Lvalue(String identifier, int type) {
        this.identifier = identifier;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setExp1(Exp exp1) {
        this.exp1 = exp1;
    }

    public void setExp2(Exp exp2) {
        this.exp2 = exp2;
    }
}
