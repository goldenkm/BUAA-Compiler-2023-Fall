package Frontend.SyntaxComponents;

public class Def {
    private int type;       // 0: 普通变量，1：一维数组，2：二维数组
    private boolean isConst;
    private String identifier;
    private ConstExp constExp1;
    private ConstExp constExp2;
    private InitialVal rvalue;

    public Def(String identifier, boolean isConst) {
        this.identifier = identifier;
        this.isConst = isConst;
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
    }
}
