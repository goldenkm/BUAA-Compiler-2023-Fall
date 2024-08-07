package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Exp;
import Frontend.SyntaxComponents.Lvalue;
import Frontend.SyntaxComponents.Stmt;

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
