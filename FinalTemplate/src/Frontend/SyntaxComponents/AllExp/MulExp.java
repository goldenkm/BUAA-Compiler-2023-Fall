package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.AllExp.UnaryExp;

public class MulExp extends ExpTemplate {
    public MulExp() {
        super();
        super.stdOpList.add("*");
        super.stdOpList.add("/");
        super.stdOpList.add("%");
        super.stdOpList.add("bitand");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof UnaryExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of MulExp!");
        }
    }

    @Override
    public String getValue(SymbolTable symbolTable) {
        if (!isNumber(bases.get(0).getValue(symbolTable))) {
            return bases.get(0).getValue(symbolTable);
        }
        int ans = Integer.parseInt(bases.get(0).getValue(symbolTable));
        if (opList.size() > 0) {
            for (int i = 0; i < opList.size(); i++) {
                switch (opList.get(i)) {
                    case "*" -> ans *= Integer.parseInt(bases.get(i + 1).getValue(symbolTable));
                    case "/" -> ans /= Integer.parseInt(bases.get(i + 1).getValue(symbolTable));
                    case "%" -> ans %= Integer.parseInt(bases.get(i + 1).getValue(symbolTable));
                    default -> System.out.println("Error in getValue of MulExp!");
                }
            }
            return String.valueOf(ans);
        } else {        // 只有一个base
            return bases.get(0).getValue(symbolTable);
        }
    }

    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }
}
