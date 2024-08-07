package Frontend.SyntaxComponents.AllExp;

import Frontend.SyntaxComponents.Lvalue;

public class PrimaryExp implements UnaryBase {
    private int type;            // 0.(exp); 1.lvalue; 2.number
    private Exp exp;
    private Lvalue lvalue;
    private String number;

    public PrimaryExp(int type, Exp exp) {
        this.type = type;
        this.exp = exp;
    }

    public PrimaryExp(int type, Lvalue lvalue) {
        this.type = type;
        this.lvalue = lvalue;
    }

    public PrimaryExp(int type, String number) {
        this.type = type;
        this.number = number;
    }
}