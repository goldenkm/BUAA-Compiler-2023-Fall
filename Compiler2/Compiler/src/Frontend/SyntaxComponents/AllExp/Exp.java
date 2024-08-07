package Frontend.SyntaxComponents.AllExp;

import Frontend.SymbolManager.SymbolType;

import java.util.ArrayList;

public class Exp {
    private ArrayList<String> opList;
    private ArrayList<Exp> bases;
    protected ArrayList<String> stdOpList;

    public Exp() {
        this.opList = new ArrayList<>();
        this.bases = new ArrayList<>();
        this.stdOpList = new ArrayList<>();
    }

    public void addOp(String op) {
        if (!stdOpList.contains(op)) {
            // TODO: error;
            System.out.println("ERROR!");
        }
        this.opList.add(op);
    }

    public void addBase(Exp base) {
        this.bases.add(base);
    }

    public int getType() {
        if (bases.size() > 1) {
            for (Exp base : bases) {
                if (base.getType() != 0) {
                    return 3;
                }
            }
            return 0;
        }
        Exp base = bases.get(0);
        return base.getType();
    }

    public SymbolType getSymbolType() {
        SymbolType type = switch (getType()) {
            case 0 -> SymbolType.VAR;
            case 1 -> SymbolType.ARRAY1;
            case 2 -> SymbolType.ARRAY2;
            case 3 -> SymbolType.FUNC;
            default -> SymbolType.ERROR;
        };
        return type;
    }

}
