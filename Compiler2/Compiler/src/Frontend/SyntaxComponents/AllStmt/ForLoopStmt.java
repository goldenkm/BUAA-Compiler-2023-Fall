package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Cond;

public class ForLoopStmt implements Stmt {
    private ForStmt forStmt1;
    private Cond cond;
    private ForStmt forStmt2;
    private Stmt stmt;

    public void setForStmt1(ForStmt forStmt1) {
        this.forStmt1 = forStmt1;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public void setForStmt2(ForStmt forStmt2) {
        this.forStmt2 = forStmt2;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }
}
