package Frontend.SyntaxComponents;

import com.sun.tools.javac.Main;

import java.util.ArrayList;

public class CompUnit {
    private ArrayList<Decl> globalDecls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public CompUnit() {
        this.globalDecls = new ArrayList<>();
        this.funcDefs = new ArrayList<>();
    }

    public void addDecl(Decl decl) {
        this.globalDecls.add(decl);
    }

    public void addFuncDefs(FuncDef funcDef) {
        this.funcDefs.add(funcDef);
    }

    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }

    public ArrayList<Decl> getGlobalDecls() {
        return globalDecls;
    }

    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }
}
