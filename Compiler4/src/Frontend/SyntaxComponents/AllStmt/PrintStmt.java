package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.AllExp.Exp;

import java.util.ArrayList;

public class PrintStmt implements Stmt {
    private String formatString;
    private ArrayList<Exp> exps = new ArrayList<>();

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public void addExp(Exp exp) {
        this.exps.add(exp);
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

    public String getFormatString() {
        return formatString;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
