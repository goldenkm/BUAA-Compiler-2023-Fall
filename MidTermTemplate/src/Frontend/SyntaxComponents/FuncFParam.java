package Frontend.SyntaxComponents;

public class FuncFParam {
    private int type;           // 0、普通变量，1、一维数组，2、二维数组
    private String identifier;
    private ConstExp exp2D;     // 二维形参内部的表达式

    public FuncFParam(String identifier) {
        this.identifier = identifier;
    }

    public void setExp2D(ConstExp exp2D) {
        this.exp2D = exp2D;
    }

    public void setType(int type) {
        this.type = type;
    }
}
