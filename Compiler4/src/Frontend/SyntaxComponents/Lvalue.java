package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllExp.Exp;

public class Lvalue {
    private String identifier;
    private int type;       // 0, 1, 2, -1: error
    private Exp exp1;
    private Exp exp2;
    private boolean hasExp1;
    private boolean hasExp2;

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
        hasExp1 = true;
    }

    public void setExp2(Exp exp2) {
        this.exp2 = exp2;
        hasExp2 = true;
    }

    public Exp getExp1() {
        return exp1;
    }

    public Exp getExp2() {
        return exp2;
    }

    public boolean hasExp1() {
        return hasExp1;
    }

    public boolean hasExp2() {
        return hasExp2;
    }
}
