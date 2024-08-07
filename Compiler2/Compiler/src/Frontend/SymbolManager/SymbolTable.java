package Frontend.SymbolManager;
import Frontend.Parser.Error;
import Frontend.Parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private int id;
    private int fatherId;
    private HashMap<String, Symbol> symbols;

    public SymbolTable(int id, int fatherId) {
        this.id = id;
        this.fatherId = fatherId;
        this.symbols = new HashMap<>();
    }

    public void addSymbol(Symbol symbol, ArrayList<Error> errorOutput) {
        String token = symbol.getToken();
        if (symbols.containsKey(token)) {
            errorOutput.add(new Error(symbol.getLine(), 'b'));
            return;
        }
        this.symbols.put(token, symbol);
    }

    public boolean containsSymbol(String token) {
        return symbols.containsKey(token);
    }

    public Symbol getSymbol(String token) {
        return symbols.get(token);
    }

    public int getId() {
        return id;
    }

    public int getFatherId() {
        return fatherId;
    }

    public HashMap<String, Symbol> getSymbols() {
        return this.symbols;
    }

    public Symbol findSymbol(String token) {
        int currentTableId = this.id;
        while (currentTableId != -1) {
            SymbolTable symbolTable = Parser.getSymbolTables().get(currentTableId);
            if (symbolTable.containsSymbol(token)) {
                return symbolTable.getSymbol(token);
            }
            currentTableId = symbolTable.getFatherId();
        }
        return null;
    }
}
