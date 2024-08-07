package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolTable;
import Frontend.SymbolManager.SymbolType;

import java.util.ArrayList;

public class Exp {
    private final AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public String getValue(SymbolTable symbolTable) {
        return addExp.getValue(symbolTable);
    }

    public int getType() {
        return addExp.getType();
    }

    public SymbolType getSymbolType() {
        return addExp.getSymbolType();
    }

    public AddExp getAddExp() {
        return addExp;
    }
}
