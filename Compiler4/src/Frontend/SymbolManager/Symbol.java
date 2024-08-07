package Frontend.SymbolManager;

import Frontend.Parser.Parser;
import Frontend.SyntaxComponents.AllExp.Exp;
import Middle.IrComponents.Value;

import java.util.ArrayList;

public class Symbol {
    private static int SymbolCnt = 0;
    private final int id;
    private final int line;
    private final SymbolTable symbolTable;
    private final String token;
    private final SymbolType type;
    private final boolean isConst;
    private int value;          // 只有普通常量才有
    private int dim1 = 0;
    private int dim2 = 0;
    private final ArrayList<Integer> values1;     // 数组初值，只有一维数组有
    private final ArrayList<ArrayList<Integer>> values2;      // 数组初值，只有二维数组有
    private Value irValue;

    public Symbol(int line, int tableId, String token, int type, boolean isConst, int value, int dim1, int dim2) {
        this.id = SymbolCnt++;
        this.line = line;
        this.symbolTable = Parser.getSymbolTables().get(tableId);
        this.token = token;
        switch (type) {
            case 0 -> this.type = SymbolType.VAR;
            case 1 -> this.type = SymbolType.ARRAY1;
            case 2 -> this.type = SymbolType.ARRAY2;
            case 3 -> this.type = SymbolType.FUNC;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        this.isConst = isConst;
        this.value = value;
        this.values1 = new ArrayList<>();
        this.values2 = new ArrayList<>();
        this.dim1 = dim1;
        this.dim2 = dim2;
    }

    public Symbol(int line, int tableId, String token, int type, boolean isConst, int value,
                  ArrayList<Exp> values1, ArrayList<ArrayList<Exp>> values2) {
        this(line, tableId, token, type, isConst, value, values1.size(), values2.size());
        if (values1.size() > 0) {
            for (Exp exp : values1) {
                this.values1.add(Integer.parseInt(exp.getValue(symbolTable)));
            }
        } else {
            for (ArrayList<Exp> exps : values2) {
                ArrayList<Integer> tmp = new ArrayList<>();
                for (Exp exp : exps) {
                    tmp.add(Integer.parseInt(exp.getValue(symbolTable)));
                }
                this.values2.add(tmp);
            }
        }
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

    public void setValue(int value) {
        this.value = value;
    }

    /** 生成中间代码 */
    public String getValue() {
        return isConst ? String.valueOf(value) : token;
    }

    public ArrayList<Integer> getValues1() {
        return values1;
    }

    public ArrayList<ArrayList<Integer>> getValues2() {
        return values2;
    }

    public String getValueByIndex(int index) {
        return String.valueOf(values1.get(index));
    }

    public String getValueByIndex(int index1, int index2) {
        return String.valueOf(values2.get(index1).get(index2));
    }

    public void setIrValue(Value irValue) {
        this.irValue = irValue;
    }

    public int getId() {
        return id;
    }

    public Value getIrValue() {
        return this.irValue;
    }

    public String getIrName() {
        return this.irValue.getName();
    }

    public int getIrType() {
        return this.irValue.getVarType();
    }

    public int getDim1() {
        return dim1;
    }

    public int getDim2() {
        return dim2;
    }
}
