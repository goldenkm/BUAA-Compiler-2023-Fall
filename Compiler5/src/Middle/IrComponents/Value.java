package Middle.IrComponents;

public class Value {
    private String name;
    /**
     * 1. i32
     * 2. i32*
     * 3. i32**             函数传参传过来的一维数组
     * 4. [n x i32]*
     * 5. [m x [n x i32]]*  正常声明的二维数组
     * 6. [n x i32]**       函数传参传过来二维数组
     * 7. i1
     * to be continued...
     */
    private int varType = 1;
    private int row = 0;
    private int col = 0;

    // 为了优化常数计算
    private boolean isConst = false;
    private int number = 0;

    public Value() {
    }

    public Value(String name) {
        this.name = name;
    }

    // 有些Value是常量
    public Value(int number) {
        this.isConst = true;
        this.number = number;
    }

    public Value(String name, int varType) {
        this.name = name;
        this.varType = varType;
    }

    public Value(String name, int varType, int col) {
        this.name = name;
        this.varType = varType;
        this.col = col;
    }

    public Value(String name, int varType, int row, int col) {
        this.name = name;
        this.varType = varType;
        this.row = row;
        this.col = col;
    }

    public void setVarType(int varType) {
        this.varType = varType;
    }

    public int getVarType() {
        return varType;
    }

    public String typeToLlvm() {
        switch (varType) {
            case 2 -> {
                return "i32*";
            }
            case 3 -> {
                return "i32**";
            }
            case 4 -> {
                return "[" + col + " x i32]*";
            }
            case 5 -> {
                return "[" + row + " x [" + col + " x i32]]*";
            }
            case 6 -> {
                return "[" + col + " x i32]**";
            }
            case 7 -> {
                return "i1";
            }
            default -> {
                return "i32";
            }
        }
    }

    public String getName() {
        if (isConst) {
            return String.valueOf(number);
        }
        return name;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isConst() {
        return this.isConst;
    }

    public int getNumber() {
        return number;
    }
}
