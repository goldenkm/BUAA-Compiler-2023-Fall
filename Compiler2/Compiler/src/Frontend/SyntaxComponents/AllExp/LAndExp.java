package Frontend.SyntaxComponents.AllExp;

public class LAndExp extends Exp {
    public LAndExp() {
        super();
        super.stdOpList.add("&&");
    }

    public void addBase(Exp exp) {
        if (exp instanceof EqExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of LAndExp!");
        }
    }
}
