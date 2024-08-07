package Middle.IrComponents;

import java.util.ArrayList;

public class IrModule {
    private ArrayList<IrGlobalVar> globalVars = new ArrayList<>();
    private ArrayList<IrFunction> functions = new ArrayList<>();

    public void addGlobalVar(IrGlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public void addFunction(IrFunction function) {
        this.functions.add(function);
    }

    public ArrayList<String> getIrOutput() {
        ArrayList<String> irOutput = new ArrayList<>();
        irOutput.add("declare i32 @getint()");
        irOutput.add("declare void @putint(i32)");
        irOutput.add("declare void @putch(i32)");
        irOutput.add("declare void @putstr(i8*)\n");
        for (IrGlobalVar globalVar : globalVars) {
            irOutput.add(globalVar.toLlvmIr());
        }
        for (IrFunction function : functions) {
            irOutput.add(function.toLlvmIr());
        }
        return irOutput;
    }

    public ArrayList<IrGlobalVar> getGlobalVars() {
        return globalVars;
    }

    public ArrayList<IrFunction> getFunctions() {
        return functions;
    }
}
