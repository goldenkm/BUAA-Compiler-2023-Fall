package Frontend.SyntaxComponents.AllStmt;

public class BorCStmt implements Stmt {
    private String content;

    public BorCStmt(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
