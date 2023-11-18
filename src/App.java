
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class App {
    static boolean hadError = false;
    static boolean hadInterpreterError = false;
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) throws IOException {
//        AppRunWithTokens promptWithTokens = new AppRunWithTokens();
        runPrompt();
        //TODO make let mutable
        //TODO implement join expression
        //TODO implement visitUntil for repeat {}
        //TODO implement return then lambda functions
        //TODO implement ! operator

//        if (arguments.length > 1) {
//            System.out.println("Using: mu-lox [script]");
//            System.exit(64);
//        } else if (arguments.length == 1) {
//            runFile(arguments[0]);
//        } else {
//            runPrompt();
//        }
    }
    static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
        if (hadInterpreterError) System.exit(70);
    }
    static void run(String line) {
        hadError = false;
        Lexer lex = new Lexer(line);
        List<Token> tokens = lex.scanTokens();

        Parser parser = new Parser(tokens);
        List<Statement> phrases = Parser.parse();

        if (hadError) {
            return;
        }
        assert phrases != null;

        interpreter.interpret(phrases);
    }

    static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("\n--> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }
    static void error(int line, String message) {
        report(line, "", message);
    }

    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
    static void reportToken(Token token, String where, String message) {
        System.err.println("[Token: " + token + "] Error" + where + ": " + message);
        hadError = true;
    }
}