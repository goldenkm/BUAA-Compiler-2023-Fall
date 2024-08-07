import Frontend.Lexer.*;
import Frontend.Parser.Parser;
import Frontend.SyntaxComponents.CompUnit;
import Frontend.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {
    public static int pointer = 0;
    public static void main(String[] args) {
        File testFile = new File("testfile.txt");
        /* 从文件读取输入 */
        Scanner scanner = null;
        try {
            scanner = new Scanner(testFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int line = 1;
        /* 词法分析 */
        Lexer lexer = new Lexer();
        while (scanner.hasNextLine()) {
            lexer.setInput(scanner.nextLine());
            lexer.work(line);
            line++;
        }
        ArrayList<Token> tokens = lexer.getTokens();
        for (Token token : tokens) {
            System.out.println(token.getToken()+" "+token.getType()+"!");
        }
        Parser parser = new Parser(tokens);
        CompUnit compUnit = parser.parseCompUnit();
        /* 输出到文件 */
        File outFile = new File("output.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        for (Token token : lexer.getTokens()) {
            writer.println(token.getType() + " " + token.getToken());
        }
        writer.close();
    }
}
