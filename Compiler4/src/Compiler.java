import Backend.MipsBuilder;
import Backend.MipsModule;
import Frontend.Lexer.*;
import Frontend.Parser.Error;
import Frontend.Parser.Parser;
import Frontend.SyntaxComponents.CompUnit;
import Frontend.Lexer.Token;
import Middle.IrBuilder;
import Middle.IrComponents.IrModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * LLVM -> Mips
 */

public class Compiler {
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
        Parser parser = new Parser(tokens);
        /* 语法分析 */
        CompUnit compUnit = parser.parseCompUnit();
        /* 输出到output.txt */
        File outFile = new File("output.txt");
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(outFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //  语法输出
        for (String s : parser.getSyntaxOutput()) {
            outputWriter.println(s);
        }
        outputWriter.close();
        /* 输出到error.txt */
        File errorFile = new File("error.txt");
        PrintWriter errorWriter = null;
        try {
            errorWriter = new PrintWriter(errorFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        /* 错误处理 */
        ArrayList<Error> errors = parser.getErrorOutput();
        errors.sort(Comparator.comparingInt(Error::getLine));
        for (Error error : parser.getErrorOutput()) {
            errorWriter.println(error.getLine() + " " + error.getErrorType());
        }
        errorWriter.close();
        /* 输出到llvm_ir.txt */
        File llvmIrFile = new File("llvm_ir.txt");
        PrintWriter llvmIrWriter = null;
        try {
            llvmIrWriter = new PrintWriter(llvmIrFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        /* 中间代码生成 */
        IrBuilder irBuilder = new IrBuilder(compUnit, Parser.getSymbolTables());
        IrModule irModule = irBuilder.build();
        for (String s : irModule.getIrOutput()) {
            llvmIrWriter.println(s);
        }
        llvmIrWriter.close();
        /* 输出到mips.txt */
        File mipsFile = new File("mips.txt");
        PrintWriter mipsWriter = null;
        try {
            mipsWriter = new PrintWriter(mipsFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        /* mips生成 */
        MipsBuilder mipsBuilder = new MipsBuilder(irModule);
        MipsModule mipsModule = mipsBuilder.build();
        for (String s : mipsModule.getMipsOutput()) {
            mipsWriter.println(s);
        }
        mipsWriter.close();
    }
}
