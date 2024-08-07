package Frontend.SyntaxComponents.AllStmt;

public class BorCStmt implements Stmt {
    private String content;
    private String semicolon;

    public BorCStmt(String content) {
        this.content = content;
        semicolon = ";";
    }
}
