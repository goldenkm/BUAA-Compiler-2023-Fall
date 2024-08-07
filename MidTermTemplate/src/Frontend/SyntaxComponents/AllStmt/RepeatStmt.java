package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Cond;

public class RepeatStmt implements Stmt {
    private Stmt stmt;
    private Cond cond;

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }
}
