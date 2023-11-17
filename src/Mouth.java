import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Mouth {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    public static boolean hadInterpreterError = false;
    public Mouth(String[] arguments) throws IOException {
        if (arguments.length > 1) {
            System.out.println("Using: mu-lox [script]");
            System.exit(64);
        } else if (arguments.length == 1) {
            runFile(arguments[0]);
        } else {
            runPrompt();
        }
    }

    static void run(String line) {
        hadError = false;
        Lexer reader = new Lexer(line);
        List<Token> tokens = reader.scanTokens();
        Parser parser = new Parser(tokens);
        List<Statement> phrases = Parser.parse();

//      use to print basic tokens TODO REMOVE THE FOR LOOP
        for (Token token : tokens) {
            System.out.print(token);
        }

        if (hadError) {
            return;
        }
        assert phrases != null;

        interpreter.interpret(phrases);
    }
    void runPrompt() throws IOException {
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
    static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
//        run(line);
        if (hadError) System.exit(65);
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