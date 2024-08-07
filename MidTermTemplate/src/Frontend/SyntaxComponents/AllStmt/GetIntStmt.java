package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.Lvalue;

public class GetIntStmt implements Stmt {
    private Lvalue lvalue;

    public GetIntStmt(Lvalue lvalue) {
        this.lvalue = lvalue;
    }
}
