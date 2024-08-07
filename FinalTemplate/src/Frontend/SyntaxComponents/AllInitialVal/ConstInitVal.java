package Frontend.SyntaxComponents.AllInitialVal;

import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.ConstExp;

import java.util.ArrayList;

public class ConstInitVal implements InitialVal {
    private ConstExp exp;

    private ArrayList<Exp> exps;
    private ArrayList<ArrayList<Exp>> expArrays;

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

    public void addExpArray(ArrayList<Exp> expArray) {
        this.expArrays.add(expArray);
    }

    @Override
    public ConstExp getExp() {
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
