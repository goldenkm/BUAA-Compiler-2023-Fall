package Frontend.SymbolManager;
import Frontend.Parser.Error;
import Frontend.Parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final int id;
    private final SymbolTable fatherTable;
    private final HashMap<String, Symbol> symbols;

    public SymbolTable(int id, SymbolTable fatherTable) {
        this.id = id;
        this.fatherTable = fatherTable;
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

    public SymbolTable getFatherTable() {
        return this.fatherTable;
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
            currentTableId = symbolTable.getFatherTable().getId();
        }
        return null;
    }

    public Symbol findSymbolByOrder(String token, int targetId) {
        int currentTableId = this.id;
        while (currentTableId != -1) {
            SymbolTable symbolTable = Parser.getSymbolTables().get(currentTableId);
            if (symbolTable.containsSymbol(token)) {
                Symbol symbol = symbolTable.getSymbol(token);
                if (symbol.getId() <= targetId) {
                    return symbol;
                }
            }
            currentTableId = symbolTable.getFatherTable().getId();
        }
        return null;
    }
}
