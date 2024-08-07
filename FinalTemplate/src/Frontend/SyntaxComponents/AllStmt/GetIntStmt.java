package Frontend.SyntaxComponents.AllStmt;
import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.AllInitialVal.InitialVal;
import Frontend.SyntaxComponents.Lvalue;

import java.util.ArrayList;


public class GetIntStmt implements Stmt, InitialVal {
    private final Lvalue lvalue;

    public GetIntStmt(Lvalue lvalue) {
        this.lvalue = lvalue;
    }

    public Lvalue getLvalue() {
        return lvalue;
    }

    @Override
    public Exp getExp() {
        return null;
    }

    @Override
    public ArrayList<Exp> getExps() {
        return null;
    }

    @Override
    public ArrayList<ArrayList<Exp>> getExpArrays() {
        return null;
    }
}
