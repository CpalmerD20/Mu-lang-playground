
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Compiler {
    static boolean hadError = false;
    public Compiler(String[] arguments) throws IOException {
        if (arguments.length > 1) {
            System.out.println("Using: jlox [script]");
            System.exit(64);
        } else if (arguments.length == 1) {
            runFile(arguments[0]);
        } else {
            runPrompt();
        }
    }

    static void run(String line) {
        hadError = false;
        Lexer scanner = new Lexer(line);
        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
    void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
//        run(line);
        if (hadError) System.exit(65);
    }
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}