package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.AllExp.Exp;

public class ReturnStmt implements Stmt {
    private Exp exp;

    public void setExp(Exp exp) {
        this.exp = exp;
    }
}
