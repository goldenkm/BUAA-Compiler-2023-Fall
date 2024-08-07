package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolTable;
import Frontend.SymbolManager.SymbolType;

import java.util.ArrayList;

public class ExpTemplate {
    protected ArrayList<String> opList;           // 此表达式对象实际有的操作符
    protected ArrayList<ExpTemplate> bases;
    protected ArrayList<String> stdOpList;      // 该类表达式允许有的操作符

    public ExpTemplate() {
        this.opList = new ArrayList<>();
        this.bases = new ArrayList<>();
        this.stdOpList = new ArrayList<>();
    }

    public void addOp(String op) {
        if (!stdOpList.contains(op)) {
            //TODO: error;
            System.out.println("ERROR!");
        }
        this.opList.add(op);
    }

    public void addBase(ExpTemplate base) {
        this.bases.add(base);
    }

    public int getType() {
        if (bases.size() > 1) {
            for (ExpTemplate base : bases) {
                if (base.getType() != 0) {
                    return 3;
                }
            }
            return 0;
        }
        ExpTemplate base = bases.get(0);
        return base.getType();
    }

    public SymbolType getSymbolType() {
        SymbolType type = switch (getType()) {
            case 0 -> SymbolType.VAR;
            case 1 -> SymbolType.ARRAY1;
            case 2 -> SymbolType.ARRAY2;
            case 3 -> SymbolType.FUNC;
            default -> SymbolType.ERROR;
        };
        return type;
    }

    public String  getValue(SymbolTable symbolTable) {
        // 和yjk学的，一般情况下不会调用到这里，在调用函数时应运用多态/instance of
        System.out.println("getValue in ExpTemplate is called!");
        return "Error!";
    }

    public ArrayList<ExpTemplate> getBases() {
        return this.bases;
    }

    public ArrayList<String> getOpList() {
        return opList;
    }
}
