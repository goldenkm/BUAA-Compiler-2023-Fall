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

    public int countOfFormatChar() {
        int count = 0;
        for (int i = 0; i < formatString.length(); i++) {
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                count++;
            }
        }
        return count;
    }
}
