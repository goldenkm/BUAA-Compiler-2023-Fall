package Frontend;

public class Token {
    private String token = "";
    private int line;
    private String type = "";

    public Token(String token, int line, String type) {
        this.token = token;
        this.line = line;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public int getLine() {
        return line;
    }

    public String getType() {
        return type;
    }
}
