package Frontend.SyntaxComponents;

import java.util.ArrayList;

public class Lvalue {
    private String identifier;
    private int type;
    private Exp exp1;
    private Exp exp2;

    public Lvalue(String identifier) {
        this.identifier = identifier;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setExp1(Exp exp1) {
        this.exp1 = exp1;
    }

    public void setExp2(Exp exp2) {
        this.exp2 = exp2;
    }
}
