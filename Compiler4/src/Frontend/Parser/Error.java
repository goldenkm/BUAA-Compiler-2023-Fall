package Frontend.Parser;

public class Error {
    private final int line;
    private final char errorType;

    public Error(int line, char errorType) {
        this.line = line;
        this.errorType = errorType;
    }

    public int getLine() {
        return line;
    }

    public char getErrorType() {
        return errorType;
    }
}
