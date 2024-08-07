package Middle;

import Frontend.Parser.Parser;
import Frontend.SymbolManager.Symbol;
import Frontend.SymbolManager.SymbolTable;
import Frontend.SyntaxComponents.AllExp.AddExp;
import Frontend.SyntaxComponents.AllExp.EqExp;
import Frontend.SyntaxComponents.AllExp.Exp;
import Frontend.SyntaxComponents.AllExp.FuncExp;
import Frontend.SyntaxComponents.AllExp.LAndExp;
import Frontend.SyntaxComponents.AllExp.LOrExp;
import Frontend.SyntaxComponents.AllExp.MulExp;
import Frontend.SyntaxComponents.AllExp.PrimaryExp;
import Frontend.SyntaxComponents.AllExp.RelExp;
import Frontend.SyntaxComponents.AllExp.UnaryExp;
import Frontend.SyntaxComponents.AllStmt.Block;
import Frontend.SyntaxComponents.AllStmt.BlockItem;
import Frontend.SyntaxComponents.AllStmt.BorCStmt;
import Frontend.SyntaxComponents.AllStmt.ExpStmt;
import Frontend.SyntaxComponents.AllStmt.ForLoopStmt;
import Frontend.SyntaxComponents.AllStmt.ForStmt;
import Frontend.SyntaxComponents.AllStmt.GetIntStmt;
import Frontend.SyntaxComponents.AllStmt.IfStmt;
import Frontend.SyntaxComponents.AllStmt.PrintStmt;
import Frontend.SyntaxComponents.AllStmt.ReturnStmt;
import Frontend.SyntaxComponents.AllStmt.Stmt;
import Frontend.SyntaxComponents.CompUnit;
import Frontend.SyntaxComponents.Cond;
import Frontend.SyntaxComponents.Decl;
import Frontend.SyntaxComponents.Def;
import Frontend.SyntaxComponents.FuncDef;
import Frontend.SyntaxComponents.FuncFParam;
import Frontend.SyntaxComponents.Lvalue;
import Frontend.SyntaxComponents.MainFuncDef;
import Middle.IrComponents.AllInstructions.AllocInstruction;
import Middle.IrComponents.AllInstructions.BrInstruction;
import Middle.IrComponents.AllInstructions.CalcInstruction;
import Middle.IrComponents.AllInstructions.CmpArgs;
import Middle.IrComponents.AllInstructions.CmpInstruction;
import Middle.IrComponents.AllInstructions.FuncInstruction;
import Middle.IrComponents.AllInstructions.GetIntInstruction;
import Middle.IrComponents.AllInstructions.MemInstruction;
import Middle.IrComponents.AllInstructions.PrintInstruction;
import Middle.IrComponents.AllInstructions.PtrInstruction;
import Middle.IrComponents.AllInstructions.RetInstruction;
import Middle.IrComponents.AllInstructions.TypeInstruction;
import Middle.IrComponents.BasicBlock;
import Middle.IrComponents.Function;
import Middle.IrComponents.GlobalVar;
import Middle.IrComponents.AllInstructions.Instruction;
import Middle.IrComponents.InstructionType;
import Middle.IrComponents.Module;
import Middle.IrComponents.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class IrBuilder {
    private CompUnit compUnit;
    private HashMap<Integer, SymbolTable> symbolTables;
    private int currentTableId;
    private int localVarCnt;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private int labelCnt;

    // 处理label设置的变量
    private String trueLabel;
    private boolean afterJump = false;
    private BrInstruction afterJumpInstr;

    // 处理break和continue
    private ForLoopStmt curLoop;

    // 处理到了哪个symbol就记录下它的id，在找符号表时只查找id比他小的，在定义变量时更新
    private int curSymbolId;

    public IrBuilder(CompUnit compUnit, HashMap<Integer, SymbolTable> symbolTables) {
        this.compUnit = compUnit;
        this.symbolTables = symbolTables;
        this.currentTableId = 0;
        this.localVarCnt = 0;
        this.labelCnt = 0;
        this.trueLabel = "";
    }

    public Module build() {
        Module module = new Module();
        for (Decl globalDecl : compUnit.getGlobalDecls()) {
            for (Def globalDef : globalDecl.getDefs()) {
                module.addGlobalVar(buildGlobalVar(globalDef));
            }
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            module.addFunction(buildFunction(funcDef));
        }
        module.addFunction(buildMainFunction(compUnit.getMainFuncDef()));
        return module;
    }

    public GlobalVar buildGlobalVar(Def globalDef) {
        GlobalVar globalVar = new GlobalVar(globalDef, symbolTables.get(currentTableId));
        curSymbolId = findSymbol(globalDef.getIdentifier()).getId();
        return globalVar;
    }

    public Function buildFunction(FuncDef funcDef) {
        Function function = new Function(funcDef.getName(),
                funcDef.getFuncType(), symbolTables.get(funcDef.getSonTableId()));
        curFunction = function;
        currentTableId = funcDef.getSonTableId();       // 进入函数所属的符号表
        function.addParams(funcDef.getFuncFParams(), symbolTables.get(currentTableId));
        localVarCnt += function.getParamsCnt();
        buildBasicBlock("Label_" + labelCnt++);
        // 将函数的参数压进栈
        for (int i = 0; i < funcDef.getFuncFParams().size(); i++) {
            FuncFParam funcFParam = funcDef.getFuncFParams().get(i);
            // 更新正在处理的symbolId
            curSymbolId = findSymbol(funcFParam.getIdentifier()).getId();
            Value paramIrVal = function.getParams().get(i);
            String irName = "%local_var_" + localVarCnt++;
            Value result;
            switch (funcFParam.getType()) {
                case 1 -> result = new Value(irName, 3);
                case 2 -> {
                    int dim2 = Integer.parseInt(funcFParam.getExp2D().getValue(symbolTables.get(currentTableId)));
                    result = new Value(irName, 6, 0, dim2);
                }
                default -> result = new Value(irName, 2);
            }
            // 压在栈上的地址才是形参后续参与运算的地址
            findSymbol(funcFParam.getIdentifier()).setIrValue(result);
            AllocInstruction allocInstr = new AllocInstruction(
                    InstructionType.ALLOCA, result);
            allocInstr.setVarType(result.getVarType());
            curBasicBlock.addInstruction(allocInstr);
            MemInstruction storeInstr = new MemInstruction(
                    InstructionType.STORE, paramIrVal, allocInstr);
            curBasicBlock.addInstruction(storeInstr);
        }
        // 解析block里的每一条语句
        for (BlockItem blockItem : funcDef.getBlock().getBlockItems()) {
            buildInstruction(blockItem);
        }
        if (funcDef.getFuncType() == 0) {       // void函数要返回一条ret void，会造成重复，后续可以在parser改
            curBasicBlock.addInstruction(new RetInstruction(
                    InstructionType.RET, new Value(), true));
            if (afterJump) {        // 防止block的最后一条语句是for但是没有return
                afterJumpInstr.setDest("Label_" + labelCnt);
                afterJump = false;
            }
        }
        localVarCnt = 0;
        labelCnt = 0;
        return function;
    }

    public Function buildMainFunction(MainFuncDef mainFuncDef) {
        Function mainFunc = new Function(
                "main", 1, symbolTables.get(mainFuncDef.getSonTableId()));
        currentTableId = mainFuncDef.getSonTableId();       // 进入函数所属的符号表
        curFunction = mainFunc;
        buildBasicBlock("Label_" + labelCnt++);
        for (BlockItem blockItem : mainFuncDef.getBlock().getBlockItems()) {
            buildInstruction(blockItem);
        }
        localVarCnt = 0;
        labelCnt = 0;
        return mainFunc;
    }

    public void buildBasicBlock(String label) {
        curBasicBlock = new BasicBlock(label);
        curFunction.addBasicBlock(curBasicBlock);
    }

    // if和for语句末尾需要干的一些事情，回填，建结束标签
    public void endJump(
            ArrayList<BrInstruction> unknownFalseLabels, ArrayList<BrInstruction> unknownDest) {
        String label = "Label_" + labelCnt++;
        for (BrInstruction unknownLabel : unknownFalseLabels) {
            unknownLabel.setFalseLabel(label);
        }
        unknownFalseLabels.clear();
        for (BrInstruction unknownLabel : unknownDest) {
            unknownLabel.setDest(label);
        }
        unknownDest.clear();
        // jump语句结束后单独有一个块
        buildBasicBlock(label);
        afterJumpInstr = new BrInstruction(InstructionType.BR, "unknown!!");
        afterJump = true;
        curBasicBlock.addInstruction(afterJumpInstr);
        buildBasicBlock("Label_" + labelCnt);
    }

    public void buildInstruction(BlockItem blockItem) {
        if (afterJump) {
            afterJumpInstr.setDest("Label_" + labelCnt);
            labelCnt++;
            afterJump = false;
        }
        if (blockItem instanceof Decl) {        // 转化decl
            for (Def def : ((Decl) blockItem).getDefs()) {
                defToInstr(def);
            }
        } else if (blockItem instanceof Stmt) {                                // 转化Stmt
            if (blockItem instanceof ReturnStmt) {
                curBasicBlock.addInstruction(returnStmtToInstr((ReturnStmt) blockItem));
            } else if (blockItem instanceof ForStmt) {
                curBasicBlock.addInstruction(forStmtToInstr((ForStmt) blockItem));
            } else if (blockItem instanceof GetIntStmt) {
                curBasicBlock.addInstruction(getIntStmtToInstr((GetIntStmt) blockItem));
            } else if (blockItem instanceof PrintStmt) {
                curBasicBlock.addInstruction(printStmtToInstr((PrintStmt) blockItem));
            } else if (blockItem instanceof ExpStmt) {
                // exp不需要再加一遍
                Value instr = expToInstr(((ExpStmt) blockItem).getExp());
            } else if (blockItem instanceof IfStmt) {
                ifStmtToInstr((IfStmt) blockItem);
            } else if (blockItem instanceof ForLoopStmt) {
                ForLoopStmt tmpLoop = curLoop;
                forLoopStmtToInstr((ForLoopStmt) blockItem);
                curLoop = tmpLoop;
            } else if (blockItem instanceof Block) {
                int tmpTableId = currentTableId;
                currentTableId = ((Block) blockItem).getSymbolTableId();    // 进入block的符号表
                for (BlockItem subItem : ((Block) blockItem).getBlockItems()) {
                    buildInstruction(subItem);
                }
                currentTableId = tmpTableId;        // 回到上层符号表
            } else if (blockItem instanceof BorCStmt) {
                bOrCToInstr((BorCStmt) blockItem);
            }
        } else {
            System.out.println("Error in buildInstruction!");
        }
    }

    public void defToInstr(Def def) {
        Value pointer = new Value("%local_var_" + localVarCnt++);
        switch (def.getType()) {
            case 1 -> {
                int dim1 = Integer.parseInt(def.getConstExp1().getValue(symbolTables.get(currentTableId)));
                pointer.setVarType(4);
                pointer.setCol(dim1);
            }
            case 2 -> {
                int dim1 = Integer.parseInt(def.getConstExp1().getValue(symbolTables.get(currentTableId)));
                int dim2 = Integer.parseInt(def.getConstExp2().getValue(symbolTables.get(currentTableId)));
                pointer.setVarType(5);
                pointer.setRow(dim1);
                pointer.setCol(dim2);
            }
            default -> pointer.setVarType(2);
        }
        AllocInstruction allocInstr = new AllocInstruction(InstructionType.ALLOCA, pointer);
        // 更新符号表，把中间代码的名字和类型存起来
        Symbol symbol = findSymbol(def.getIdentifier());
        symbol.setIrValue(allocInstr);
        curSymbolId = symbol.getId();
        findSymbol(def.getIdentifier()).setIrValue(allocInstr);
        curBasicBlock.addInstruction(allocInstr);
        if (def.hasInitialValue()) {
            // 把右值计算出来
            switch (def.getType()) {
                case 1 -> {
                    for (int i = 0; i < def.getRvalue().getExps().size(); i++) {
                        Exp exp = def.getRvalue().getExps().get(i);
                        Value result = new Value("%local_var_" + localVarCnt++, 2);
                        PtrInstruction gepInstr = new PtrInstruction(
                                InstructionType.PTR, result, pointer, String.valueOf(i));
                        curBasicBlock.addInstruction(gepInstr);
                        Value rValue = expToInstr(exp);
                        Instruction storeInstr = new MemInstruction(InstructionType.STORE,
                                rValue, gepInstr);
                        curBasicBlock.addInstruction(storeInstr);
                    }
                }
                case 2 -> {
                    for (int i = 0; i < def.getRvalue().getExpArrays().size(); i++) {
                        for (int j = 0; j < def.getRvalue().getExpArrays().get(i).size(); j++) {
                            Exp exp = def.getRvalue().getExpArrays().get(i).get(j);
                            Value result = new Value("%local_var_" + localVarCnt++, 2);
                            PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR, result,
                                    pointer, String.valueOf(i), String.valueOf(j));
                            gepInstr.setVarType(allocInstr.getVarType());
                            curBasicBlock.addInstruction(gepInstr);
                            Value rValue = expToInstr(exp);
                            Instruction storeInstr = new MemInstruction(InstructionType.STORE,
                                    rValue, gepInstr);
                            curBasicBlock.addInstruction(storeInstr);
                        }
                    }
                }
                default -> {
                    Value rValue = expToInstr(def.getRvalue().getExp());
                    Instruction storeInstr = new MemInstruction(InstructionType.STORE, rValue, pointer);
                    curBasicBlock.addInstruction(storeInstr);
                }
            }
        }
    }

    public Value expToInstr(Exp exp) {
        AddExp addExp = exp.getAddExp();
        return addExpToInstr(addExp);
    }

    public Value addExpToInstr(AddExp addExp) {
        Value localVar1 = mulExpToInstr((MulExp) addExp.getBases().get(0));
        for (int i = 1; i < addExp.getBases().size(); i++) {
            MulExp mulExp = (MulExp) addExp.getBases().get(i);
            Value localVar2 = mulExpToInstr(mulExp);
            Instruction newInstr = null;
            if (addExp.getOpList().get(i - 1).equals("+")) {
                newInstr = new CalcInstruction(InstructionType.ADD,
                        new Value("%local_var_" + localVarCnt), localVar1, localVar2);
            } else if (addExp.getOpList().get(i - 1).equals("-")) {
                newInstr = new CalcInstruction(InstructionType.SUB,
                        new Value("%local_var_" + localVarCnt), localVar1, localVar2);
            }
            curBasicBlock.addInstruction(newInstr);
            localVar1 = newInstr;
            localVarCnt++;
        }
        return addExp.getBases().size() == 1 ? localVar1 : curBasicBlock.getLastInstruction();
    }

    public Value mulExpToInstr(MulExp mulExp) {
        Value localVar1 = unaryExpToInstr((UnaryExp) mulExp.getBases().get(0));
        for (int i = 1; i < mulExp.getBases().size(); i++) {
            UnaryExp unaryExp = (UnaryExp) mulExp.getBases().get(i);
            Value localVar2 = unaryExpToInstr(unaryExp);
            Instruction newInstr = switch (mulExp.getOpList().get(i - 1)) {
                case "*" -> new CalcInstruction(InstructionType.MUL,
                        new Value("%local_var_" + localVarCnt), localVar1, localVar2);
                case "/" -> new CalcInstruction(InstructionType.SDIV,
                        new Value("%local_var_" + localVarCnt), localVar1, localVar2);
                case "%" -> new CalcInstruction(InstructionType.SREM,
                        new Value("%local_var_" + localVarCnt), localVar1, localVar2);
                default -> null;
            };
            curBasicBlock.addInstruction(newInstr);
            localVar1 = newInstr;
            localVarCnt++;
        }
        return mulExp.getBases().size() == 1 ? localVar1 : curBasicBlock.getLastInstruction();
    }

    public Value unaryExpToInstr(UnaryExp unaryExp) {       // 需要分情况讨论
        if (unaryExp.getOp().equals("-")) {     // 带负号的unaryExp
            CalcInstruction mulInstr = new CalcInstruction(InstructionType.MUL,
                    new Value("%local_var_" + localVarCnt++),
                    new Value("-1"),
                    unaryExpToInstr((UnaryExp) unaryExp.getUnaryBase()));
            curBasicBlock.addInstruction(mulInstr);
            return mulInstr;
        }
        if (unaryExp.getOp().equals("+")) {     // 带正号的unaryExp
            return unaryExpToInstr((UnaryExp) unaryExp.getUnaryBase());
        }
        if (unaryExp.getOp().equals("!")) {     // 带！的Exp
            Value op1 = unaryExpToInstr((UnaryExp) unaryExp.getUnaryBase());
            Value result = new Value("%local_var_" + localVarCnt++, 7);
            cmpToInstr(op1, new Value("0"), result, CmpArgs.EQ);
            // return zextResult的结果
            return curBasicBlock.getLastInstruction();
        }
        if (unaryExp.isExp()) {     // exp
            return expToInstr(((PrimaryExp) unaryExp.getUnaryBase()).getExp());
        }
        String name = unaryExp.getValue(symbolTables.get(currentTableId));
        if (unaryExp.getUnaryBase() instanceof FuncExp) {       // 函数调用
            return funcExpToInstr((FuncExp) unaryExp.getUnaryBase());
        }
        if (isNumber(name)) {           // 常量或数字，直接返回
            return new Value(name);
        }
        // LValue
        Symbol symbol = findSymbolByOrder(name);
        Value pointer = dealParamArray(symbol);
        // 不仅要知道左值的类型，还要知道原变量的类型
        switch (unaryExp.getType()) {
            case 2 -> {
                // 如果在左值出现二维数组，肯定是函数调用
                Value result = new Value("%local_var_" + localVarCnt++, 4, symbol.getDim2());
                PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR,
                        result, pointer, "0");
                curBasicBlock.addInstruction(gepInstr);
                return gepInstr;
            }
            case 1 -> {         // 一维数组，二维数组加一个[]
                switch (symbol.getType()) {
                    case ARRAY2 -> {        // 二维数组加一个[]
                        Value result = new Value("%local_var_" + localVarCnt++, 2);
                        Exp exp1 = unaryExp.getDim1();
                        String index1 = dealVarDim(exp1);
                        PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR,
                                result, pointer, index1, "0");
                        curBasicBlock.addInstruction(gepInstr);
                        return gepInstr;
                    }
                    case ARRAY1 -> {
                        Value result = new Value("%local_var_" + localVarCnt++, 2);
                        PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR,
                                result, pointer, "0");
                        curBasicBlock.addInstruction(gepInstr);
                        return gepInstr;
                    }
                }
            }
            default -> {        // 普通变量、一维数组加一个[]，二维数组加两个[]
                switch (symbol.getType()) {
                    case ARRAY1 -> {    // 一维数组加一个[]
                        Value result = new Value("%local_var_" + localVarCnt++, 2);
                        Exp exp1 = unaryExp.getDim1();
                        String index1 = dealVarDim(exp1);
                        PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR,
                                result, pointer, index1);
                        curBasicBlock.addInstruction(gepInstr);
                        pointer = gepInstr;
                    }
                    case ARRAY2 -> {    // 二维数组加两个[]
                        Value result = new Value("%local_var_" + localVarCnt++, 2);
                        Exp exp1 = unaryExp.getDim1();
                        String index1 = dealVarDim(exp1);
                        Exp exp2 = unaryExp.getDim2();
                        String index2 = dealVarDim(exp2);
                        PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR,
                                result, pointer, index1, index2);
                        gepInstr.setVarType(2);
                        curBasicBlock.addInstruction(gepInstr);
                        pointer = gepInstr;
                    }
                }
            }
        }
        Instruction loadInstr = new MemInstruction(
                InstructionType.LOAD, new Value("%local_var_" + localVarCnt++, 1), pointer);
        curBasicBlock.addInstruction(loadInstr);
        return loadInstr;
    }

    public Value funcExpToInstr(FuncExp funcExp) {
        FuncInstruction callInstr;
        if (funcExp.isVoid()) {
            callInstr = new FuncInstruction(
                    InstructionType.CALL, new Value(), true, funcExp.getFuncName());
        } else {
            Value result = new Value("%local_var_" + localVarCnt++);
            callInstr = new FuncInstruction(InstructionType.CALL, result, false, funcExp.getFuncName());
        }
        for (Exp funcParam : funcExp.getFuncParams()) {
            Value value = expToInstr(funcParam);
            // 函数参数类型就是此时最后一条指令的类型
            callInstr.addParam(value.getName(), value.typeToLlvm());
        }
        curBasicBlock.addInstruction(callInstr);
        return callInstr;
    }

    public Value returnStmtToInstr(ReturnStmt returnStmt) {
        RetInstruction retInstr;
        if (returnStmt.hasRetVal()) {
            Value retVal = expToInstr(returnStmt.getExp());
            retInstr = new RetInstruction(
                    InstructionType.RET, retVal, false);
        } else {
            retInstr = new RetInstruction(
                    InstructionType.RET, new Value(), true);
        }
        return retInstr;
    }

    public Value forStmtToInstr(ForStmt forStmt) {
        Value value = expToInstr(forStmt.getExp());
        // 赋值语句左边也需要分情况讨论，参考unaryExp
        return lvalueToInstr(value, forStmt.getLvalue());
    }

    public Value getIntStmtToInstr(GetIntStmt getIntStmt) {
        Value getIntResult = new Value("%local_var_" + localVarCnt++);
        GetIntInstruction getIntInstr = new GetIntInstruction(InstructionType.CALL, getIntResult);
        curBasicBlock.addInstruction(getIntInstr);
        Value pointer =  lvalueToInstr(getIntResult, getIntStmt.getLvalue());
        return new MemInstruction(InstructionType.STORE, getIntInstr, pointer);
    }

    private Value lvalueToInstr(Value value, Lvalue lvalue) {
        Symbol symbol = findSymbolByOrder(lvalue.getIdentifier());
        Value pointer = dealParamArray(symbol);
        switch (symbol.getType()) {     // 左边的值应该只能是普通变量（如果不是我会崩溃）
            case ARRAY1 -> {
                Exp exp1 = lvalue.getExp1();
                String index1 = dealVarDim(exp1);
                Value result = new Value("%local_var_" + localVarCnt++, 2);
                PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR, result,
                        pointer, index1);
                curBasicBlock.addInstruction(gepInstr);
                return new MemInstruction(InstructionType.STORE, value, gepInstr);
            }
            case ARRAY2 -> {
                Exp exp1 = lvalue.getExp1();
                String index1 = dealVarDim(exp1);
                Exp exp2 = lvalue.getExp2();
                String index2 = dealVarDim(exp2);
                Value result = new Value("%local_var_" + localVarCnt++, 2);
                PtrInstruction gepInstr = new PtrInstruction(InstructionType.PTR, result,
                        pointer, index1, index2);
                curBasicBlock.addInstruction(gepInstr);
                return new MemInstruction(InstructionType.STORE, value, gepInstr);
            }
            default -> {
                MemInstruction storeInstr = new MemInstruction(InstructionType.STORE, value, pointer);
                storeInstr.setVarType(value.getVarType());
                return storeInstr;
            }
        }
    }

    public Value printStmtToInstr(PrintStmt printStmt) {
        PrintInstruction printInstr = new PrintInstruction(InstructionType.CALL,
                new Value(), printStmt.getFormatString());
        for (Exp exp : printStmt.getExps()) {
            Value param = expToInstr(exp);
            Symbol symbol = findSymbol(param.getName());
            if (symbol != null) {
                printInstr.addParam(symbol.getIrName());
            } else {
                printInstr.addParam(param.getName());
            }
        }
        return printInstr;
    }

    public void condToInstr(
            Cond cond, ArrayList<BrInstruction> unknownFalseLabels) {
        lOrExpToInstr(cond.getLOrExp(), unknownFalseLabels);
    }

    public void lOrExpToInstr(LOrExp lOrExp,
                              ArrayList<BrInstruction> unknownFalseLabels) {
        for (int i = 0; i < lOrExp.getBases().size(); i++) {
            String currentLabel = "Label_" + labelCnt++;
            buildBasicBlock(currentLabel);
            // 回填falseExp
            for (BrInstruction unknownLabel : unknownFalseLabels) {
                unknownLabel.setFalseLabel(currentLabel);
            }
            unknownFalseLabels.clear();
            Value andExp = lAndExpToInstr((LAndExp) lOrExp.getBases().get(i), unknownFalseLabels);
            if (andExp == null) {
                continue;
            }
            String falseLabel = "Label_" + labelCnt;
            Value cmpResult = new Value("%local_var_" + localVarCnt++, 7);
            CmpInstruction cmpInstr = new CmpInstruction(
                    InstructionType.ICMP, cmpResult, CmpArgs.NE, andExp.getName(), "0");
            curBasicBlock.addInstruction(cmpInstr);
            BrInstruction brInstruction;
            if (i != lOrExp.getBases().size() - 1) {
                brInstruction = new BrInstruction(
                        InstructionType.BR, cmpResult.getName(), trueLabel, falseLabel);
            } else {
                brInstruction = new BrInstruction(
                        InstructionType.BR, cmpResult.getName(), trueLabel, "unknown");
                unknownFalseLabels.add(brInstruction);
            }
            curBasicBlock.addInstruction(brInstruction);
        }
    }

    public Value lAndExpToInstr(LAndExp lAndExp, ArrayList<BrInstruction> unknownFalseLabels) {
        if (lAndExp.getBases().size() == 1) {       // 如果LAndExp只有一个base，那就说明没有&&
            return eqExpToInstr((EqExp) lAndExp.getBases().get(0));
        }
        String unknown = "unknown";
        for (int i = 0; i < lAndExp.getBases().size(); i++) {
            if (i != 0) {       // 第一个andExp不用开标签，因为orExp已经开好了
                String currentLabel = "Label_" + labelCnt++;
                buildBasicBlock(currentLabel);
            }
            Value eqExp = eqExpToInstr((EqExp) lAndExp.getBases().get(i));
            Value cmpResult = new Value("%local_var_" + localVarCnt++, 7);
            CmpInstruction cmpInstr = new CmpInstruction(
                    InstructionType.ICMP, cmpResult, CmpArgs.NE, eqExp.getName(), "0");
            curBasicBlock.addInstruction(cmpInstr);
            String label = i == lAndExp.getBases().size() - 1 ? trueLabel : "Label_" + labelCnt;
            BrInstruction brInstruction = new BrInstruction(
                    InstructionType.BR, cmpResult.getName(), label, unknown);
            // 把LAndExp加进一个集合里，最后统一回填
            unknownFalseLabels.add(brInstruction);
            curBasicBlock.addInstruction(brInstruction);
        }
        return null;   // 每个base单独处理，不需要返回东西
    }

    public void ifStmtToInstr(IfStmt blockItem) {
        // 处理需要回填目的地的label
        ArrayList<BrInstruction> unknownFalseLabels = new ArrayList<>();
        ArrayList<BrInstruction> unknownDest = new ArrayList<>();
        // if语句前是一个块，需要在该块末尾加一个跳转指令跳转到if块的开始，因为下面开了1个label，所以此处需要加1
        BrInstruction brInstr = new BrInstruction(
                InstructionType.BR, "Label_" + (labelCnt + 1));
        curBasicBlock.addInstruction(brInstr);
        // 为if为真的情况开一个标签
        trueLabel = "Label_" + labelCnt++;
        condToInstr(blockItem.getCond(), unknownFalseLabels);
        Stmt ifStmt = blockItem.getIfStmt();
        // 处理if后面的语句，成一个块
        buildBasicBlock(trueLabel);
        buildInstruction(ifStmt);
        BrInstruction unknown1 = new BrInstruction(InstructionType.BR, "ifEndLabel");
        curBasicBlock.addInstruction(unknown1);
        unknownDest.add(unknown1);
        if (afterJump) {        // 针对if的最后一条语句是for的情况
            afterJumpInstr.setDest("Label_" + labelCnt++);
            afterJump = false;
        }
        // 处理else后面的语句，成一个块（如果有）
        boolean hasElse = blockItem.getElseStmt() != null;
        if (hasElse) {
            // 给else分配一个新的标签；
            String elseLabel = "Label_" + labelCnt++;
            buildBasicBlock(elseLabel);
            // 回填cond的最后一条语句
            for (BrInstruction unknownLabel : unknownFalseLabels) {
                unknownLabel.setFalseLabel(elseLabel);
            }
            ArrayList<BrInstruction> ifEnd = new ArrayList<>();
            for (BrInstruction unknown : unknownDest) {
                if (unknown.getDest().equals("ifEndLabel")) {
                    ifEnd.add(unknown);
                    continue;
                }
                unknown.setDest(elseLabel);
            }
            unknownFalseLabels.clear();
            unknownDest.clear();
            unknownDest.addAll(ifEnd);
            Stmt elseStmt = blockItem.getElseStmt();
            buildInstruction(elseStmt);
            BrInstruction unknown2 = new BrInstruction(InstructionType.BR, "unknown");
            curBasicBlock.addInstruction(unknown2);
            unknownDest.add(unknown2);
            if (afterJump) {        // 针对if的最后一条语句是for的情况
                afterJumpInstr.setDest("Label_" + labelCnt++);
                afterJump = false;
            }
        }
        // if语句结束后的标签
        endJump(unknownFalseLabels, unknownDest);
    }

    public void forLoopStmtToInstr(ForLoopStmt forLoopStmt) {
        curLoop = forLoopStmt;
        // 处理需要回填目的地的label
        ArrayList<BrInstruction> unknownFalseLabels = new ArrayList<>();
        ArrayList<BrInstruction> unknownDest = new ArrayList<>();
        boolean hasForStmt1 = forLoopStmt.getForStmt1() != null;
        boolean hasCond = forLoopStmt.getCond() != null;
        boolean hasForStmt2 = forLoopStmt.getForStmt2() != null;
        // for语句之前的指令连同for的forStmt1是一个块
        if (hasForStmt1) {
            buildInstruction(forLoopStmt.getForStmt1());
        }
        // for语句前是一个块，需要在该块末尾加一个跳转指令跳转到cond块的开始，因为下面开了1个label，所以此处需要加1
        BrInstruction brInstr = new BrInstruction(
                InstructionType.BR, hasCond ? "Label_" + (labelCnt + 1) : "Label_" + labelCnt);
        curBasicBlock.addInstruction(brInstr);
        trueLabel = "Label_" + labelCnt++;
        // 提前开好循环体的label，没有cond的话循环体的label就是trueLabel
        String loopLabel = hasCond ? "Label_" + labelCnt : trueLabel;
        // 解析cond
        if (forLoopStmt.getCond() != null) {
            condToInstr(forLoopStmt.getCond(), unknownFalseLabels);
        }
        buildBasicBlock(trueLabel);
        Stmt loopBody = forLoopStmt.getStmt();
        buildInstruction(loopBody);
        String forStmt2Label = "Label_" + labelCnt;
        if (!curBasicBlock.getLabel().equals(forStmt2Label)) {  // 不知道什么情况，强制避免一下
            curBasicBlock.addInstruction(new BrInstruction(InstructionType.BR, forStmt2Label));
        }
        if (afterJump) {    // 针对for的最后一条语句是if的情况
            labelCnt++;
            afterJumpInstr.setDest(forStmt2Label);
            afterJump = false;
        } else {
            labelCnt++;
            buildBasicBlock(forStmt2Label);
        }
        // 把continue语句单独筛出来回填
        for (BrInstruction unknown : forLoopStmt.getContinues()) {
            if (hasForStmt2) {
                unknown.setDest(forStmt2Label);
            } else {
                unknown.setDest(loopLabel);
            }
        }
        if (hasForStmt2) {
            buildInstruction(forLoopStmt.getForStmt2());
        }
        // 最后一句，跳回loop最开始
        BrInstruction back = new BrInstruction(InstructionType.BR, loopLabel);
        curBasicBlock.addInstruction(back);
        // break最后处理
        unknownDest.addAll(forLoopStmt.getBreaks());
        // for语句之后的标签
        endJump(unknownFalseLabels, unknownDest);
    }

    public void bOrCToInstr(BorCStmt stmt) {
        if (stmt.getContent().equals("break")) {
            // unknown代表的是跳出for或if语句，只有forStmt2代表的是跳到循环内部
            BrInstruction breakInstr =
                    new BrInstruction(InstructionType.BR, "unknown");
            curLoop.addBreak(breakInstr);
            curBasicBlock.addInstruction(breakInstr);
        } else {
            BrInstruction continueInstr =
                    new BrInstruction(InstructionType.BR, "forStmt2");
            curLoop.addContinue(continueInstr);
            curBasicBlock.addInstruction(continueInstr);
        }
    }

    public Value eqExpToInstr(EqExp eqExp) {
        Value relExp1 = relExpToInstr((RelExp) eqExp.getBases().get(0));
        for (int i = 1; i < eqExp.getBases().size(); i++) {
            Value relExp2 = relExpToInstr((RelExp) eqExp.getBases().get(i));
            Value result = new Value("%local_var_" + localVarCnt++, 7);
            String op = eqExp.getOpList().get(i - 1);
            CmpArgs arg = null;
            switch (op) {
                case "==" -> arg = CmpArgs.EQ;
                case "!=" -> arg = CmpArgs.NE;
                default -> System.out.println("Error in eqExpToInstr!");
            }
            cmpToInstr(relExp1, relExp2, result, arg);
            relExp1 = curBasicBlock.getLastInstruction();
        }
        return eqExp.getBases().size() == 1 ? relExp1 : curBasicBlock.getLastInstruction();
    }

    public Value relExpToInstr(RelExp relExp) {
        Value addExp1 = addExpToInstr((AddExp) relExp.getBases().get(0));
        for (int i = 1; i < relExp.getBases().size(); i++) {
            Value addExp2 = addExpToInstr((AddExp) relExp.getBases().get(i));
            Value cmpResult = new Value("%local_var_" + localVarCnt++, 7);
            String op = relExp.getOpList().get(i - 1);
            CmpArgs arg = null;
            switch (op) {
                case "<" -> arg = CmpArgs.SLT;
                case ">" -> arg = CmpArgs.SGT;
                case "<=" -> arg = CmpArgs.SLE;
                case ">=" -> arg = CmpArgs.SGE;
                default -> System.out.println("Error in relExpToInstr");
            }
            cmpToInstr(addExp1, addExp2, cmpResult, arg);
            addExp1 = curBasicBlock.getLastInstruction();
        }
        return relExp.getBases().size() == 1 ? addExp1 : curBasicBlock.getLastInstruction();
    }

    private void cmpToInstr(Value op1, Value op2, Value cmpResult, CmpArgs arg) {
        Instruction cmpInstr = new CmpInstruction(
                InstructionType.ICMP, cmpResult, arg, op1.getName(), op2.getName());
        curBasicBlock.addInstruction(cmpInstr);
        Value zextResult = new Value("%local_var_" + localVarCnt++);
        TypeInstruction zextInstr = new TypeInstruction(
                InstructionType.ZEXT, zextResult, cmpResult);
        curBasicBlock.addInstruction(zextInstr);
    }

    /**** 工具函数 ****/
    private boolean isNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i)) && s.charAt(i) != '-') {
                return false;
            }
        }
        return true;
    }

    private boolean isFuncParam(Value pointer) {
        return pointer.getVarType() == 6 || pointer.getVarType() == 3;
    }

    private Value dealParamArray(Symbol symbol) {     // 处理函数传过来的数组
        Value pointer = symbol.getIrValue();
        boolean isFuncParam = isFuncParam(pointer);
        if (isFuncParam) {     // 如果是函数传过来的数组，就需要先load
            Value loadResult = new Value("%local_var_" + localVarCnt++);
            loadResult.setVarType(pointer.getVarType() == 3 ? 2 : 4);
            loadResult.setCol(pointer.getCol());
            MemInstruction loadInstr = new MemInstruction(InstructionType.LOAD, loadResult, pointer);
            curBasicBlock.addInstruction(loadInstr);
            pointer = loadInstr;
        }
        return pointer;
    }

    private Symbol findSymbol(String name) {
        return Parser.getSymbolTables().get(currentTableId).findSymbol(name);
    }

    private Symbol findSymbolByOrder(String name) {
        return Parser.getSymbolTables().get(currentTableId).findSymbolByOrder(name, curSymbolId);
    }

    private String dealVarDim(Exp indexExp) {
        return expToInstr(indexExp).getName();
    }
}
