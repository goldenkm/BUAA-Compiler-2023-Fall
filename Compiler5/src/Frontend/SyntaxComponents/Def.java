package Frontend.SyntaxComponents;

import Frontend.SyntaxComponents.AllInitialVal.InitialVal;

public class Def {
    private int type;       // 0: 普通变量，1：一维数组，2：二维数组
    private boolean isConst;
    private String identifier;
    private ConstExp constExp1;
    private ConstExp constExp2;
    private InitialVal rvalue;
    private boolean hasRvalue;

    public Def(String identifier, boolean isConst) {
        this.identifier = identifier;
        this.isConst = isConst;
        this.hasRvalue = false;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setConstExp1(ConstExp constExp1) {
        this.constExp1 = constExp1;
    }

    public void setConstExp2(ConstExp constExp2) {
        this.constExp2 = constExp2;
    }

    public void setRvalue(InitialVal rvalue) {
        this.rvalue = rvalue;
        this.hasRvalue = true;
    }

    public boolean isConst() {
        return isConst;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean hasInitialValue() {
        return this.hasRvalue;
    }

    public InitialVal getRvalue() {
        return rvalue;
    }

    public int getType() {
        return type;
    }

    public ConstExp getConstExp1() {
        return constExp1;
    }

    public ConstExp getConstExp2() {
        return constExp2;
    }
}
