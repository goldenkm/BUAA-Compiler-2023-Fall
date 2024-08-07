package Frontend.SyntaxComponents.AllExp;

import Frontend.SyntaxComponents.AllExp.AddExp;
import Frontend.SyntaxComponents.AllExp.Exp;

public class RelExp extends ExpTemplate {
    public RelExp() {
        super();
        super.stdOpList.add(">");
        super.stdOpList.add("<");
        super.stdOpList.add(">=");
        super.stdOpList.add("<=");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof AddExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of RelExp!");
        }
    }
}
