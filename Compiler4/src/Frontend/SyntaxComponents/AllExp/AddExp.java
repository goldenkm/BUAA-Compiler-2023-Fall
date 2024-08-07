package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolTable;

public class AddExp extends ExpTemplate {
    public AddExp() {
        super();
        super.stdOpList.add("+");
        super.stdOpList.add("-");
    }

    public void addBase(ExpTemplate exp) {
        if (exp instanceof MulExp) {
            super.addBase(exp);
        } else {
            // TODO: error;
            System.out.println("Error in addBase of AddExp!");
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
                String baseValue = bases.get(i + 1).getValue(symbolTable);
                if (opList.get(i).equals("+")) {
                    ans += Integer.parseInt(baseValue);
                } else {
                    ans -= Integer.parseInt(baseValue);
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
