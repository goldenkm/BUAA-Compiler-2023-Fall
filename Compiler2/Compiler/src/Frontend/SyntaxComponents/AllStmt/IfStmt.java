package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Cond;

public class IfStmt implements Stmt {
    private Cond cond;
    private Stmt ifStmt;
    private Stmt elseStmt;

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public void setIfStmt(Stmt ifStmt) {
        this.ifStmt = ifStmt;
    }

    public void setElseStmt(Stmt elseStmt) {
        this.elseStmt = elseStmt;
    }
}
