package Frontend.Parser;

import Frontend.SymbolManager.FuncSymbol;
import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SymbolManager.SymbolType;
import Frontend.SyntaxComponents.AllExp.AddExp;
import Frontend.SyntaxComponents.AllInitialVal.InitialVal;
import Frontend.SyntaxComponents.AllStmt.BorCStmt;
import Frontend.SyntaxComponents.AllStmt.ExpStmt;
import Frontend.SyntaxComponents.AllStmt.ForLoopStmt;
import Frontend.SyntaxComponents.AllStmt.ForStmt;
import Frontend.SyntaxComponents.AllStmt.GetIntStmt;
import Frontend.SyntaxComponents.AllStmt.IfStmt;
import Frontend.SyntaxComponents.AllStmt.PrintStmt;
import Frontend.SyntaxComponents.AllStmt.ReturnStmt;
import Frontend.SyntaxComponents.AllStmt.SemicolonStmt;
import Frontend.SyntaxComponents.AllStmt.Block;
import Frontend.SyntaxComponents.AllStmt.BlockItem;
import Frontend.SyntaxComponents.CompUnit;
import Frontend.SyntaxComponents.Cond;
import Frontend.SyntaxComponents.AllInitialVal.ConstInitVal;
import Frontend.SyntaxComponents.Def;
import Frontend.SyntaxComponents.ConstExp;
import Frontend.SyntaxComponents.Decl;
import Frontend.SyntaxComponents.AllExp.EqExp;
import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.FuncDef;
import Frontend.SyntaxComponents.AllExp.FuncExp;
import Frontend.SyntaxComponents.FuncFParam;
import Frontend.SyntaxComponents.AllExp.LAndExp;
import Frontend.SyntaxComponents.AllExp.LOrExp;
import Frontend.SyntaxComponents.Lvalue;
import Frontend.SyntaxComponents.MainFuncDef;
import Frontend.SyntaxComponents.AllExp.MulExp;
import Frontend.SyntaxComponents.AllExp.PrimaryExp;
import Frontend.SyntaxComponents.AllExp.RelExp;
import Frontend.SyntaxComponents.AllStmt.Stmt;
import Frontend.SyntaxComponents.AllExp.UnaryExp;
import Frontend.SyntaxComponents.AllInitialVal.VarInitVal;
import Frontend.Lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private ArrayList<Token> tokens;
    private int pointer;
    private String curToken;
    private ArrayList<String> syntaxOutput = new ArrayList<>();
    private ArrayList<String> tmpOutput = new ArrayList<>();
    private ArrayList<Error> errorOutput = new ArrayList<>();
    private ArrayList<Error> tmpErrorOutput = new ArrayList<>();
    private static HashMap<Integer, SymbolTable> symbolTables = new HashMap<>();
    private int currentTableId;     // 当前表的id
    private int fatherTableId;      // 父级表的id
    private int countOfTable;       // 表的数量
    private int currentFuncType;    // 当前函数的类型，0：void，1：int
    private boolean hasReturnStmt;  // 是否有返回值
    private boolean canBorC;        // 可不可以break或continue
    private boolean calculating;   // 是否正在计算，针对函数调用是否要参与计算
    private boolean timeStop;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.pointer = 0;
        curToken = tokens.get(this.pointer).getToken();
        currentTableId = -1;
        fatherTableId = -1;
        // 创建一个编号为-1的表，但不装任何东西
        symbolTables.put(-1, new SymbolTable(-1, null));
        countOfTable = 0;
        currentFuncType = -1;
        hasReturnStmt = false;
        canBorC = false;
        calculating = false;
        timeStop = false;
    }

    private void next() {
        addSyntaxOutput(getCurTokenType() + " " + curToken);
        if (pointer == tokens.size() - 1) {
            return;
        }
        //System.out.println(curToken);
        curToken = tokens.get(++pointer).getToken();
    }

    private String getCurTokenType() {
        return tokens.get(pointer).getType();
    }

    private int getCurTokenLine() {
        return tokens.get(pointer).getLine();
    }

    private String getNextToken() {
        return tokens.get(pointer + 1).getToken();
    }

    private void addSyntaxOutput(String s) {
        if (!timeStop) {
            syntaxOutput.add(s);
        } else {
            tmpOutput.add(s);
        }
    }

    private void addErrorOutput(int line, char type) {
        if (!timeStop) {
            errorOutput.add(new Error(line, type));
        } else {
            tmpErrorOutput.add(new Error(line, type));
        }
    }

    private void tmpToOutput() {
        syntaxOutput.addAll(tmpOutput);
        tmpOutput.clear();
    }

    private void tmpToErrorOutput() {
        errorOutput.addAll(tmpErrorOutput);
        tmpErrorOutput.clear();
    }

    private void addSymbol(int line, String token, int type, boolean isConst, int value, int dim1, int dim2) {
        Symbol symbol = new Symbol(line, currentTableId, token, type, isConst, value, dim1, dim2);
        SymbolTable symbolTable = symbolTables.get(currentTableId);
        symbolTable.addSymbol(symbol, errorOutput);
    }

    private void addArraySymbol(int line, String token, int type, boolean isConst,
                                ArrayList<Exp> values1, ArrayList<ArrayList<Exp>> values2) {
        Symbol symbol = new Symbol(line, currentTableId, token, type, isConst, 0,
                values1, values2);
        SymbolTable symbolTable = symbolTables.get(currentTableId);
        symbolTable.addSymbol(symbol, errorOutput);
    }

    private void addFuncSymbol(FuncSymbol symbol) {     // funcSymbol要加到这一级符号表
        SymbolTable symbolTable = symbolTables.get(currentTableId);
        symbolTable.addSymbol(symbol, errorOutput);
    }

    private void createSymbolTable() {
        fatherTableId = currentTableId;
        currentTableId = countOfTable++;
        symbolTables.put(currentTableId, new SymbolTable(currentTableId,
                symbolTables.get(fatherTableId)));
    }

    private boolean isUndefinedSymbol(String token) {
        SymbolTable symbolTable = symbolTables.get(currentTableId);
        return symbolTable.findSymbol(token) == null;
    }

    private Symbol findSymbol(String name) {
        SymbolTable symbolTable = symbolTables.get(currentTableId);
        return symbolTable.findSymbol(name);
    }

    private FuncSymbol findFuncSymbol(String name) {
        Symbol symbol = findSymbol(name);
        if (symbol instanceof FuncSymbol) {
            return (FuncSymbol) symbol;
        }
        return null;
    }

    private boolean isUnaryOp(String token) {
        return token.equals("+") || token.equals("-") || token.equals("!");
    }

    private boolean hasParam() {
        return getCurTokenType().equals("IDENFR") || getCurTokenType().equals("INTCON")
                || isUnaryOp(curToken) || curToken.equals("(");
    }

    private void checkConstLvalue(Lvalue lvalue) {
        if (lvalue.getType() != -1) {
            if (symbolTables.get(currentTableId).findSymbol(lvalue.getIdentifier()).isConst()) {
                // h类错误，修改常量
                addErrorOutput(getCurTokenLine(), 'h');
            }
        }
    }

    /**
     * 进入一个作用域的流程：
     * 1、建表
     * 2、parse
     * 3、将currentTableId置为父id
     */
    public CompUnit parseCompUnit() {
        createSymbolTable();
        CompUnit compUnit = new CompUnit();
        while (true) {                                      // Decl
            if (curToken.equals("const")) {
                next();
                compUnit.addDecl(parseConstDecl());
            } else if (curToken.equals("int") && !getNextToken().equals("main")) {
                String next2Token = tokens.get(pointer + 2).getToken();
                if (next2Token.equals("(")) {  // 读到int func()就停止
                    break;
                } else {
                    next();
                    compUnit.addDecl(parseVarDecl());
                }
            } else {        // 读到void也停止
                break;
            }
        }
        while (true) {                                      // FuncDef
            currentFuncType = curToken.equals("int") ? 1 : 0;
            next();
            if (curToken.equals("main")) {
                break;
            }
            addSyntaxOutput("<FuncType>");
            compUnit.addFuncDefs(parseFuncDef());
        }
        compUnit.setMainFuncDef(parseMainFuncDef());
        addSyntaxOutput("<CompUnit>");
        currentTableId = 0;
        return compUnit;
    }

    private Decl parseConstDecl() {
        Decl constDecl = new Decl(true);
        while (true) {
            next();         // read 'int'
            constDecl.addDef(parseConstDef());
            if (curToken.equals(",")) {
                continue;
            }
            if (curToken.equals(";")) {
                next();
                break;
            }
            // i类错误，缺少分号
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
            break;
        }
        addSyntaxOutput("<ConstDecl>");
        return constDecl;
    }

    private Def parseConstDef() {
        String ident = curToken;
        int line = getCurTokenLine();
        Def constDef = new Def(ident, true);
        int type = 0;
        next();
        if (curToken.equals("[")) {             // 一维数组
            next();
            constDef.setConstExp1(parseConstExp());
            if (!curToken.equals("]")) {                // k类错误，缺少右中括号
                addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
            } else {
                next();
            }
            if (curToken.equals("[")) {         // 二维数组
                next();
                constDef.setConstExp2(parseConstExp());
                type = 2;
                if (!curToken.equals("]")) {            // k类错误，缺少右中括号
                    addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
                } else {
                    next();
                }
            } else {
                type = 1;
            }
        }
        constDef.setType(type);
        next(); // read '='
        ConstInitVal constInitVal = parseConstInitVal();
        constDef.setRvalue(constInitVal);
        if (type == 0) {
            int value = Integer.parseInt(constInitVal.getExp().getValue(symbolTables.get(currentTableId)));
            addSymbol(line, ident, type, true, value, 0, 0);
        } else if (type == 1) {
            addArraySymbol(line, ident, type, true, constInitVal.getExps(), new ArrayList<>());
        } else {
            addArraySymbol(line, ident, type, true, new ArrayList<>(), constInitVal.getExpArrays());
        }
        addSyntaxOutput("<ConstDef>");
        return constDef;
    }

    private ConstInitVal parseConstInitVal() {
        ConstInitVal constInitVal = new ConstInitVal();
        if (curToken.equals("{")) {
            next();
            while (true) {
                if (curToken.equals("{")) {
                    if (getNextToken().equals("}")) {           // 防止空花括号
                        next();
                        next();
                        addSyntaxOutput("<ConstInitVal>");
                        if (curToken.equals(",")) {
                            next();
                        }
                        continue;
                    }
                    constInitVal.addExpArray(parseConstArrayInitVal());
                    addSyntaxOutput("<ConstInitVal>");
                    if (curToken.equals(",")) {
                        next();
                    }
                } else if (!curToken.equals("}")) {
                    constInitVal.addExp(parseConstExp());
                    addSyntaxOutput("<ConstInitVal>");
                    if (curToken.equals(",")) {
                        next();
                    }
                } else {
                    next();
                    break;
                }
            }
        } else {
            constInitVal.setExp(parseConstExp());
        }
        addSyntaxOutput("<ConstInitVal>");
        return constInitVal;
    }

    private ArrayList<Exp> parseConstArrayInitVal() {
        ArrayList<Exp> exps = new ArrayList<>();
        next();
        while (true) {
            exps.add(parseConstExp());
            addSyntaxOutput("<ConstInitVal>");     // 改写左递归之后要变回去
            if (curToken.equals("}")) {
                break;
            }
            next();
        }
        next();
        return exps;
    }

    private Decl parseVarDecl() {
        Decl varDecl = new Decl(false);
        while (true) {
            varDecl.addDef(parseVarDef());
            if (curToken.equals(",")) {
                next();
            } else {
                break;
            }
        }
        if (curToken.equals(";")) {
            next();
        } else {
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
        }
        addSyntaxOutput("<VarDecl>");
        return varDecl;
    }

    private Def parseVarDef() {
        String ident = curToken;
        int line = getCurTokenLine();
        Def varDef = new Def(ident, false);
        int type;
        next();
        int dim1 = 0;
        int dim2 = 0;
        if (curToken.equals("[")) {             // 一维数组
            next();
            ConstExp constExp1 = parseConstExp();
            varDef.setConstExp1(constExp1);
            dim1 = Integer.parseInt(constExp1.getValue(symbolTables.get(currentTableId)));
            if (!curToken.equals("]")) {            // k类错误，缺少右中括号
                addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
            } else {
                next();
            }
            if (curToken.equals("[")) {         // 二维数组
                next();
                ConstExp constExp2 = parseConstExp();
                varDef.setConstExp2(constExp2);
                dim2 = Integer.parseInt(constExp2.getValue(symbolTables.get(currentTableId)));
                type = 2;
                if (!curToken.equals("]")) {            // k类错误，缺少右中括号
                    addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
                } else {
                    next();
                }
            } else {
                type = 1;
            }
        } else {
            type = 0;
        }
        varDef.setType(type);
        if (curToken.equals("=")) {
            next();
            InitialVal initialVal = parseVarInitVal();
            varDef.setRvalue(initialVal);
        }
        addSymbol(line, ident, type, false, 0, dim1, dim2);
        addSyntaxOutput("<VarDef>");
        return varDef;
    }

    private VarInitVal parseVarInitVal() {
        VarInitVal varInitVal = new VarInitVal();
        if (curToken.equals("{")) {
            next();
            while (true) {
                if (curToken.equals("{")) {
                    if (getNextToken().equals("}")) {           // 防止空花括号
                        next();
                        next();
                        addSyntaxOutput("<InitVal>");
                        if (curToken.equals(",")) {
                            next();
                        }
                        continue;
                    }
                    varInitVal.addExpArray(parseVarArrayInitVal());
                    addSyntaxOutput("<InitVal>");
                    if (curToken.equals(",")) {
                        next();
                    }
                } else if (!curToken.equals("}")) {
                    varInitVal.addExp(parseExp());
                    addSyntaxOutput("<InitVal>");
                    if (curToken.equals(",")) {
                        next();
                    }
                } else {
                    next();
                    break;
                }
            }
        } else {
            varInitVal.setExp(parseExp());
        }
        addSyntaxOutput("<InitVal>");
        return varInitVal;
    }

    private ArrayList<Exp> parseVarArrayInitVal() {
        ArrayList<Exp> exps = new ArrayList<>();
        next();                     // read '{'
        while (true) {
            exps.add(parseExp());
            addSyntaxOutput("<InitVal>");      // 改写左递归之后要变回去
            if (curToken.equals("}")) {
                break;
            }
            next();
        }
        next();
        return exps;
    }

    private FuncDef parseFuncDef() {
        FuncDef funcDef = new FuncDef(curToken, currentFuncType);
        int line = getCurTokenLine();
        String funcName = curToken;
        FuncSymbol funcSymbol = new FuncSymbol(line, currentTableId,
                funcName, 3, false, currentFuncType);
        next();         // read 'ident'
        next();         // read '('
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        if (!curToken.equals(")")) {            // 注意函数空参问题
            if (!curToken.equals("{")) {         // void f( {}，这种情况需要特判
                while (true) {      // 防止函数空参
                    FuncFParam funcFParam = parseFuncFParam(funcSymbol);
                    funcDef.addFuncFParam(funcFParam);
                    funcFParams.add(funcFParam);
                    if (!curToken.equals(",")) {
                        addSyntaxOutput("<FuncFParams>");
                        break;
                    }
                    next();
                }
            }
        }
        if (!curToken.equals(")")) {        // j类错误，没有右括号
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
            funcSymbol.setHasRParent(false);
        } else {            // read ')'
            next();
        }
        // 为函数设置子符号表id
        funcDef.setSonTableId(countOfTable);
        addFuncSymbol(funcSymbol);
        Block block = parseBlock(funcFParams);
        funcDef.setBlock(block);
        addSyntaxOutput("<FuncDef>");
        if (currentFuncType == 1 && !block.hasLastReturnStmt()) {    // g类错误，函数有返回值但没有return
            addErrorOutput(funcDef.getEndLine(), 'g');
        }
        currentTableId = 0;         // 把当前函数所在表的id置空
        fatherTableId = -1;
        hasReturnStmt = false;      // 把是否有返回值的标记置空
        return funcDef;
    }

    private MainFuncDef parseMainFuncDef() {
        next();     // read 'main'
        next();     // read '('
        if (curToken.equals(")")) {
            next();
        } else {                // j类
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
        }
        MainFuncDef mainFuncDef = new MainFuncDef();
        mainFuncDef.setSonTableId(countOfTable);
        Block block = parseBlock(new ArrayList<>());
        mainFuncDef.setBlock(block);
        addSyntaxOutput("<MainFuncDef>");
        if (!block.hasLastReturnStmt()) {       // g类错误，函数有返回值但没有return
            addErrorOutput(getCurTokenLine(), 'g');
        }
        currentFuncType = -1;       // 把当前函数的类型置空
        hasReturnStmt = false;      // 把是否有返回值的标记置空
        return mainFuncDef;
    }

    private FuncFParam parseFuncFParam(FuncSymbol funcSymbol) {
        next();     //read 'int'
        int line = getCurTokenLine();
        FuncFParam funcParam = new FuncFParam(curToken, line);
        next();
        int type = 0;
        if (curToken.equals("[")) {
            next();
            if (curToken.equals("]")) {
                next();
            } else {                                        // k类错误，缺少右中括号
                addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
            }
            if (curToken.equals("[")) {             // 二维形参
                next();
                type = 2;
                funcParam.setExp2D(parseConstExp());
                if (!curToken.equals("]")) {            // k类错误，缺少右中括号
                    addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
                } else {
                    next();
                }
            } else {                                // 一维形参
                type = 1;
            }
        }
        funcParam.setType(type);
        funcSymbol.addParam(type);
        addSyntaxOutput("<FuncFParam>");
        return funcParam;
    }

    private Block parseBlock(ArrayList<FuncFParam> funcFParams) {
        createSymbolTable();
        for (FuncFParam funcFParam : funcFParams) {     // 注意要记录二维数组的第二维
            int dim2;
            if (funcFParam.getType() == 2) {
                dim2 = Integer.parseInt(funcFParam.getExp2D().getValue(symbolTables.get(currentTableId)));
            } else {
                dim2 = 0;
            }
            addSymbol(funcFParam.getLine(), funcFParam.getIdentifier(),
                    funcFParam.getType(), false, 0, 0, dim2);
        }
        Block block = new Block(currentTableId);
        next();
        while (!curToken.equals("}")) {
            block.addBlockItem(parseBlockItem());
        }
        block.setEndLine(getCurTokenLine());
        next();                 // read '}'
        addSyntaxOutput("<Block>");
        currentTableId = fatherTableId;
        fatherTableId = symbolTables.get(currentTableId).getFatherTable().getId();
        return block;
    }

    private BlockItem parseBlockItem() {
        if (curToken.equals("const")) {             // 常量声明
            next();
            return parseConstDecl();
        } else if (curToken.equals("int")) {        // 变量声明
            next();
            return parseVarDecl();
        } else {                                    // 语句
            return parseStmt();
        }
    }

    private Stmt parseStmt() {
        switch (curToken) {
            case "if" -> {
                next();
                IfStmt ifStmt = parseIfStmt();
                addSyntaxOutput("<Stmt>");
                return ifStmt;
            }
            case "for" -> {
                next();
                ForLoopStmt forLoopStmt = parseForLoopStmt();
                addSyntaxOutput("<Stmt>");
                return forLoopStmt;
            }
            case "break" -> {
                return parseBorCStmt("break");
            }
            case "continue" -> {
                return parseBorCStmt("continue");
            }
            case "return" -> {
                ReturnStmt returnStmt = parseReturnStmt();
                addSyntaxOutput("<Stmt>");
                return returnStmt;
            }
            case "printf" -> {
                next();
                PrintStmt printStmt = parsePrintStmt();
                addSyntaxOutput("<Stmt>");
                return printStmt;
            }
            case "{" -> {
                Block block = parseBlock(new ArrayList<>());
                addSyntaxOutput("<Stmt>");
                return block;
            }
            case ";" -> {
                next();
                addSyntaxOutput("<Stmt>");
                return new SemicolonStmt();
            }
            default -> {
                if (getCurTokenType().equals("IDENFR")) {
                    if (isUndefinedSymbol(curToken)) {
                        addErrorOutput(getCurTokenLine(), 'c');
                        while (!curToken.equals(";")) {
                            next();
                        }
                        next();
                        return null;
                    }
                    int tmpPointer = pointer;
                    timeStop = true;
                    Exp exp = parseExp();
                    timeStop = false;
                    if (curToken.equals(";")) {             // 试探成功，返回exp
                        tmpToOutput();
                        tmpToErrorOutput();
                        next();             // read ';'
                        ExpStmt expStmt = new ExpStmt(exp);
                        addSyntaxOutput("<Stmt>");
                        return expStmt;
                    } else if (curToken.equals("=")) {                                // 试探失败
                        tmpOutput.clear();
                        tmpErrorOutput.clear();
                        curToken = tokens.get(tmpPointer).getToken();
                        pointer = tmpPointer;
                        Lvalue lvalue = parseLvalue(curToken);      // 确定是左值
                        checkConstLvalue(lvalue);
                        next();     // 读过'='
                        if (curToken.equals("getint")) {
                            GetIntStmt getIntStmt = new GetIntStmt(lvalue);
                            next();     // read 'getint'
                            next();     // read '('
                            if (!curToken.equals(")")) {            // j类错误，缺少右括号
                                addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
                            } else {
                                next();
                            }
                            if (!curToken.equals(";")) {              // i类错误，缺少分号
                                addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
                            } else {
                                next();         // read ';'
                            }
                            addSyntaxOutput("<Stmt>");
                            return getIntStmt;
                        } else {
                            calculating = true;
                            ForStmt forStmt = new ForStmt(lvalue, parseExp());
                            calculating = false;
                            if (!curToken.equals(";")) {              // i类错误，缺少分号
                                addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
                            } else {
                                next();                 // read ';'
                            }
                            addSyntaxOutput("<Stmt>");
                            return forStmt;
                        }
                    } else {            // i类错误，缺失分号
                        addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
                        ExpStmt expStmt = new ExpStmt(exp);
                        addSyntaxOutput("<Stmt>");
                        return expStmt;
                    }
                } else {        // 数字或者括号开头的表达式；
                    ExpStmt expStmt = new ExpStmt(parseExp());
                    if (!curToken.equals(";")) {            // i类错误，缺失分号
                        addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
                    } else {
                        next();         // read ';'
                    }
                    addSyntaxOutput("<Stmt>");
                    return expStmt;
                }
            }
        }
    }

    private IfStmt parseIfStmt() {
        IfStmt ifStmt = new IfStmt();
        if (!curToken.equals("(")) {
            // TODO: error;
            System.out.println("Error in parseIfStmt");
        }
        next();
        ifStmt.setCond(parseCond());
        if (!curToken.equals(")")) {
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
        } else {
            next();
        }
        ifStmt.setIfStmt(parseStmt());
        if (curToken.equals("else")) {
            next();
            ifStmt.setElseStmt(parseStmt());
        }
        return ifStmt;
    }

    private ForLoopStmt parseForLoopStmt() {
        ForLoopStmt forLoopStmt = new ForLoopStmt();
        canBorC = true;
        next();     // read '('
        if (!curToken.equals(";")) {
            forLoopStmt.setForStmt1(parseForStmt());
        }
        next();     // 此处应该读过的是';'
        if (!curToken.equals(";")) {
            forLoopStmt.setCond(parseCond());
        }
        next();     // 此处应该读过的是';'
        if (!curToken.equals(")")) {
            forLoopStmt.setForStmt2(parseForStmt());
        }
        next();     // 此处应该读过的是')'
        forLoopStmt.setStmt(parseStmt());
        canBorC = false;
        return forLoopStmt;
    }

    private ForStmt parseForStmt() {
        ForStmt forStmt = new ForStmt();
        Lvalue lvalue = parseLvalue(curToken);
        forStmt.setLvalue(lvalue);
        checkConstLvalue(lvalue);
        next();     // 此处应该读过的是'='
        calculating = true;
        forStmt.setExp(parseExp());
        calculating = false;
        addSyntaxOutput("<ForStmt>");
        return forStmt;
    }

    private BorCStmt parseBorCStmt(String token) {
        BorCStmt stmt = new BorCStmt(token);
        int line = getCurTokenLine();
        next();                     // read 'break'
        if (!canBorC) {                           // m类，不能break/continue
            addErrorOutput(line, 'm');
        }
        if (!curToken.equals(";")) {              // i类，缺少分号
            addErrorOutput(line, 'i');
        } else {
            next();                     // read ';'
        }
        addSyntaxOutput("<Stmt>");
        return stmt;
    }

    private ReturnStmt parseReturnStmt() {
        ReturnStmt returnStmt = new ReturnStmt();
        int line = getCurTokenLine();
        next();
        if (!curToken.equals(";")) {
            if (currentFuncType == 0) {         // f类错误，函数返回了不应该有的返回值
                returnStmt.setRetVal(false);
                if (hasParam()) {           // void函数，如果return了表达式
                    addErrorOutput(line, 'f');
                    calculating = true;
                    returnStmt.setExp(parseExp());
                    calculating = false;
                }
            } else {                        // int函数，return后肯定有表达式
                returnStmt.setRetVal(true);
                calculating = true;
                returnStmt.setExp(parseExp());
                calculating = false;
            }
        }
        if (curToken.equals(";")) {             // i类错误
            next();         // read ';'
        } else {
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'i');
        }
        hasReturnStmt = true;
        return returnStmt;
    }

    private PrintStmt parsePrintStmt() {
        int line = getCurTokenLine();               // 记录下printf所在的行数
        next();             // read '('
        PrintStmt printStmt = new PrintStmt();
        printStmt.setFormatString(curToken);
        if (!isValidFormatString(curToken)) {           // a类错误
            addErrorOutput(getCurTokenLine(), 'a');
        }
        next();
        int countOfExp = 0;
        if (curToken.equals(",")) {
            next();
            while (true) {
                printStmt.addExp(parseExp());
                countOfExp++;
                if (!curToken.equals(",")) {
                    break;
                }
                next();
            }
        }
        if (printStmt.countOfFormatChar() != countOfExp) {      // l类错误
            addErrorOutput(line, 'l');
        }
        if (!curToken.equals(")")) {        // j类错误，缺少右括号
            addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
        } else {
            next();             // read ')'
        }
        if (!curToken.equals(";")) {        // i类错误，缺少分号
            addErrorOutput(line, 'i');      // 假定printf只有一行，如果不是就可能有问题
        } else {
            next();             // read ';'
        }
        return printStmt;
    }

    private Exp parseExp() {
        Exp exp = new Exp(parseAddExp());
        addSyntaxOutput("<Exp>");
        return exp;
    }

    private Cond parseCond() {
        Cond cond = new Cond(parseLOrExp());
        addSyntaxOutput("<Cond>");
        return cond;
    }

    private Lvalue parseLvalue(String identifier) {
        boolean undefined = false;
        int type = 0;
        Lvalue lvalue = new Lvalue(identifier);
        if (isUndefinedSymbol(curToken)) {      // c类
            addErrorOutput(getCurTokenLine(), 'c');
            undefined = true;
        } else {
            SymbolTable currentSymbolTable = symbolTables.get(currentTableId);
            Symbol symbol = currentSymbolTable.findSymbol(curToken);
            if (symbol instanceof FuncSymbol) {    // c类，赋值给一个函数名
                addErrorOutput(getCurTokenLine(), 'c');
            }
            SymbolType originalType = symbol.getType();
            type = switch (originalType) {
                case VAR -> 0;
                case ARRAY1 -> 1;
                case ARRAY2 -> 2;
                case FUNC -> 3;
                default -> -1;
            };
        }
        next();
        if (curToken.equals("[")) {
            type--;
            next();
            lvalue.setExp1(parseExp());
            if (curToken.equals("]")) {
                next();             // read ']'
            } else {                                        // k类错误，缺少右中括号
                addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
            }
            if (curToken.equals("[")) {             // 二维形参
                type--;
                next();
                lvalue.setExp2(parseExp());
                if (!curToken.equals("]")) {            // k类错误，缺少右中括号
                    addErrorOutput(tokens.get(pointer - 1).getLine(), 'k');
                } else {
                    next();                 // read ']'
                }
            }
        }
        lvalue.setType(undefined ? -1 : type);
        addSyntaxOutput("<LVal>");
        return lvalue;
    }

    private UnaryExp parseUnaryExp() {
        UnaryExp unaryExp = new UnaryExp();
        if (getCurTokenType().equals("IDENFR")) {
            if (getNextToken().equals("(")) {                    // func()
                if (isUndefinedSymbol(curToken)) {
                    addErrorOutput(getCurTokenLine(), 'c');        // c类错误，名字未定义
                    while (!curToken.equals(")")) {
                        next();
                    }
                    next();
                    return new UnaryExp(true);
                }
                FuncSymbol funcSymbol = findFuncSymbol(curToken);
                if (funcSymbol == null) {       // c类错误，定义了但是不是函数类型
                    addErrorOutput(getCurTokenLine(), 'c');
                    while (!curToken.equals(")")) {     // 全部读完不知道可不可以
                        next();
                    }
                    next();
                    return new UnaryExp(true);
                }
                FuncExp funcExp = new FuncExp(funcSymbol.getToken(), funcSymbol.isVoid());
                int line = getCurTokenLine();
                next();         // read 'ident'
                next();         // read '('
                if (hasParam()) {
                    int paramCount = 0;
                    while (true) {
                        Exp param = parseExp();
                        if (param.getType() == -1 || param.getType() == 3) {        // 参数未定义或类型不匹配
                            if (param.getType() == 3) {     // e类，参数类型不匹配
                                addErrorOutput(line, 'e');
                            }
                            paramCount++;
                            if (curToken.equals(",")) {
                                next();
                                continue;
                            } else {
                                break;
                            }
                        }
                        if (paramCount < funcSymbol.getParamCount()) {
                            funcExp.addFuncParam(param);
                            if (funcSymbol.hasRParent() &&
                                    param.getSymbolType() != funcSymbol.getParams().get(paramCount)) {
                                // e类错误，函数参数类型不匹配
                                addErrorOutput(line, 'e');
                            }
                        }
                        paramCount++;
                        if (!curToken.equals(",")) {
                            break;
                        }
                        next();
                    }
                    if (funcSymbol.hasRParent() && paramCount != funcSymbol.getParamCount()) {
                        // d类错误，函数参数个数不匹配
                        addErrorOutput(line, 'd');
                    }
                    addSyntaxOutput("<FuncRParams>");
                } else {
                    if (funcSymbol.hasRParent() && funcSymbol.getParamCount() != 0) {
                        // d类错误，函数调用空参但实际不空
                        addErrorOutput(line, 'd');
                    }
                }
                if (!curToken.equals(")")) {            // j类错误，缺少右小括号
                    addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
                } else {
                    next();                     // read ')'
                }
                unaryExp.setUnaryBase(funcExp);
            } else {                                        // lvalue
                PrimaryExp primaryExp = new PrimaryExp(1, parseLvalue(curToken));
                addSyntaxOutput("<PrimaryExp>");
                unaryExp.setUnaryBase(primaryExp);
            }
        } else if (getCurTokenType().equals("INTCON")) {     // number
            PrimaryExp primaryExp = new PrimaryExp(2, Integer.parseInt(curToken));
            unaryExp.setUnaryBase(primaryExp);
            next();
            addSyntaxOutput("<Number>");
            addSyntaxOutput("<PrimaryExp>");
        } else if (curToken.equals("(")) {                  // exp
            next();
            Exp exp = parseExp();
            PrimaryExp primaryExp = new PrimaryExp(0, exp);
            if (curToken.equals(")")) {
                next();
            } else {
                addErrorOutput(tokens.get(pointer - 1).getLine(), 'j');
            }
            addSyntaxOutput("<PrimaryExp>");
            unaryExp.setUnaryBase(primaryExp);
        } else if (curToken.equals("+") || curToken.equals("-") || curToken.equals("!")) {
            // 只有当UnaryOp在第一个位置出现的时候才进入这个分支，设置flag来控制
            unaryExp.setOp(curToken);
            next();
            addSyntaxOutput("<UnaryOp>");
            UnaryExp unaryExpBase = parseUnaryExp();
            unaryExp.setUnaryBase(unaryExpBase);
        }
        addSyntaxOutput("<UnaryExp>");
        return unaryExp;
    }

    private MulExp parseMulExp() {
        MulExp mulExp = new MulExp();
        mulExp.addBase(parseUnaryExp());
        while (curToken.equals("*") || curToken.equals("/") || curToken.equals("%")) {
            addSyntaxOutput("<MulExp>");
            mulExp.addOp(curToken);
            next();
            mulExp.addBase(parseUnaryExp());
        }
        addSyntaxOutput("<MulExp>");
        return mulExp;
    }

    private AddExp parseAddExp() {
        AddExp addExp = new AddExp();
        addExp.addBase(parseMulExp());
        while (curToken.equals("+") || curToken.equals("-")) {
            addSyntaxOutput("<AddExp>");
            addExp.addOp(curToken);
            next();
            addExp.addBase(parseMulExp());
        }
        addSyntaxOutput("<AddExp>");
        return addExp;
    }

    private RelExp parseRelExp() {
        RelExp relExp = new RelExp();
        relExp.addBase(parseAddExp());
        while (curToken.equals("<") || curToken.equals(">") || curToken.equals("<=") || curToken.equals(">=")) {
            addSyntaxOutput("<RelExp>");
            relExp.addOp(curToken);
            next();
            relExp.addBase(parseAddExp());
        }
        addSyntaxOutput("<RelExp>");
        return relExp;
    }

    private EqExp parseEqExp() {
        EqExp eqExp = new EqExp();
        eqExp.addBase(parseRelExp());
        while (curToken.equals("==") || curToken.equals("!=")) {
            addSyntaxOutput("<EqExp>");
            eqExp.addOp(curToken);
            next();
            eqExp.addBase(parseRelExp());
        }
        addSyntaxOutput("<EqExp>");
        return eqExp;
    }

    private LAndExp parseLAndExp() {
        LAndExp lAndExp = new LAndExp();
        lAndExp.addBase(parseEqExp());
        while (curToken.equals("&&")) {
            addSyntaxOutput("<LAndExp>");
            lAndExp.addOp(curToken);
            next();
            lAndExp.addBase(parseEqExp());
        }
        addSyntaxOutput("<LAndExp>");
        return lAndExp;
    }

    private LOrExp parseLOrExp() {
        LOrExp lOrExp = new LOrExp();
        lOrExp.addBase(parseLAndExp());
        while (curToken.equals("||")) {
            addSyntaxOutput("<LOrExp>");
            lOrExp.addOp(curToken);
            next();
            lOrExp.addBase(parseLAndExp());
        }
        addSyntaxOutput("<LOrExp>");
        return lOrExp;
    }

    private ConstExp parseConstExp() {
        ConstExp constExp = new ConstExp(parseAddExp());
        addSyntaxOutput("<ConstExp>");
        return constExp;
    }

    public ArrayList<String> getSyntaxOutput() {
        return syntaxOutput;
    }

    public ArrayList<Error> getErrorOutput() {
        return errorOutput;
    }

    public static HashMap<Integer, SymbolTable> getSymbolTables() {
        return symbolTables;
    }

    private Boolean isValidFormatString(String s) {
        for (int i = 1; i < s.length() - 1; i++) {      // 跳过左右的引号
            char c = s.charAt(i);
            if (c == '%' && s.charAt(i + 1) != 'd') {
                return false;
            }
            if (!isValidNormalChar(c)) {
                return false;
            }
            if (c == 92) {              // c == '\'
                if (s.charAt(i + 1) != 'n') {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    private Boolean isValidNormalChar(char c) {
        return c == 32 || c == 33 || (c >= 40 && c <= 126) || c == '%';
    }
}
