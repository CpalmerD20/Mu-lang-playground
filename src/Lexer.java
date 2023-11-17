import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, Types> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("each", Types.LAMBDA_IN);
        keywords.put("true", Types.TRUE);
        keywords.put("false", Types.FALSE);
        keywords.put("join", Types.JOIN);
        keywords.put("match", Types.MATCH);
        keywords.put("and", Types.AND);
        keywords.put("or", Types.OR);
        keywords.put("if", Types.IF);
        keywords.put("else", Types.ELSE);
        keywords.put("void", Types.VOID);
        keywords.put("repeat", Types.REPEAT);
        keywords.put("until", Types.UNTIL);
        keywords.put("skip", Types.SKIP);
        keywords.put("stop", Types.STOP);
        keywords.put("return", Types.RETURN);
        keywords.put("let", Types.VARIABLE);
        keywords.put("model", Types.MODEL);
        keywords.put("self", Types.SELF);
        keywords.put("print", Types.PRINT);
        keywords.put("define", Types.CLOSURE);
        keywords.put("as", Types.AS);
        keywords.put("is", Types.IS);
    }

    public Lexer(String source) {
        this.source = source;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(Types type) {
        addToken(type, null);
    }
    private void addToken(Types type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private boolean advanceIf(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current += 1;
        return true;
    }
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }
    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }
    private boolean isDigit(char glyph) {
        return glyph >= '0' && glyph <= '9';
    }
    private void number() {
        while (isDigit(peek())) {
            advance();
            if (peek() == '.' && isDigit(peekNext())) {
                advance();
                while (isDigit(peek())) {
                    advance();
                }
            }
        }
        addToken(Types.FLOAT, Double.parseDouble((source.substring(start, current))));

    }
    private boolean isAlpha(char glyph) {
        return (glyph >= 'a' && glyph <= 'z') ||
                (glyph >= 'A' && glyph <= 'Z') ||
                glyph == '_';
    }
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        Types type = keywords.get(text);
        if (type == null) type = Types.IDENTIFIER;
        addToken(type);
    }
    void scanToken() {
        char glyph = advance();

        //TODO find out what is wrong with string
        switch (glyph) {
            case '"' -> string();
            case ' ' -> noOp();
            case '\r' -> noOp();
            case '\t' -> noOp();
            case '\n' -> line += 1;
            case '(' -> addToken(Types.L_PAREN);
            case ')' -> addToken(Types.R_PAREN);
            case '{' -> addToken(Types.L_CURLY);
            case '}' -> addToken(Types.R_CURLY);
            case '[' -> addToken(Types.L_BRACE);
            case ']' -> addToken(Types.R_BRACE);
            case ',' -> addToken(Types.COMMA);
            case '.' -> addToken(Types.DOT);
            case '-' -> addToken(Types.MINUS);
            case '+' -> addToken(Types.PLUS);
            case '%' -> addToken(Types.MODULO);
            case ':' -> addToken(Types.COLON);
            case ';' -> addToken(Types.SEMICOLON);
            case '#' -> addToken(Types.DATA);
            case '/' -> addToken(advanceIf('>') ? Types.LAMBDA_OUT : Types.SLASH);
            case '*' -> addToken(advanceIf('&') ? Types.NOTE_OUT : Types.STAR);
            case '^' -> addToken(advanceIf('*') ? Types.EXPONENT : Types.BIT_X0R);
            case '&' -> addToken(advanceIf('*') ? Types.NOTE_IN : Types.BIT_AND);
            case '!' -> addToken(advanceIf('=') ? Types.BANG_EQUAL : Types.BANG);
            case '=' -> addToken(advanceIf('=') ? Types.EQUAL_EQUAL : Types.EQUAL);
            case '>' -> addToken(advanceIf('=') ? Types.GREATER_EQUAL : Types.GREATER);
            case '<' -> addToken(advanceIf('=') ? Types.LESS_EQUAL : Types.LESS);
            default -> {
                if (isDigit(glyph)) {
                    number();
                } else if (isAlpha(glyph)) {
                    identifier();
                } else {
                    Mouth.error(line, "unexpected character");
                }
            }
        }
    }

    private void noOp() {
    }

    //TODO does not work
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line += 1;
                advance();
            }
        }
        if (isAtEnd()) {
            Mouth.error(line, "unterminated string");
            return;
        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(Types.STRING, value);
    }
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
            tokens.add(new Token(Types.EOF, "", null, line));
        }
        return tokens;
    }
}
