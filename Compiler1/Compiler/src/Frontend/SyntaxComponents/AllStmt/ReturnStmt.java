package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.Exp;
import Frontend.SyntaxComponents.Stmt;

public class ReturnStmt implements Stmt {
    private Exp exp;

    public void setExp(Exp exp) {
        this.exp = exp;
    }
}
