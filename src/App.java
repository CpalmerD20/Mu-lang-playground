
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {

    //TODO implement join expression
    //TODO finish implementing lambda expression, pesky bug when called.
    //TODO implement ternary assignment (update if else)
    static boolean hadError = false;
    static boolean hadInterpreterError = false;
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) throws IOException {
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Path.of("/src/App.mu"));
        } catch (Exception e) {
            var stackTrace = e.getStackTrace();
            int end = stackTrace.length - 1;

            System.out.println("---------------");
            System.out.println("EXCEPTION: " + stackTrace[end].toString());
            System.out.println("MESSAGE " + e.getMessage());
            System.out.println(e.getClass());
        } finally {
            System.out.println("---------------");
        }

        run(AppTest.CODE_SAMPLE);

        if (bytes.length > 0) {
            runFile(bytes);
        } else {
            runPrompt();
        }
    }
    static void runFile(byte[] array) {
        run(new String(array, Charset.defaultCharset()));
        if (hadError) System.exit(65);
        if (hadInterpreterError) System.exit(70);
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
    static void run(String line) {
        hadError = false;
        Lexer lex = new Lexer(line);
        List<Token> tokens = lex.scanTokens();

        Parser parser = new Parser(tokens);
        List<Statement> phrases = Parser.parse();

//        for (Token token : Parser.tokens) {
//            System.out.print(token);
//        }

        if (hadError) {
            return;
        }
        assert phrases != null;
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(phrases);
        if (hadError) {
            return;
        }
        interpreter.interpret(phrases);
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