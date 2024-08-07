package Frontend.SyntaxComponents.AllExp;

public class UnaryExp extends Exp implements UnaryBase {
    private String op;
    private UnaryBase unaryBase;

    public UnaryExp() {
    }

    public void setUnaryBase(UnaryBase unaryBase) {
        this.unaryBase = unaryBase;
    }
}
