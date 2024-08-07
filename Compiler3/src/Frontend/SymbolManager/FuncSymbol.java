package Frontend.SymbolManager;

import java.util.ArrayList;

public class FuncSymbol extends Symbol {
    private int funcType;       // 0: void, 1: int
    private boolean hasRParent;     // 有没有右括号
    private ArrayList<SymbolType> params;              // 只存储参数的类型

    public FuncSymbol(int line, int tableId, String token, int type, boolean isConst, int funcType) {
        super(line, tableId, token, type, isConst, 0, 0, 0);
        this.funcType = funcType;
        this.params = new ArrayList<>();
        this.hasRParent = true;
    }

    public boolean hasRParent() {
        return hasRParent;
    }

    public void setHasRParent(boolean hasRParent) {
        this.hasRParent = hasRParent;
    }

    public void addParam(int type) {
        switch (type) {
            case 0 -> params.add(SymbolType.VAR);
            case 1 -> params.add(SymbolType.ARRAY1);
            case 2 -> params.add(SymbolType.ARRAY2);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    public int getParamCount() {
        return params.size();
    }

    public ArrayList<SymbolType> getParams() {
        return params;
    }

    public boolean isVoid() {
        return funcType == 0;
    }
}
