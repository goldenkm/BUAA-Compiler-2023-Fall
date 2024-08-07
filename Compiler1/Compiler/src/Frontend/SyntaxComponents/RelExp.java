package Frontend.SyntaxComponents;

public class RelExp extends Exp {
    public RelExp() {
        super();
        super.stdOpList.add(">");
        super.stdOpList.add("<");
        super.stdOpList.add(">=");
        super.stdOpList.add("<=");
    }

    public void addBase(Exp exp) {
        if (exp instanceof AddExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of RelExp!");
        }
    }
}
