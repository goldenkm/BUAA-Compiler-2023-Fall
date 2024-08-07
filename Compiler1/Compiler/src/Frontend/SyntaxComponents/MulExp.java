package Frontend.SyntaxComponents;

import java.util.ArrayList;
import java.util.Arrays;

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
