package Frontend.SyntaxComponents;

import java.util.ArrayList;
import java.util.Arrays;

public class AddExp extends Exp {
    public AddExp() {
        super();
        super.stdOpList.add("+");
        super.stdOpList.add("-");
    }

    public void addBase(Exp exp) {
        if (exp instanceof MulExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of AddExp!");
        }
    }
}
