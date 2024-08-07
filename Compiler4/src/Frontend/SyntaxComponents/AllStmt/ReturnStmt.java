package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.AllExp.Exp;

public class ReturnStmt implements Stmt {
    private Exp exp;
    private boolean hasRetVal;

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setRetVal(boolean hasRetVal) {
        this.hasRetVal = hasRetVal;
    }

    public boolean hasRetVal() {
        return hasRetVal;
    }

    public Exp getExp() {
        return exp;
    }
}
