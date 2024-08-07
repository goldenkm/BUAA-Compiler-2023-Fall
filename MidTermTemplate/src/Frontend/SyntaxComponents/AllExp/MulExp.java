package Frontend.SyntaxComponents.AllExp;

import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.AllExp.UnaryExp;

public class MulExp extends Exp {
    public MulExp() {
        super();
        super.stdOpList.add("*");
        super.stdOpList.add("/");
        super.stdOpList.add("%");
    }

    public void addBase(Exp exp) {
        if (exp instanceof UnaryExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of MulExp!");
        }
    }
}
