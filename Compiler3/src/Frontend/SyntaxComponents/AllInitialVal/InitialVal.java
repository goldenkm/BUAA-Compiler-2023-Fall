package Frontend.SyntaxComponents.AllInitialVal;

import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.AllExp.Exp;

import java.util.ArrayList;

public interface InitialVal {

    Exp getExp();
    ArrayList<Exp> getExps();
    ArrayList<ArrayList<Exp>> getExpArrays();
}
