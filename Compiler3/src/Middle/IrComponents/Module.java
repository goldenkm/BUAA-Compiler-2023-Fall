package Middle.IrComponents;

import java.util.ArrayList;

public class Module {
    private ArrayList<GlobalVar> globalVars = new ArrayList<>();
    private ArrayList<Function> functions = new ArrayList<>();

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVars.add(globalVar);
    }

    public void addFunction(Function function) {
        this.functions.add(function);
    }

    public ArrayList<String> getIrOutput() {
        ArrayList<String> irOutput = new ArrayList<>();
        irOutput.add("declare i32 @getint()");
        irOutput.add("declare void @putint(i32)");
        irOutput.add("declare void @putch(i32)");
        irOutput.add("declare void @putstr(i8*)\n");
        for (GlobalVar globalVar : globalVars) {
            irOutput.add(globalVar.toLlvmIr());
        }
        for (Function function : functions) {
            irOutput.add(function.toLlvmIr());
        }
        return irOutput;
    }
}
