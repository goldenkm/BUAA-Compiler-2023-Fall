package Middle.IrComponents;

import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.Def;
import Frontend.SyntaxComponents.AllInitialVal.InitialVal;

import java.util.ArrayList;

public class IrGlobalVar {
    private final int type;       // 0: 普通变量，1：一维数组，2：二维数组
    private final String token;
    private boolean isConst;
    private int dim1;
    private int dim2;
    private boolean hasInitialValue = false;
    private InitialVal initialVal;

    private final SymbolTable symbolTable;

    // def ---> global variable
    public IrGlobalVar(Def def, SymbolTable symbolTable) {
        this.token = def.getIdentifier();
        this.isConst = def.isConst();
        if (def.hasInitialValue()) {
            this.hasInitialValue = true;
            this.initialVal = def.getRvalue();
        }
        Symbol symbol = symbolTable.findSymbol(token);
        Value value = new Value("@" + token);
        this.type = def.getType();
        switch (type) {
            case 0 -> value.setVarType(2);
            case 1 -> {
                this.dim1 = Integer.parseInt(def.getConstExp1().getValue(symbolTable));
                value.setVarType(4);
                value.setCol(dim1);
            }
            case 2 -> {
                this.dim1 = Integer.parseInt(def.getConstExp1().getValue(symbolTable));
                this.dim2 = Integer.parseInt(def.getConstExp2().getValue(symbolTable));
                value.setVarType(5);
                value.setRow(dim1);
                value.setCol(dim2);
            }
        }
        if (symbol.isConst()) {
            fillZero(symbol);
        }
        this.symbolTable = symbolTable;
        symbol.setIrValue(value);
    }

    public String toLlvmIr() {
        StringBuilder sb = new StringBuilder("@");
        sb.append(this.token).append(" = dso_local global ");
        switch (type) {
            case 0 -> {
                sb.append("i32 ");
                if (hasInitialValue) {
                    sb.append(initialVal.getExp().getValue(symbolTable));
                } else {            // 填补0
                    sb.append(0);
                }
            }
            case 1 -> {
                sb.append("[").append(dim1).append(" x i32]");
                if (hasInitialValue) {
                    sb.append(buildArray1(initialVal.getExps()));
                } else {
                    sb.append(" zeroinitializer");
                }
            }
            case 2 -> {
                sb.append("[").append(dim1).append(" x [").append(dim2).append(" x i32]]");
                if (hasInitialValue) {
                    sb.append(buildArray2());
                    sb.append("]");
                } else {
                    sb.append(" zeroinitializer");
                }
            }
        }
        return sb.toString();
    }

    private String buildArray1(ArrayList<Exp> initialValues) {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        for (int i = 0; i < initialValues.size(); i++) {
            sb.append("i32 ").append(initialValues.get(i).getValue(symbolTable));
            if (i != initialValues.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String buildArray2() {
        StringBuilder sb = new StringBuilder();
        sb.append(" [");
        for (int i = 0; i < dim1; i++) {
            sb.append("[").append(dim2).append(" x i32]");
            if (i < initialVal.getExpArrays().size()) {
                sb.append(buildArray1(initialVal.getExpArrays().get(i)));
            } else {
                sb.append(" zeroinitializer");
            }
            if (i != dim1 - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void fillZero(Symbol symbol) {
        if (type == 1) {
            ArrayList<Integer> zero = new ArrayList<>();
            for (int i = 0; i <= dim1 - symbol.getValues1().size(); i++) {
                zero.add(0);
            }
            symbol.getValues1().addAll(zero);
        } else {
            for (int i = 0; i <= dim1 - symbol.getValues2().size(); i++) {
                ArrayList<Integer> zero = new ArrayList<>();
                int realDim2 = symbol.getValues2().size();
                for (int j = 0; j <= dim2 - realDim2; j++) {
                    zero.add(0);
                }
                if (symbol.getValues2().size() <= i) {
                    symbol.getValues2().add(zero);
                } else {
                    symbol.getValues2().get(i).addAll(zero);
                }
            }
        }
    }

    public String getToken() {
        return token;
    }

    public int getType() {
        return type;
    }

    // 全局变量赋值只能是常数或者常量
    public int getInitVal0() {
        if (!hasInitialValue) {
            return 0;
        }
        return Integer.parseInt(initialVal.getExp().getValue(symbolTable));
    }

    public ArrayList<Integer> getInitVal1() {
        if (!hasInitialValue) {
            return new ArrayList<>();
        }
        ArrayList<Integer> array = new ArrayList<>();
        for (Exp exp : this.initialVal.getExps()) {
            int val = Integer.parseInt(exp.getValue(symbolTable));
            array.add(val);
        }
        return array;
    }

    public ArrayList<ArrayList<Integer>> getInitVal2() {
        if (!hasInitialValue) {
            return new ArrayList<>();
        }
        ArrayList<ArrayList<Integer>> arrays = new ArrayList<>();
        for (ArrayList<Exp> exps : this.initialVal.getExpArrays()) {
            ArrayList<Integer> array = new ArrayList<>();
            for (Exp exp : exps) {
                int val = Integer.parseInt(exp.getValue(symbolTable));
                array.add(val);
            }
            arrays.add(array);
        }
        return arrays;
    }

    public boolean hasInitVal() {
        return this.hasInitialValue;
    }

    public int getDim1() {
        return dim1;
    }

    public int getDim2() {
        return dim2;
    }
}
