package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolTable;

public class UnaryExp extends ExpTemplate implements UnaryBase {
    private String op = "";
    private UnaryBase unaryBase;
    private boolean errorFlag = false;

    public UnaryExp() {
    }

    public UnaryExp(boolean errorFlag) {
        this.errorFlag = errorFlag;
    }

    public void setUnaryBase(UnaryBase unaryBase) {
        this.unaryBase = unaryBase;
    }

    public UnaryBase getUnaryBase() {
        return unaryBase;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public int getType() {
        if (errorFlag) {
            return -1;
        }
        if (unaryBase instanceof PrimaryExp) {
            if (((PrimaryExp) unaryBase).getType() == 0) {
                return ((PrimaryExp) unaryBase).getExp().getType();
            } else if (((PrimaryExp) unaryBase).getType() == 1) {
                return ((PrimaryExp) unaryBase).getLvalue().getType();
            } else {
                return 0;
            }
        } else if (unaryBase instanceof UnaryExp) {
            return ((UnaryExp) unaryBase).getType();
        } else {
            if (unaryBase instanceof FuncExp) {
                return ((FuncExp) unaryBase).isVoid() ? 3 : 0;
            }
            return -1;
        }
    }

    @Override
    public String getValue(SymbolTable symbolTable) {
        if (unaryBase instanceof PrimaryExp) {
            return ((PrimaryExp) unaryBase).getValue(symbolTable);
        } else if (unaryBase instanceof UnaryExp) {
            if (op.equals("-")) {
                String value = ((UnaryExp) unaryBase).getValue(symbolTable);
                if (value.contains("-")) {
                    return value.replace("-", "");
                } else {
                    return "-" + value;
                }
            }
            return ((UnaryExp) unaryBase).getValue(symbolTable);
        } else if (unaryBase instanceof FuncExp) {
            if (((FuncExp) unaryBase).isVoid()) {
                return "call void " + ((FuncExp) unaryBase).getFuncName();
            } else {
                return "call i32 " + ((FuncExp) unaryBase).getFuncName();
            }
        } else {
            return "Error in UnaryExp";
        }
    }

    public Exp getDim1() {
        if (unaryBase instanceof PrimaryExp) {
            return ((PrimaryExp) unaryBase).getDim1();
        } else {
            throw new RuntimeException("非一维数组求了数组维数");
        }
    }

    public Exp getDim2() {
        if (unaryBase instanceof PrimaryExp) {
            return ((PrimaryExp) unaryBase).getDim2();
        } else {
            throw new RuntimeException("非一维数组求了数组维数");
        }
    }

    public String getOp() {
        return op;
    }

    public boolean isExp() {
        if (unaryBase instanceof PrimaryExp) {
            if (((PrimaryExp) unaryBase).getType() == 0) {
                return true;
            }
        }
        return false;
    }
}
