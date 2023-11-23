import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens;
    private int start, current, line;
    public Lexer(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.start = 0;
        this.line = 1;
    }
    private static final Map<String, Types> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("each", Types.LAMBDA);
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
        keywords.put("print", Types.PRINT);
        keywords.put("define", Types.CLOSURE);
        keywords.put("as", Types.AS);
        keywords.put("is", Types.IS);
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

    private void makeIdentifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }
        String text = source.substring(start, current);
        Types type = keywords.get(text);
        if (type == null) {
            addToken(Types.IDENTIFIER, text);
        } else {
            addToken(type);
        }
    }
    void scanToken() {
        char glyph = advance();

        //TODO find out what is wrong with string
        switch (glyph) {

            case ' ', '\t', '\r':
                return;
            case '\n' : line += 1;
                return;
            case ';' : addToken(Types.SEMICOLON);
                return;
            case '"' : handleString();
                return;
            case '(' : addToken(Types.L_PAREN);
                break;
            case ')' : addToken(Types.R_PAREN);
                break;
            case '{' : addToken(Types.L_CURLY);
                break;
            case '}' : addToken(Types.R_CURLY);
                break;
            case '[' : addToken(Types.L_BRACE);
                break;
            case ']' : addToken(Types.R_BRACE);
                break;
            case '#' : addToken(Types.DATA);
                break;
            case ',' : addToken(Types.COMMA);
                break;
            case '-' : addToken(advanceIf('>') ? Types.BIT_RIGHT : Types.MINUS);
                break;
            case '+' : addToken(advanceIf('<') ? Types.BIT_LEFT : Types.PLUS);
                break;
            case '.' : addToken(advanceIf('.') ? Types.RANGE : Types.DOT);
                break;
            case '!' : addToken(advanceIf('=') ? Types.BANG_EQUAL : Types.BANG);
                break;
            case '/' : addToken(advanceIf('=') ? Types.DIVIDE_EQ : Types.SLASH);
                break;
            case '*' : addToken(advanceIf('=') ? Types.TIMES_EQ : Types.STAR);
                break;
            case '^' : addToken(advanceIf('*') ? Types.EXPONENT : Types.BIT_X0R);
                break;
            case '|' : addToken(Types.BIT_OR);
                break;
            case '&' : addToken(advanceIf('*') ? Types.NOTE_IN : Types.BIT_AND);
                break;
            case '~' : addToken(advanceIf('!') ? Types.NOTE_OUT : Types.BIT_NOT);
                break;
            case '=' : addToken(advanceIf('=') ? Types.EQUAL_EQUAL : Types.EQUAL);
                break;
            case '>' : addToken(advanceIf('=') ? Types.GREATER_EQUAL : Types.GREATER);
                break;
            case '<' : addToken(advanceIf('=') ? Types.LESS_EQUAL : Types.LESS);
                break;
            case ':' : addToken(advanceIf('=') ? Types.COLON_EQ : Types.COLON);
                break;
            case '%' : addToken(Types.MODULO);
                break;
            default : {
                if (isDigit(glyph)) {
                    number();
                } else if (isAlpha(glyph)) {
                    makeIdentifier();
                } else {
                    App.error(line, "unexpected character");
                }
            }
        }
    }
    private void handleString() {
        while (peek() != '"') {
            if(isAtEnd()) {
                App.error(line, "unterminated string");
                break;
            }
            if (peek() == '\n') {
                line += 1;
            }
            advance();

        }
        advance();
        String value = source.substring(start + 1, current - 1);
        addToken(Types.STRING, value);
    }
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(Types.EOF, "", null, line));
        return tokens;
    }
}
