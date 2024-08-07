package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Cond;
import Middle.IrComponents.AllInstructions.BrInstruction;

import java.util.ArrayList;

public class ForLoopStmt implements Stmt {
    private ForStmt forStmt1;
    private Cond cond;
    private ForStmt forStmt2;
    private Stmt stmt;

    // 为了处理break和continue所作的巨大努力
    private ArrayList<BrInstruction> breaks = new ArrayList<>();
    private ArrayList<BrInstruction> continues = new ArrayList<>();

    public void setForStmt1(ForStmt forStmt1) {
        this.forStmt1 = forStmt1;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public void setForStmt2(ForStmt forStmt2) {
        this.forStmt2 = forStmt2;
    }

    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }

    public ForStmt getForStmt1() {
        return forStmt1;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getForStmt2() {
        return forStmt2;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public void addContinue(BrInstruction brInstruction) {
        this.continues.add(brInstruction);
    }

    public void addBreak(BrInstruction brInstruction) {
        this.breaks.add(brInstruction);
    }

    public ArrayList<BrInstruction> getBreaks() {
        return breaks;
    }

    public ArrayList<BrInstruction> getContinues() {
        return continues;
    }
}
