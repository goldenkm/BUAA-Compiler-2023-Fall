package Frontend.SyntaxComponents.AllExp;

public class LAndExp extends ExpTemplate {
    public LAndExp() {
        super();
        super.stdOpList.add("&&");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof EqExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of LAndExp!");
        }
    }
}
