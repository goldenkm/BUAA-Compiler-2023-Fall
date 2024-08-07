package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Exp;
import Frontend.SyntaxComponents.Stmt;

public class ExpStmt implements Stmt {
    private Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }
}
