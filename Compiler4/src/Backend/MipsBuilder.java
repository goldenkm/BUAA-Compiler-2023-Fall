package Backend;

import Backend.Instructions.Addiu;
import Backend.Instructions.Addu;
import Backend.Instructions.Asciiz;
import Backend.Instructions.Bne;
import Backend.Instructions.Cmp;
import Backend.Instructions.Div;
import Backend.Instructions.J;
import Backend.Instructions.Jal;
import Backend.Instructions.Jr;
import Backend.Instructions.La;
import Backend.Instructions.Label;
import Backend.Instructions.Li;
import Backend.Instructions.Lw;
import Backend.Instructions.Mfhi;
import Backend.Instructions.Mflo;
import Backend.Instructions.MipsInstruction;
import Backend.Instructions.Move;
import Backend.Instructions.Mul;
import Backend.Instructions.Sll;
import Backend.Instructions.Subiu;
import Backend.Instructions.Subu;
import Backend.Instructions.Sw;
import Backend.Instructions.Syscall;
import Backend.MipsSymbolManager.MipsSymbolTable;
import Backend.MipsSymbolManager.MipsValue;
import Middle.IrComponents.AllInstructions.AllocInstruction;
import Middle.IrComponents.AllInstructions.BrInstruction;
import Middle.IrComponents.AllInstructions.CalcInstruction;
import Middle.IrComponents.AllInstructions.CmpInstruction;
import Middle.IrComponents.AllInstructions.FuncInstruction;
import Middle.IrComponents.AllInstructions.GetIntInstruction;
import Middle.IrComponents.AllInstructions.Instruction;
import Middle.IrComponents.AllInstructions.MemInstruction;
import Middle.IrComponents.AllInstructions.PrintInstruction;
import Middle.IrComponents.AllInstructions.PtrInstruction;
import Middle.IrComponents.AllInstructions.RetInstruction;
import Middle.IrComponents.AllInstructions.TypeInstruction;
import Middle.IrComponents.BasicBlock;
import Middle.IrComponents.InstructionType;
import Middle.IrComponents.IrFunction;
import Middle.IrComponents.IrGlobalVar;
import Middle.IrComponents.IrModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MipsBuilder {
    private final IrModule irModule;
    private static HashMap<Integer, String> REGS;
    private final MipsModule mipsModule;
    private final RegManager regManager;
    private MipsSymbolTable currentTable;
    private int fpOffset;       //  局部变量指针
    private final int GP = 28;
    private final int FP = 30;
    private int asciizLabelCnt;
    private boolean isMain = false;

    public MipsBuilder(IrModule irModule) {
        this.irModule = irModule;
        initialRegs();
        this.currentTable = new MipsSymbolTable(null);
        this.mipsModule = new MipsModule();
        this.fpOffset = 0;
        this.regManager = new RegManager();
        this.asciizLabelCnt = 0;

    }

    public MipsModule build() {
        // 生成全局变量
        buildGlobalVar();
        buildFunction();
        return mipsModule;
    }

    public void buildGlobalVar() {
        Li li1 = new Li(GP, 0x10000000);
        addMipsInstr(MipsInstrType.GLOBAL_VAR, li1);
        int gpOffset = 0;
        // 将全局变量直接存入以gp为基地址的内存
        for (IrGlobalVar globalVar : irModule.getGlobalVars()) {
            // 初始化全局变量只用一个$t0
            switch (globalVar.getType()) {
                case 1 -> {
                    ArrayList<Integer> initValues = globalVar.getInitVal1();
                    MipsValue mipsValue = new MipsValue(GP, gpOffset);
                    currentTable.addMipsSymbol("@" + globalVar.getToken(), mipsValue);
                    for (int i = 0; i < globalVar.getDim1(); i++) {
                        int imm = globalVar.hasInitVal() ? initValues.get(i) : 0;
                        initValToMips(imm, gpOffset, MipsInstrType.GLOBAL_VAR);
                        gpOffset += 4;
                    }
                }
                case 2 -> {
                    ArrayList<ArrayList<Integer>> initValues = globalVar.getInitVal2();
                    MipsValue mipsValue = new MipsValue(GP, gpOffset);
                    currentTable.addMipsSymbol("@" + globalVar.getToken(), mipsValue);
                    for (int i = 0; i < globalVar.getDim1(); i++) {
                        for (int j = 0; j < globalVar.getDim2(); j++) {
                            int imm = globalVar.hasInitVal() ? initValues.get(i).get(j) : 0;
                            initValToMips(imm, gpOffset, MipsInstrType.GLOBAL_VAR);
                            gpOffset += 4;
                        }
                    }
                }
                default -> {
                    int imm = globalVar.getInitVal0();
                    MipsValue mipsValue = new MipsValue(GP, gpOffset);
                    currentTable.addMipsSymbol("@" + globalVar.getToken(), mipsValue);
                    initValToMips(imm, gpOffset, MipsInstrType.GLOBAL_VAR);
                    gpOffset += 4;
                }
            }
        }
        // 写入函数运行栈基地址
        Li li = new Li(FP, 0x10080000);
        addMipsInstr(MipsInstrType.COMMON, li);
        // 跳转到main函数
        J j = new J("main");
        addMipsInstr(MipsInstrType.COMMON, j);
    }

    public void buildFunction() {
        MipsSymbolTable fatherTable = currentTable;
        currentTable = new MipsSymbolTable(fatherTable);
        int fatherFpOffset = fpOffset;
        for (IrFunction irFunction : irModule.getFunctions()) {
            fpOffset = 0;       // 进入一个新的函数调用要把fp偏移清零
            Label label = new Label(irFunction.getFuncName());
            addMipsInstr(MipsInstrType.COMMON, label);
            for (int i = 0; i < irFunction.getParams().size(); i++) {
                String param = irFunction.getParams().get(i).getName();
                if (i < 4) {
                    currentTable.addMipsSymbol(param, new MipsValue(4 + i));
                } else {
                    int offset = (irFunction.getParams().size() - i) * -4;
                    // 这里的baseReg是父函数的fp，也就是v1
                    currentTable.addMipsSymbol(param, new MipsValue(3, offset));
                }
            }
            isMain = irFunction.getFuncName().equals("main");
            for (int i = 0; i < irFunction.getBasicBlocks().size(); i++) {
                BasicBlock basicBlock = irFunction.getBasicBlocks().get(i);
                if (i != 0) {
                    addMipsInstr(MipsInstrType.COMMON, new Label(basicBlock.getLabel()));
                }
                buildBasicBlock(basicBlock);
            }
            currentTable = fatherTable;
            fpOffset = fatherFpOffset;
            regManager.unmapAll();      // 函数结束解除所有寄存器
        }
        // 程序结束
        Li li = new Li(2, 10);
        addMipsInstr(MipsInstrType.COMMON, li);
        Syscall syscall = new Syscall();
        addMipsInstr(MipsInstrType.COMMON, syscall);
    }

    public void buildBasicBlock(BasicBlock basicBlock) {
        for (Instruction irInstr : basicBlock.getInstructions()) {
            if (irInstr instanceof AllocInstruction) {  // alloca似乎是不需要任何指令的，只是给变量分配一个内存地址
                allocToMips((AllocInstruction) irInstr);
            } else if (irInstr instanceof MemInstruction) {
                memInstrToMips((MemInstruction) irInstr);
            } else if (irInstr instanceof PrintInstruction) {
                printToMips((PrintInstruction) irInstr);
            } else if (irInstr instanceof CalcInstruction) {
                calcToMips((CalcInstruction) irInstr);
            } else if (irInstr instanceof GetIntInstruction) {
                getIntToMips((GetIntInstruction) irInstr);
            } else if (irInstr instanceof PtrInstruction) {
                gepToMips((PtrInstruction) irInstr);
            } else if (irInstr instanceof BrInstruction) {
                brToMips((BrInstruction) irInstr);
            } else if (irInstr instanceof CmpInstruction) {
                cmpToMips((CmpInstruction) irInstr);
            } else if (irInstr instanceof RetInstruction) {
                if (!isMain) {
                    retToMips((RetInstruction) irInstr);
                }
            } else if (irInstr instanceof TypeInstruction) {
                typeToMips((TypeInstruction) irInstr);
            } else if (irInstr instanceof FuncInstruction) {
                funcToMips((FuncInstruction) irInstr);
            } else {
                System.out.println("Unknown IrInstruction in MipsBuilder!");
            }
        }
    }

    public void allocToMips(AllocInstruction irInstr) {
        MipsValue val = new MipsValue(FP, fpOffset);
        currentTable.addMipsSymbol(irInstr.getName(), val);
        if (irInstr.getCol() == 0 && irInstr.getRow() == 0) {       // 0维
            fpOffset += 4;
        } else if (irInstr.getRow() == 0 && irInstr.getCol() != 0) {        // 1维
            fpOffset += irInstr.getCol() * 4;
        } else {        // 2维
            fpOffset += irInstr.getCol() * irInstr.getRow() * 4;
        }
    }

    public void memInstrToMips(MemInstruction irInstr) {
        if (irInstr.getType() == InstructionType.LOAD) {        // Load
            int tmpReg = regManager.allocTmpReg();
            MipsValue pointer = currentTable.findSymbol(irInstr.getPointer().getName());
            int base = pointerToReg(pointer);
            if (tmpReg >= 22) {             // 如果没有寄存器了，就用t8当一个临时变量，把值存进内存里
                Lw lw = new Lw(tmpReg, base, pointer.getOffset());
                addMipsInstr(MipsInstrType.COMMON, lw);
                Sw sw = new Sw(tmpReg, FP, fpOffset);
                addMipsInstr(MipsInstrType.COMMON, sw);
                MipsValue tmpVal = new MipsValue(FP, fpOffset);
                currentTable.addMipsSymbol(irInstr.getName(), tmpVal);
                fpOffset += 4;
                regManager.unmap(tmpReg);
                return;
            }
            Lw lw = new Lw(tmpReg, base, pointer.getOffset());
            // 把pointer的寄存器unmap了
            regManager.unmap(pointer.getBaseReg());
            addMipsInstr(MipsInstrType.COMMON, lw);
            // 将load出来的结果存入一个临时寄存器中
            MipsValue tmpVal = new MipsValue(tmpReg);
            currentTable.addMipsSymbol(irInstr.getName(), tmpVal);
        } else {                // Store
            String irName = irInstr.getResult().getName();
            int srcReg = opToMips(irName);
            regManager.unmap(srcReg);       // 解除映射，不知道是否有风险？
            MipsValue pointer = currentTable.findSymbol(irInstr.getPointer().getName());
            int base = pointerToReg(pointer);
            Sw sw = new Sw(srcReg, base, pointer.getOffset());
            // 把pointer的寄存器unmap了
            regManager.unmap(pointer.getBaseReg());
            addMipsInstr(MipsInstrType.COMMON, sw);
        }
    }

    public void printToMips(PrintInstruction irInstr) {
        // 把字符串按照 %d 分割处理
        String formatStr = irInstr.getFormatString();
        StringBuilder sb = new StringBuilder();
        LinkedList<String> queue = new LinkedList<>(irInstr.getParams());
        for (int i = 1; i < formatStr.length() - 1; i++) {
            if (formatStr.charAt(i) == '%' && formatStr.charAt(i + 1) == 'd') {
                /* 处理刚刚的str */
                putStrToMips(sb.toString());
                /* 处理 %d */
                if (queue.isEmpty()) {      // 防止参数个数少于%d的个数
                    continue;
                }
                String irParam = queue.poll();
                putIntToMips(irParam);
                sb.setLength(0);
                i++;
                continue;
            }
            sb.append(formatStr.charAt(i));
        }
        // 处理最后一段str
        if (sb.length() > 0) {
            putStrToMips(sb.toString());
        }
    }

    public void putStrToMips(String str) {
        String label = "str_" + asciizLabelCnt++;
        addMipsInstr(MipsInstrType.ASCIIZ, new Asciiz(label, str));
        La la = new La(4, label);         // la $a0,str
        addMipsInstr(MipsInstrType.COMMON, la);
        Li li = new Li(2, 4);   // li $v0,4
        addMipsInstr(MipsInstrType.COMMON, li);
        addMipsInstr(MipsInstrType.COMMON, new Syscall());
    }

    public void putIntToMips(String irParam) {
        int tmpReg = opToMips(irParam);
        Move move = new Move(tmpReg, 4);
        addMipsInstr(MipsInstrType.COMMON, move);
        Li li = new Li(2, 1);          // li $v0,1
        addMipsInstr(MipsInstrType.COMMON, li);
        addMipsInstr(MipsInstrType.COMMON, new Syscall());  // syscall
        regManager.unmap(tmpReg);
    }

    public void initValToMips(int imm, int gpOffset, MipsInstrType type) {
        if (imm != 0) {
            Li li = new Li(8, imm);
            addMipsInstr(type, li);
            Sw sw = new Sw(8, GP, gpOffset);
            addMipsInstr(type, sw);
        }
    }

    public void calcToMips(CalcInstruction irInstr) {
        // irOp -> mipsOp -> regOp
        String irOp1 = irInstr.getOp1().getName();
        int regOp1 = opToMips(irOp1);
        String irOp2 = irInstr.getOp2().getName();
        int regOp2 = opToMips(irOp2);
        String result = irInstr.getResult().getName();
        int targetReg = regManager.allocTmpReg();
        MipsValue mipsSymbol = new MipsValue(targetReg);     // 临时分配一个寄存器给运算结果
        currentTable.addMipsSymbol(result, mipsSymbol);
        switch (irInstr.getType()) {
            case ADD -> {
                Addu addu = new Addu(targetReg, regOp1, regOp2);
                addMipsInstr(MipsInstrType.COMMON, addu);
            }
            case SUB -> {
                Subu subu = new Subu(targetReg, regOp1, regOp2);
                addMipsInstr(MipsInstrType.COMMON, subu);
            }
            case MUL -> {
                Mul mul = new Mul(targetReg, regOp1, regOp2);
                addMipsInstr(MipsInstrType.COMMON, mul);
            }
            case SDIV -> {
                Div div = new Div(regOp1, regOp2);
                addMipsInstr(MipsInstrType.COMMON, div);
                Mflo mflo = new Mflo(targetReg);
                addMipsInstr(MipsInstrType.COMMON, mflo);
            }
            case SREM -> {
                Div div = new Div(regOp1, regOp2);
                addMipsInstr(MipsInstrType.COMMON, div);
                Mfhi mfhi = new Mfhi(targetReg);
                addMipsInstr(MipsInstrType.COMMON, mfhi);
            }
        }
        regManager.unmap(regOp1);       // 释放两个op的寄存器
        regManager.unmap(regOp2);
    }

    public void getIntToMips(GetIntInstruction irInstr) {
        Li li = new Li(2, 5);
        addMipsInstr(MipsInstrType.COMMON, li);
        addMipsInstr(MipsInstrType.COMMON, new Syscall());
        int tmpReg = regManager.allocTmpReg();
        Move move = new Move(2, tmpReg);
        addMipsInstr(MipsInstrType.COMMON, move);
        currentTable.addMipsSymbol(irInstr.getResult().getName(), new MipsValue(tmpReg));
        //regManager.unmap(tmpReg);
    }

    public void gepToMips(PtrInstruction irInstr) {
        if (irInstr.getIndex2() == null) {       // 一维数组
            MipsValue pointer = currentTable.findSymbol(irInstr.getPointer().getName());
            int offsetReg1 = opToMips(irInstr.getIndex1());
            int newBaseReg = regManager.allocTmpReg();
            // 调用一个宏指令
            int oldBase = pointerToReg(pointer);
            // sll %offset, %offset, 2
            Sll sll = new Sll(offsetReg1, offsetReg1, 2);
            addMipsInstr(MipsInstrType.COMMON, sll);
            // addu %ans, %base, %offset
            Addu addu = new Addu(newBaseReg, oldBase, offsetReg1);
            addMipsInstr(MipsInstrType.COMMON, addu);
            MipsValue result = new MipsValue(newBaseReg, pointer.getOffset());
            currentTable.addMipsSymbol(irInstr.getResult().getName(), result);
            regManager.unmap(offsetReg1);
        } else {                                // 二维数组
            MipsValue pointer = currentTable.findSymbol(irInstr.getPointer().getName());
            int offsetReg1 = opToMips(irInstr.getIndex1());
            int offsetReg2 = opToMips(irInstr.getIndex2());
            int dim2 = irInstr.getDim2();
            int tmpReg = regManager.allocTmpReg();      // 用来存dim2
            Li li = new Li(tmpReg, dim2);
            addMipsInstr(MipsInstrType.COMMON, li);
            int newBaseReg = regManager.allocTmpReg();
            int oldBaseReg = pointerToReg(pointer);
            // mul %ans, %i, %dim2
            Mul mul = new Mul(newBaseReg, offsetReg1, tmpReg);
            addMipsInstr(MipsInstrType.COMMON, mul);
            // addu   %ans, %ans, %j
            Addu addu = new Addu(newBaseReg, newBaseReg, offsetReg2);
            addMipsInstr(MipsInstrType.COMMON, addu);
            // sll %ans, %ans, 2
            Sll sll = new Sll(newBaseReg, newBaseReg, 2);
            addMipsInstr(MipsInstrType.COMMON, sll);
            // addu %ans, %ans, %base
            Addu addu1 = new Addu(newBaseReg, newBaseReg, oldBaseReg);
            addMipsInstr(MipsInstrType.COMMON, addu1);
            MipsValue result = new MipsValue(newBaseReg, pointer.getOffset());
            currentTable.addMipsSymbol(irInstr.getResult().getName(), result);
            // 过程用到的寄存器全部解除映射
            regManager.unmap(offsetReg1);
            regManager.unmap(offsetReg2);
            regManager.unmap(tmpReg);
        }
    }

    public void brToMips(BrInstruction irInstr) {
        if (irInstr.getDest() != null) {        // 无条件跳转 j
            J j = new J(irInstr.getDest());
            addMipsInstr(MipsInstrType.COMMON, j);
        } else {
            String cond = irInstr.getCond();
            MipsValue condMipsSymbol = currentTable.findSymbol(cond);   // 假定cond都是存在寄存器里的
            Bne bne = new Bne(condMipsSymbol.getTmpReg(), 0, irInstr.getTrueLabel());
            addMipsInstr(MipsInstrType.COMMON, bne);
            J j = new J(irInstr.getFalseLabel());
            addMipsInstr(MipsInstrType.COMMON, j);
            regManager.unmap(condMipsSymbol.getTmpReg());       // 一个cond应该只服务于一个跳转，可以解除？
        }
    }

    public void funcToMips(FuncInstruction irInstr) {
        Sw savaRa = new Sw(31, 29, 0);          //  save $ra
        addMipsInstr(MipsInstrType.COMMON, savaRa);
        Subiu subiu = new Subiu(29, 29, 4);     // 栈顶 - 4
        addMipsInstr(MipsInstrType.COMMON, subiu);
        HashMap<Integer, Integer> regAddrMap = new HashMap<>();
        for (int i = 0; i < irInstr.getParams().size(); i++) {
            String param = irInstr.getParams().get(i);
            int reg = funcRParamToMips(param, irInstr.getParamType().get(i));
            if (i >= 4) {       // 多于4个参数就压进fp里
                Sw savaParam = new Sw(reg, FP, fpOffset);
                addMipsInstr(MipsInstrType.COMMON, savaParam);
                fpOffset += 4;
            } else {            // 小于4个参数就存进a里
                Move savaParam = new Move(reg, 4 + i);
                addMipsInstr(MipsInstrType.COMMON, savaParam);
            }
            regManager.unmap(reg);
        }
        Move fp2v1 = new Move(30, 3);           // 这一步必不可少，子函数要用
        addMipsInstr(MipsInstrType.COMMON, fp2v1);
        Addiu addV1 = new Addiu(3, 3, fpOffset);
        //这一步的意思v1存的是保存完参数，保存临时寄存器之前的地址，子函数可以从这里往回找参数
        addMipsInstr(MipsInstrType.COMMON, addV1);
        for (int i = 8; i <= 15; i++) {         // 将临时寄存器保存起来
            if (regManager.isUsed(i)) {
                Sw saveReg = new Sw(i, FP, fpOffset);
                addMipsInstr(MipsInstrType.COMMON, saveReg);
                regAddrMap.put(i, fpOffset);
                fpOffset += 4;
            }
        }
        for (int i = 16; i <= 25; i++) {
            if (regManager.isUsed(i)) {
                Sw saveReg = new Sw(i, FP, fpOffset);
                addMipsInstr(MipsInstrType.COMMON, saveReg);
                regAddrMap.put(i, fpOffset);
                fpOffset += 4;
            }
        }
        int fpStackSpace = fpOffset;
        Addiu saveFp = new Addiu(FP, FP, fpStackSpace);     // 将fp自增至子函数栈顶
        addMipsInstr(MipsInstrType.COMMON, saveFp);
        Jal jal = new Jal(irInstr.getFuncName());
        addMipsInstr(MipsInstrType.COMMON, jal);
        Addiu addiu = new Addiu(29, 29, 4);             // 栈顶 + 4
        addMipsInstr(MipsInstrType.COMMON, addiu);
        Lw restoreRa = new Lw(31, 29, 0);             // 恢复ra
        addMipsInstr(MipsInstrType.COMMON, restoreRa);
        Subiu restoreFp = new Subiu(FP, FP, fpStackSpace);   // 恢复fp
        addMipsInstr(MipsInstrType.COMMON, restoreFp);
        fpOffset = fpStackSpace;            // 恢复fpOffset
        for (Map.Entry<Integer, Integer> entry : regAddrMap.entrySet()) {   // 恢复临时寄存器
            Lw restoreReg = new Lw(entry.getKey(), FP, entry.getValue());
            addMipsInstr(MipsInstrType.COMMON, restoreReg);
            regManager.setUsed(entry.getKey());     // 将临时寄存器标记为使用过
            //fpOffset -= 4;          // 不加也没问题？
        }
        if (!irInstr.isVoid()) {            // 处理函数返回值
            int resultReg = regManager.allocTmpReg();
            Move move = new Move(2, resultReg);
            addMipsInstr(MipsInstrType.COMMON, move);
            Sw sw = new Sw(resultReg, FP, fpOffset);        // 将返回值存入栈中
            addMipsInstr(MipsInstrType.COMMON, sw);
            MipsValue mipsValue = new MipsValue(FP, fpOffset);
            fpOffset += 4;
            currentTable.addMipsSymbol(irInstr.getResult().getName(), mipsValue);
            regManager.unmap(resultReg);
        }
    }

    public void cmpToMips(CmpInstruction irInstr) {
        int opReg1 = opToMips(irInstr.getOp1());
        int opReg2 = opToMips(irInstr.getOp2());
        int resultReg = regManager.allocTmpReg();
        // 把比较结果存起来
        currentTable.addMipsSymbol(irInstr.getResult().getName(), new MipsValue(resultReg));
        Cmp cmp = new Cmp(irInstr.getCond(), resultReg, opReg1, opReg2);
        addMipsInstr(MipsInstrType.COMMON, cmp);
        regManager.unmap(opReg1);
        regManager.unmap(opReg2);
    }

    public void retToMips(RetInstruction irInstr) {
        if (!irInstr.isVoid()) {
            int reg = opToMips(irInstr.getResult().getName());
            Move move = new Move(reg, 2);       // 返回值存到v0
            addMipsInstr(MipsInstrType.COMMON, move);
            regManager.unmap(reg);
        }
        Jr jr = new Jr(31);
        addMipsInstr(MipsInstrType.COMMON, jr);
    }

    public void typeToMips(TypeInstruction irInstr) {       // 不需要指令
        int value = opToMips(irInstr.getValue().getName());
        currentTable.addMipsSymbol(irInstr.getResult().getName(), new MipsValue(value));
    }

    public int funcRParamToMips(String param, String type) {
        if (type.equals("i32")) {
            return opToMips(param);
        } else {        // 除了传递整数，其他的都需要传递一个地址，且是绝对地址
            MipsValue mipsValue = currentTable.findSymbol(param);
            int reg = regManager.allocTmpReg();
            Addiu addiu = new Addiu(reg, mipsValue.getBaseReg(), mipsValue.getOffset());
            addMipsInstr(MipsInstrType.COMMON, addiu);
            regManager.unmap(mipsValue.getBaseReg());       // 应该可以unmap，每个gep都会诞生一个新的mipsValue
            return reg;
        }
    }

    public int opToMips(String irOp) {
        int regOp;
        if (irOp.charAt(0) == '%') {
            MipsValue mipsOp1 = currentTable.findSymbol(irOp);
            if (!mipsOp1.isInReg()) {
                regOp = regManager.allocTmpReg();
                Lw lw = new Lw(regOp, mipsOp1.getBaseReg(), mipsOp1.getOffset());
                addMipsInstr(MipsInstrType.COMMON, lw);
            } else {
                regOp = mipsOp1.getTmpReg();
            }
        } else {
            regOp = regManager.allocTmpReg();
            Li li = new Li(regOp, Integer.parseInt(irOp));
            addMipsInstr(MipsInstrType.COMMON, li);
        }
        return regOp;
    }

    public int pointerToReg(MipsValue pointer) {
        int reg;
        if (pointer.isInReg()) {
            reg = pointer.getTmpReg();
            regManager.unmap(reg);
        } else {
            reg = pointer.getBaseReg();
        }
        return reg;
    }

    // tools
    public void addMipsInstr(MipsInstrType type, MipsInstruction instr) {
        switch (type) {
            case ASCIIZ -> mipsModule.addData(instr);
            case GLOBAL_VAR -> mipsModule.addGlobalVar(instr);
            default -> mipsModule.addText(instr);
        }
    }

    public void initialRegs() {
        REGS = new HashMap<>();
        REGS.put(0, "$zero");
        REGS.put(1, "$at");
        REGS.put(2, "$v0");
        REGS.put(3, "$v1");
        for (int i = 4; i <= 7; i++) {
            REGS.put(i, "$a" + (i - 4));
        }
        for (int i = 8; i <= 15; i++) {
            REGS.put(i, "$t" + (i - 8));
        }
        for (int i = 16; i <= 23; i++) {
            REGS.put(i, "$s" + (i - 16));
        }
        REGS.put(24, "$t8");
        REGS.put(25, "$t9");
        REGS.put(26, "$k0");
        REGS.put(27, "$k1");
        REGS.put(28, "$gp");
        REGS.put(29, "$sp");
        REGS.put(30, "$fp");
        REGS.put(31, "$ra");
    }

    public static String getReg(int regId) {
        return REGS.get(regId);
    }
}
