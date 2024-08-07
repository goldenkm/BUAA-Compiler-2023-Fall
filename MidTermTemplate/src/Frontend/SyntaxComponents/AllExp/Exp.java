package Frontend.SyntaxComponents.AllExp;

import java.util.ArrayList;

public class Exp {
    private ArrayList<String> opList;
    private ArrayList<Exp> bases;
    protected ArrayList<String> stdOpList;

    public Exp() {
        this.opList = new ArrayList<>();
        this.bases = new ArrayList<>();
        this.stdOpList = new ArrayList<>();
    }

    public void addOp(String op) {
        if (!stdOpList.contains(op)) {
            // TODO: error;
            System.out.println("ERROR!");
        }
        this.opList.add(op);
    }

    public void addBase(Exp base) {
        this.bases.add(base);
    }

}
