package Frontend.SyntaxComponents.AllExp;

public class EqExp extends ExpTemplate {
    public EqExp() {
        super();
        super.stdOpList.add("!=");
        super.stdOpList.add("==");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof RelExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of EqExp!");
        }
    }
}
