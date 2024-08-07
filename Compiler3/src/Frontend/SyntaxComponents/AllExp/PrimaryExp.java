package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.Lvalue;

public class PrimaryExp implements UnaryBase {
    private final int type;            // 0.(exp); 1.lvalue; 2.number
    private Exp exp;
    private Lvalue lvalue;
    private int number;

    public PrimaryExp(int type, Exp exp) {
        this.type = type;
        this.exp = exp;
    }

    public PrimaryExp(int type, Lvalue lvalue) {
        this.type = type;
        this.lvalue = lvalue;
    }

    public PrimaryExp(int type, int number) {
        this.type = type;
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public Exp getExp() {
        return exp;
    }

    public Lvalue getLvalue() {
        return lvalue;
    }

    public String getValue(SymbolTable symbolTable) {
        switch (this.type) {
            case 0 -> {
                return exp.getValue(symbolTable);
            }
            case 1 -> {
                Symbol symbol = symbolTable.findSymbol(lvalue.getIdentifier());
                if (!symbol.isConst()) {            // 变量就直接返回标识符
                    return lvalue.getIdentifier();
                }
                if (!lvalue.hasExp1() && !lvalue.hasExp2()) {        // 普通变量
                    return symbol.getValue();
                } else if (!lvalue.hasExp2()) {     // 一维数组
                    String index = lvalue.getExp1().getValue(symbolTable);
                    // 需要校验数组下标是否是数字
                    if (!isNumber(index)) {
                        return lvalue.getIdentifier();
                    }
                    return getValueByIndex(Integer.parseInt(index), symbolTable);
                } else {                            // 二维数组
                    String index1 = lvalue.getExp1().getValue(symbolTable);
                    String index2 = lvalue.getExp2().getValue(symbolTable);
                    // 需要校验数组下标是否是数字
                    if (!isNumber(index1) || !isNumber(index2)) {
                        return lvalue.getIdentifier();
                    }
                    return getValueByIndex(
                            Integer.parseInt(index1), Integer.parseInt(index2), symbolTable);
                }
            }
            case 2 -> {
                return String.valueOf(number);
            }
            default -> {
                System.out.println("Error in getValue in PrimaryExp!");
                return "Error!";
            }
        }
    }

    public String getValueByIndex(int index, SymbolTable symbolTable) {
        return symbolTable.findSymbol(lvalue.getIdentifier()).getValueByIndex(index);
    }

    public String getValueByIndex(int index1, int index2, SymbolTable symbolTable) {
        return symbolTable.findSymbol(lvalue.getIdentifier()).getValueByIndex(index1, index2);
    }

    public Exp getDim1() {
        if (type != 1) {
            throw new RuntimeException("非数组求了数组维数");
        }
        return lvalue.getExp1();
    }

    public Exp getDim2() {
        if (type != 1) {
            throw new RuntimeException("非二维数组求了数组维数");
        }
        return lvalue.getExp2();
    }

    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
