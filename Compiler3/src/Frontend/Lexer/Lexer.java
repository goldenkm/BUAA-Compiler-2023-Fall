package Frontend.Lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Lexer {
    private String input = "";
    private int pos = 0;
    private final String[] keywords = {"main", "const", "int", "break", "continue",
            "if", "else", "for", "getint", "printf", "return", "void"};
    private final String[] operator = {"|", "&", "+", "-", "*", "/", "%", "<", "=", "!", ">"};
    private final String[] brackets = {"(", ")", "[", "]", "{", "}"};
    private final String[] punctuation = {"\"", "\'", ",", ";"};
    private final String[] whitespace = {" ", "\n", "\t"};
    private ArrayList<Token> tokens = new ArrayList<>();    // (token, type)
    private boolean isSingleLineComment;
    private boolean isMultilineComment;
    private HashMap<String, String> tokenTypeMap;

    public Lexer() {
        initializeMap();
    }

    public void setInput(String input) {
        this.input = input;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void work(int line) {
        while (pos < input.length()) {
            if (isSingleLineComment) {      //单行注释，直接跳过
                break;
            }
            if (isMultilineComment) {      //若注释，则跳过
                if (pos < input.length() - 1 && input.charAt(pos) == '*' && input.charAt(pos + 1) == '/') {
                    isMultilineComment = false;
                    pos += 2;
                } else {
                    pos++;
                }
                continue;
            }
            char c = input.charAt(pos);
            if (Arrays.asList(whitespace).contains(String.valueOf(c))) {
                pos++;
                continue;
            }
            String token = "";
            String type = "";
            if (Character.isAlphabetic(c) || c == '_') {               //读入字符
                token = getWord();
                if (Arrays.asList(keywords).contains(token)) {   //关键字
                    type = tokenTypeMap.get(token);
                } else {                                            //标识符
                    type = "IDENFR";
                }
            } else if (Character.isDigit(c)) {                  //读入数字
                token = getNum();
                type = "INTCON";
            } else if (Arrays.asList(operator).contains(String.valueOf(c))) {    //读入运算符
                Token curToken = dealOperator(line);
                if (curToken == null) {
                    continue;
                }
                tokens.add(curToken);
                continue;
            } else if (Arrays.asList(brackets).contains(String.valueOf(c))) {   //读入括号
                pos++;
                token = String.valueOf(c);
                type = tokenTypeMap.get(token);
            } else if (Arrays.asList(punctuation).contains(String.valueOf(c))) {     //读入标点符号
                pos++;
                if (c == '\"') {
                    token = getString();
                    type = "STRCON";
                } else {
                    token = String.valueOf(c);
                    type = tokenTypeMap.get(token);
                }
            }
            Token curToken = new Token(token, line, type);
            tokens.add(curToken);
        }
        pos = 0;
        isSingleLineComment = false;            // 处理完一行，单行注释清零
    }

    public String getWord() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() &&
                (Character.isAlphabetic(input.charAt(pos))
                        || Character.isDigit(input.charAt(pos))
                        || input.charAt(pos) == '_')) {
            if (pos == input.length()) {
                break;
            }
            sb.append(input.charAt(pos));
            pos++;
        }
        return sb.toString();
    }

    public String getNum() {
        StringBuilder sb = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            sb.append(input.charAt(pos));
            pos++;
        }
        return sb.toString();
    }

    public Token dealOperator(int line) {
        char c = input.charAt(pos);
        String token = "";
        if (c == '+' || c == '-' || c == '%' || c == '*') {     // +, -, %
            pos++;
            token = String.valueOf(c);
        } else if (c == '|') {      // |, ||
            pos++;
            pos++;
            token = "||";
        } else if (c == '&') {      // &, &&
            pos++;
            pos++;
            token = "&&";
        } else if (c == '<' || c == '>' || c == '=' || c == '!') {  // <, <=, >, >=, !, !=, =, ==
            pos++;
            if (pos == input.length()) {        // 防止字符串下标越界
                token = String.valueOf(input.charAt(input.length() - 1));
            } else {
                c = input.charAt(pos);
                if (c == '=') {
                    token = input.charAt(pos - 1) + "=";
                    pos++;
                } else {
                    token = String.valueOf(input.charAt(pos - 1));
                }
            }
        } else if (c == '/') {            // /, //, /*
            pos++;
            c = input.charAt(pos);
            if (c == '/') {     // 处理单行注释//
                isSingleLineComment = true;
                pos++;
                return null;
            } else if (c == '*') {      // 处理多行注释
                isMultilineComment = true;
                pos++;
                return null;
            } else {            // 处理 /
                token = "/";
            }
        }
        return new Token(token, line, tokenTypeMap.get(token));
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        while (pos < input.length() && input.charAt(pos) != '\"') {
            sb.append(input.charAt(pos));
            pos++;
        }
        sb.append("\"");
        pos++;
        return sb.toString();
    }

    public void initializeMap() {
        tokenTypeMap = new HashMap<>();
        tokenTypeMap.put("Ident", "IDENFR");
        tokenTypeMap.put("IntConst", "INTCON");
        tokenTypeMap.put("FormatString", "STRCON");
        tokenTypeMap.put("main", "MAINTK");
        tokenTypeMap.put("const", "CONSTTK");
        tokenTypeMap.put("int", "INTTK");
        tokenTypeMap.put("break", "BREAKTK");
        tokenTypeMap.put("continue", "CONTINUETK");
        tokenTypeMap.put("if", "IFTK");
        tokenTypeMap.put("else", "ELSETK");
        tokenTypeMap.put("!", "NOT");
        tokenTypeMap.put("&&", "AND");
        tokenTypeMap.put("||", "OR");
        tokenTypeMap.put("for", "FORTK");
        tokenTypeMap.put("getint", "GETINTTK");
        tokenTypeMap.put("printf", "PRINTFTK");
        tokenTypeMap.put("return", "RETURNTK");
        tokenTypeMap.put("+", "PLUS");
        tokenTypeMap.put("-", "MINU");
        tokenTypeMap.put("void", "VOIDTK");
        tokenTypeMap.put("*", "MULT");
        tokenTypeMap.put("/", "DIV");
        tokenTypeMap.put("%", "MOD");
        tokenTypeMap.put("<", "LSS");
        tokenTypeMap.put("<=", "LEQ");
        tokenTypeMap.put(">", "GRE");
        tokenTypeMap.put(">=", "GEQ");
        tokenTypeMap.put("==", "EQL");
        tokenTypeMap.put("!=", "NEQ");
        tokenTypeMap.put("=", "ASSIGN");
        tokenTypeMap.put(";", "SEMICN");
        tokenTypeMap.put(",", "COMMA");
        tokenTypeMap.put("(", "LPARENT");
        tokenTypeMap.put(")", "RPARENT");
        tokenTypeMap.put("[", "LBRACK");
        tokenTypeMap.put("]", "RBRACK");
        tokenTypeMap.put("{", "LBRACE");
        tokenTypeMap.put("}", "RBRACE");
    }
}
