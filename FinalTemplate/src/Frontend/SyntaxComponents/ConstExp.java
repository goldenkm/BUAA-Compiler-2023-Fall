package Frontend.SyntaxComponents;

import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.AllExp.AddExp;
import Frontend.SyntaxComponents.AllExp.Exp;

public class ConstExp extends Exp {
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        super(addExp);
    }

}
