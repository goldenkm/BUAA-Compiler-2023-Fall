package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.Lvalue;

public class ForStmt implements Stmt {
    private Lvalue lvalue;
    private Exp exp;

    public ForStmt() {
    }

    public ForStmt(Lvalue lvalue, Exp exp) {
        this.lvalue = lvalue;
        this.exp = exp;
    }

    public void setLvalue(Lvalue lvalue) {
        this.lvalue = lvalue;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }
}
