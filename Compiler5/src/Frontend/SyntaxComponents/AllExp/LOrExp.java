package Frontend.SyntaxComponents.AllExp;

public class LOrExp extends ExpTemplate {
    public LOrExp() {
        super();
        super.stdOpList.add("||");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof LAndExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of LOrExp!");
        }
    }
}
