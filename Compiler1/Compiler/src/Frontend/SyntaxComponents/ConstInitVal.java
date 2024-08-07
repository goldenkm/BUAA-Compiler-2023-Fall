package Frontend.SyntaxComponents;

import java.util.ArrayList;

public class ConstInitVal implements InitialVal {
    private ConstExp exp;

    private ArrayList<ConstExp> exps;
    private ArrayList<ArrayList<ConstExp>> expArrays;

    public ConstInitVal() {
        this.exps = new ArrayList<>();
        this.expArrays = new ArrayList<>();
    }

    public void setExp(ConstExp exp) {
        this.exp = exp;
    }

    public void addExp(ConstExp exps) {
        this.exps.add(exps);
    }

    public void addExpArray(ArrayList<ConstExp> expArray) {
        this.expArrays.add(expArray);
    }
}
