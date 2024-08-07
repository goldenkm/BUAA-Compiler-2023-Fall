package Frontend.SyntaxComponents.AllInitialVal;

import Frontend.SyntaxComponents.AllExp.Exp;

import java.util.ArrayList;

public class VarInitVal implements InitialVal {
    private Exp exp;
    private ArrayList<Exp> exps;
    private ArrayList<ArrayList<Exp>> expArrays;

    public VarInitVal() {
        this.exps = new ArrayList<>();
        this.expArrays = new ArrayList<>();
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void addExp(Exp exp) {
        this.exps.add(exp);
    }

    public void addExpArray(ArrayList<Exp> array) {
        this.expArrays.add(array);
    }

    @Override
    public Exp getExp() {
        return exp;
    }

    @Override
    public ArrayList<Exp> getExps() {
        return exps;
    }

    @Override
    public ArrayList<ArrayList<Exp>> getExpArrays() {
        return expArrays;
    }
}
