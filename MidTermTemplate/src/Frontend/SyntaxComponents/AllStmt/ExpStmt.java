package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.AllExp.Exp;

public class ExpStmt implements Stmt {
    private Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }
}
