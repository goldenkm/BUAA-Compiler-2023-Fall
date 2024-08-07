package Frontend.Parser;

import Frontend.SyntaxComponents.AddExp;
import Frontend.SyntaxComponents.AllStmt.BorCStmt;
import Frontend.SyntaxComponents.AllStmt.ExpStmt;
import Frontend.SyntaxComponents.AllStmt.ForLoopStmt;
import Frontend.SyntaxComponents.AllStmt.ForStmt;
import Frontend.SyntaxComponents.AllStmt.GetIntStmt;
import Frontend.SyntaxComponents.AllStmt.IfStmt;
import Frontend.SyntaxComponents.AllStmt.PrintStmt;
import Frontend.SyntaxComponents.AllStmt.ReturnStmt;
import Frontend.SyntaxComponents.AllStmt.SemicolonStmt;
import Frontend.SyntaxComponents.Block;
import Frontend.SyntaxComponents.BlockItem;
import Frontend.SyntaxComponents.CompUnit;
import Frontend.SyntaxComponents.Cond;
import Frontend.SyntaxComponents.ConstInitVal;
import Frontend.SyntaxComponents.Def;
import Frontend.SyntaxComponents.ConstExp;
import Frontend.SyntaxComponents.Decl;
import Frontend.SyntaxComponents.EqExp;
import Frontend.SyntaxComponents.Exp;
import Frontend.SyntaxComponents.FuncDef;
import Frontend.SyntaxComponents.FuncExp;
import Frontend.SyntaxComponents.FuncFParam;
import Frontend.SyntaxComponents.LAndExp;
import Frontend.SyntaxComponents.LOrExp;
import Frontend.SyntaxComponents.Lvalue;
import Frontend.SyntaxComponents.MainFuncDef;
import Frontend.SyntaxComponents.MulExp;
import Frontend.SyntaxComponents.PrimaryExp;
import Frontend.SyntaxComponents.RelExp;
import Frontend.SyntaxComponents.Stmt;
import Frontend.SyntaxComponents.UnaryExp;
import Frontend.SyntaxComponents.VarInitVal;
import Frontend.Token;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int pointer;
    private String curToken;
    private ArrayList<String> syntaxOutput = new ArrayList<>();
    private ArrayList<String> tmpOutput = new ArrayList<>();
    private boolean timeStop;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.pointer = 0;
        curToken = tokens.get(this.pointer).getToken();
        timeStop = false;
    }

    private void next() {
        StringBuilder sb = new StringBuilder();
        sb.append(getCurTokenType()).append(" ").append(curToken);
        addSyntaxOutput(sb.toString());
        if (pointer == tokens.size() - 1) {
            return;
        }
        curToken = tokens.get(++pointer).getToken();
        System.out.println(curToken);
    }

    private String getCurTokenType() {
        return tokens.get(pointer).getType();
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

    private void tmpToOutput() {
        syntaxOutput.addAll(tmpOutput);
        tmpOutput.clear();
    }

    public CompUnit parseCompUnit() {
        CompUnit compUnit = new CompUnit();
        while (true) {                                      // Decl
            if (curToken.equals("const")) {
                next();
                if (!curToken.equals("int")) {          // const后面必须跟int
                    // TODO: error;
                    System.out.println("Error in parseCompUnit!");
                }
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
            int funcType = curToken.equals("int") ? 1 : 0;
            next();
            if (curToken.equals("main")) {
                break;
            }
            addSyntaxOutput("<FuncType>");
            compUnit.addFuncDefs(parseFuncDef(funcType));
        }
        compUnit.setMainFuncDef(parseMainFuncDef());
        addSyntaxOutput("<CompUnit>");
        return compUnit;
    }

    private Decl parseConstDecl() {
        Decl constDecl = new Decl(true);
        while (true) {
            next();
            constDecl.addDef(parseConstDef());
            if (curToken.equals(",")) {
                continue;
            }
            if (curToken.equals(";")) {
                next();
                break;
            }
        }
        addSyntaxOutput("<ConstDecl>");
        return constDecl;
    }

    private Def parseConstDef() {
        if (!getCurTokenType().equals("IDENFR")) {
            // TODO:error;
        }
        Def constDef = new Def(curToken, true);
        int type = 0;
        next();
        if (curToken.equals("[")) {             // 一维数组
            next();
            constDef.setConstExp1(parseConstExp());
            if (!curToken.equals("]")) {
                // TODO:error;
            }
            next();
            if (curToken.equals("[")) {         // 二维数组
                next();
                constDef.setConstExp2(parseConstExp());
                type = 2;
                if (!curToken.equals("]")) {
                    // TODO:error;
                    System.out.println("Error in parseConstDef!");
                }
                next();
            } else {
                type = 1;
            }
        } else {
            type = 0;
        }
        if (!curToken.equals("=")) {            // 常量定义必须要有初值
            // TODO: error;
        }
        constDef.setType(type);
        next(); // identifier
        constDef.setRvalue(parseConstInitVal());
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

    private ArrayList<ConstExp> parseConstArrayInitVal() {
        ArrayList<ConstExp> exps = new ArrayList<>();
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
                continue;
            }
            if (curToken.equals(";")) {
                next();
                break;
            }
        }
        addSyntaxOutput("<VarDecl>");
        return varDecl;
    }

    private Def parseVarDef() {
        if (!getCurTokenType().equals("IDENFR")) {
            // TODO:error;
            System.out.println("ERROR in parseVarDef 1");
        }
        Def varDef = new Def(curToken, false);
        int type;
        next();
        if (curToken.equals("[")) {             // 一维数组
            next();
            varDef.setConstExp1(parseConstExp());
            if (!curToken.equals("]")) {
                // TODO:error;
                System.out.println("ERROR in parseVarDef 2");
            }
            next();
            if (curToken.equals("[")) {         // 二维数组
                next();
                varDef.setConstExp2(parseConstExp());
                type = 2;
                if (!curToken.equals("]")) {
                    // TODO:error;
                    System.out.println("ERROR in parseVarDef 3");
                }
                next();
            } else {
                type = 1;
            }
        } else {
            type = 0;
        }
        varDef.setType(type);
        if (curToken.equals("=")) {
            next();
            varDef.setRvalue(parseVarInitVal());
        }
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

    private FuncDef parseFuncDef(int funcType) {
        FuncDef funcDef = new FuncDef(curToken, funcType);
        next();
        if (!curToken.equals("(")) {
            // TODO: error;
            System.out.println("Error in parseFuncDef!");
        }
        next();
        if (!curToken.equals(")")) {            // 防止函数空参
            while (true) {
                funcDef.addFuncFParam(parseFuncFParam());
                if (curToken.equals(")")) {
                    addSyntaxOutput("<FuncFParams>");
                    break;
                }
                next();
            }
        }
        next();
        funcDef.setBlock(parseBlock());
        addSyntaxOutput("<FuncDef>");
        return funcDef;
    }

    private MainFuncDef parseMainFuncDef() {
        next();
        next();
        next();
        Block block = parseBlock();
        addSyntaxOutput("<MainFuncDef>");
        return new MainFuncDef(block);
    }

    private FuncFParam parseFuncFParam() {
        next();     //read 'int'
        FuncFParam funcParam = new FuncFParam(curToken);
        next();
        if (curToken.equals("[")) {
            next();
            if (curToken.equals("]")) {
                next();
                if (curToken.equals("[")) {             // 二维形参
                    next();
                    funcParam.setType(2);
                    funcParam.setExp2D(parseConstExp());
                    if (!curToken.equals("]")) {
                        // TODO: error;
                    }
                    next();
                } else {                                // 一维形参
                    funcParam.setType(1);
                }
            } else {
                // TODO: error;
            }
        } else {                                        // 普通形参
            funcParam.setType(0);
        }
        addSyntaxOutput("<FuncFParam>");
        return funcParam;
    }

    private Block parseBlock() {
        if (!curToken.equals("{")) {
            // TODO: error;
            System.out.println("ERROR in parseBlock!");
        }
        Block block = new Block();
        next();
        while (!curToken.equals("}")) {
            block.addBlockItem(parseBlockItem());
        }
        next();
        addSyntaxOutput("<Block>");
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
                BorCStmt breakStmt = new BorCStmt("break");
                next();                     // read 'break'
                next();                     // read ';'
                addSyntaxOutput("<Stmt>");
                return breakStmt;
            }
            case "continue" -> {
                BorCStmt continueStmt = new BorCStmt("continue");
                next();                     // read 'continue'
                next();                     // read ';'
                addSyntaxOutput("<Stmt>");
                return continueStmt;
            }
            case "return" -> {
                next();
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
                Block block = parseBlock();
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
                    int tmpPointer = pointer;
                    timeStop = true;
                    Exp exp = parseExp();
                    timeStop = false;
                    if (curToken.equals(";")) {             // 试探成功，返回exp
                        tmpToOutput();
                        next();             // read ';'
                        ExpStmt expStmt = new ExpStmt(exp);
                        addSyntaxOutput("<Stmt>");
                        return expStmt;
                    } else {                                // 试探失败
                        tmpOutput.clear();
                        curToken = tokens.get(tmpPointer).getToken();
                        pointer = tmpPointer;
                        Lvalue lvalue = parseLvalue(curToken);      // 确定是左值
                        next();     // 读过'='
                        if (curToken.equals("getint")) {
                            GetIntStmt getIntStmt = new GetIntStmt(lvalue);
                            while (!curToken.equals(";")) {
                                next();
                            }
                            next();
                            addSyntaxOutput("<Stmt>");
                            return getIntStmt;
                        } else {
                            ForStmt forStmt = new ForStmt(lvalue, parseExp());
                            next();                 // read ';'
                            addSyntaxOutput("<Stmt>");
                            return forStmt;
                        }
                    }
                } else {
                    ExpStmt expStmt = new ExpStmt(parseExp());
                    next();         // read ';'
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
            // TODO: error;
            System.out.println("Error in parseIfStmt");
        }
        next();
        ifStmt.setIfStmt(parseStmt());
        if (curToken.equals("else")) {
            next();
            ifStmt.setElseStmt(parseStmt());
        }
        return ifStmt;
    }

    private ForLoopStmt parseForLoopStmt() {
        ForLoopStmt forLoopStmt = new ForLoopStmt();
        if (!curToken.equals("(")) {
            // TODO: error;
            System.out.println("Error in parseForLoopStmt!");
        }
        next();
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
        return forLoopStmt;
    }

    private ForStmt parseForStmt() {
        ForStmt forStmt = new ForStmt();
        forStmt.setLvalue(parseLvalue(curToken));
        next();     // 此处应该读过的是'='
        forStmt.setExp(parseExp());
        addSyntaxOutput("<ForStmt>");
        return forStmt;
    }

    private ReturnStmt parseReturnStmt() {
        ReturnStmt returnStmt = new ReturnStmt();
        if (!curToken.equals(";")) {
            returnStmt.setExp(parseExp());
        }
        next();             // read ';'
        return returnStmt;
    }

    private PrintStmt parsePrintStmt() {
        next();             // read '('
        PrintStmt printStmt = new PrintStmt();
        printStmt.setFormatString(curToken);
        next();
        if (curToken.equals(",")) {
            while (!curToken.equals(")")) {
                next();
                printStmt.setExp(parseExp());
            }
        }
        next();
        if (!curToken.equals(";")) {
            // TODO: error;
            System.out.println("Error in parsePrintStmt!");
        }
        next();
        return printStmt;
    }

    private Exp parseExp() {
        AddExp addExp = parseAddExp();
        addSyntaxOutput("<Exp>");
        return addExp;
    }

    private Cond parseCond() {
        Cond cond = new Cond(parseLOrExp());
        addSyntaxOutput("<Cond>");
        return cond;
    }

    private Lvalue parseLvalue(String identifier) {
        Lvalue lvalue = new Lvalue(identifier);
        next();
        if (curToken.equals("[")) {
            next();
            lvalue.setExp1(parseExp());
            if (curToken.equals("]")) {
                next();
                if (curToken.equals("[")) {             // 二维形参
                    next();
                    lvalue.setType(2);
                    lvalue.setExp2(parseExp());
                    if (!curToken.equals("]")) {
                        // TODO: error;
                        System.out.println("Error in parseLVal!");
                    }
                    next();                 // read ']'
                } else {                                // 一维形参
                    lvalue.setType(1);
                }
            } else {
                // TODO: error;
                System.out.println("Error in parseLVal");
            }
        } else {                                        // 普通形参
            lvalue.setType(0);
        }
        addSyntaxOutput("<LVal>");
        return lvalue;
    }

    private UnaryExp parseUnaryExp() {
        UnaryExp unaryExp = new UnaryExp();
        int flag = 0;
        while (true) {
            if (getCurTokenType().equals("IDENFR")) {
                if (getNextToken().equals("(")) {                    // func()
                    FuncExp funcExp = new FuncExp(curToken);
                    next();
                    if (!getNextToken().equals(")")) {
                        while (!curToken.equals(")")) {
                            next();
                            funcExp.addFuncParam(parseExp());
                        }
                        addSyntaxOutput("<FuncRParams>");
                    } else {
                        next();                 // read '('
                    }
                    next();                     // read ')'
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
                PrimaryExp primaryExp = new PrimaryExp(1, exp);
                next();
                addSyntaxOutput("<PrimaryExp>");
                unaryExp.setUnaryBase(primaryExp);
            } else if (flag == 0 && (curToken.equals("+") || curToken.equals("-") || curToken.equals("!"))) {
                // 只有当UnaryOp在第一个位置出现的时候才进入这个分支，设置flag来控制
                next();
                addSyntaxOutput("<UnaryOp>");
                UnaryExp unaryExpBase = parseUnaryExp();
                unaryExp.setUnaryBase(unaryExpBase);
            } else {
                break;
            }
            flag = 1;
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
        ConstExp constExp = new ConstExp();
        constExp.setAddExp(parseAddExp());
        addSyntaxOutput("<ConstExp>");
        return constExp;
    }

    public ArrayList<String> getSyntaxOutput() {
        return syntaxOutput;
    }
}
