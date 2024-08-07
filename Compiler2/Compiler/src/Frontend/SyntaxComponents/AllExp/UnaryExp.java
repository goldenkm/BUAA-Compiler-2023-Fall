package Frontend.SyntaxComponents.AllExp;

public class UnaryExp extends Exp implements UnaryBase {
    private String op;
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
        } else {
            if (unaryBase instanceof FuncExp) {
                return ((FuncExp) unaryBase).isVoid() ? 3 : 0;
            }
            return -1;
        }
    }
}
