package Frontend.SymbolManager;

import Frontend.Parser.Parser;

public class Symbol {
    private int line;
    private int tableId;
    private String token;
    private SymbolType type;
    private boolean isConst;

    public Symbol(int line, int tableId, String token, int type, boolean isConst) {
        this.line = line;
        this.tableId = tableId;
        this.token = token;
        switch (type) {
            case 0:
                this.type = SymbolType.VAR;
                break;
            case 1:
                this.type = SymbolType.ARRAY1;
                break;
            case 2:
                this.type = SymbolType.ARRAY2;
                break;
            case 3:
                this.type = SymbolType.FUNC;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        this.isConst = isConst;
    }

    public String getToken() {
        return token;
    }

    public int getLine() {
        return line;
    }

    public boolean isConst() {
        return isConst;
    }

    public SymbolType getType() {
        return this.type;
    }
}
