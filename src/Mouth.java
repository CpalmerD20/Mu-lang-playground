import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Mouth {
    private static final Interpreter interpreter = new Interpreter();
    public Mouth() throws IOException {
        prompt();
    }
    void prompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("\n--> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }
    static void run(String line) {
        Lexer reader = new Lexer(line);
        List<Token> tokens = reader.scanTokens();
        Parser parser = new Parser(tokens);

        for (Token token : Parser.tokens) {
            System.out.print(token);
        }

        List<Statement> phrases = Parser.parse();
        assert phrases != null;
        interpreter.interpret(phrases);
    }
}