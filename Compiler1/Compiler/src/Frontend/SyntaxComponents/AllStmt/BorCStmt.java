package Frontend.SyntaxComponents.AllStmt;

import Frontend.SyntaxComponents.Stmt;

public class BorCStmt implements Stmt {
    private String content;
    private String semicolon;

    public BorCStmt(String content) {
        this.content = content;
        semicolon = ";";
    }
}
