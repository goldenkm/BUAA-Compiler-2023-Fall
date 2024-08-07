package Backend.MipsSymbolManager;

import Frontend.Parser.Parser;
import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;

import java.util.HashMap;

public class MipsSymbolTable {
    private final MipsSymbolTable fatherTable;
    private final HashMap<String, MipsValue> mipsSymbols;

    public MipsSymbolTable(MipsSymbolTable fatherTable) {
        this.fatherTable = fatherTable;
        this.mipsSymbols = new HashMap<>();
    }

    public void addMipsSymbol(String irName, MipsValue symbol) {
        this.mipsSymbols.put(irName, symbol);
    }

    public MipsSymbolTable getFatherTable() {
        return fatherTable;
    }

    public MipsValue getSymbol(String irName) {
        return this.mipsSymbols.get(irName);
    }

    public boolean containsSymbol(String irName) {
        return this.mipsSymbols.containsKey(irName);
    }

    public MipsValue findSymbol(String irName) {
        MipsSymbolTable currentTable = this;
        while (currentTable != null) {
            if (currentTable.containsSymbol(irName)) {
                return currentTable.getSymbol(irName);
            }
            currentTable = currentTable.getFatherTable();
        }
        System.out.println("未找到MipsSymbol");
        return null;
    }
}
