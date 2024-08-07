package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.AllExp.Exp;

public class PrintStmt implements Stmt {
    private String formatString;
    private Exp exp;

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }
}
