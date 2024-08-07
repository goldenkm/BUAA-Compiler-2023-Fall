package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.Lvalue;
import Frontend.SyntaxComponents.Stmt;

public class GetIntStmt implements Stmt {
    private Lvalue lvalue;

    public GetIntStmt(Lvalue lvalue) {
        this.lvalue = lvalue;
    }
}
