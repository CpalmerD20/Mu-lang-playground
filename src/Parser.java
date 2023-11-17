import java.util.ArrayList;
import java.util.List;
public class Parser {
    private static List<Token> tokens;
    private static int current = 0;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
    private static Expression expression() {
        return assignment();
    }
    private static Expression assignment() {
        Expression e = or();

        if (match(Types.EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

            if (e instanceof Expression.Data) {
                Token name = ((Expression.Data)e).name;
                return new Expression.Assign(name, value);
            }
            error(equals, "invalid assignment target.");
        }
        return e;
    }
    private static Expression or() {
        Expression ex = and();

        while (match(Types.OR)) {
            Token operator = previous();
            Expression right = and();
            ex = new Expression.Logical(ex, operator, right);
        }
        return ex;
    }
    private static Expression and() {
        Expression ex = equality();

        while (match(Types.AND)) {
            Token operator = previous();
            Expression right = equality();
            ex = new Expression.Logical(ex, operator, right);
        }
        return ex;
    }
    private static Expression equality() {
        Expression left = comparison();
        while (match(Types.BANG_EQUAL, Types.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }
    private static Expression comparison() {
        Expression left = term();

        while (match(Types.GREATER, Types.GREATER_EQUAL, Types.LESS, Types.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }
    private static Expression term() {
        Expression left = factor();

        while (match(Types.MINUS, Types.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }
    private static boolean match(Types... types) {
        for (Types type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    private static boolean check(Types type) {
        if (isAtEnd()) return false;

        return peek().type == type;
    }
    private static Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }
    private static boolean isAtEnd() {
        return peek().type == Types.EOF;
    }
    private static Token peek() {
        return tokens.get(current);
    }
    private static Token previous() {
        return tokens.get(current - 1);
    }
    private static Expression factor() {
        Expression left = unary();

        while (match (Types.SLASH, Types.STAR)) {
            Token operator = previous();
            Expression right = unary();
            left = new Expression.Binary(left, operator, right);
        }
        return left;
    }
    private static Expression unary() {
        if (match(Types.BANG, Types.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        return primary();
    }
    private static Expression primary() {
        if (match(Types.FALSE)) {
            return new Expression.Literal(false);
        }
        if (match(Types.VOID)) {
            return new Expression.Literal(null);
            //TODO do we want null?
        }
        if (match(Types.FLOAT, Types.STRING)) {
            return new Expression.Literal(previous().literal);
        }
        if (match(Types.IDENTIFIER)) {
            return new Expression.Data(previous());
        }
        if (match(Types.L_PAREN)) {
            Expression e = expression();
            consume(Types.R_PAREN, "expect ')' after expression");
            return new Expression.Grouping(e);
        }
        throw shoutError(peek(), "Expect Expression");
//        return null; //TODO REPLACE FOR LONGTERM
    }
    private static Token consume(Types type, String message) {
        if (check(type)) {
            return advance();
        }
        throw shoutError(peek(), message);
    }
    private static ParseError shoutError(Token token, String message) {
        error(token, message);
        return new ParseError();
    }
    static void error(Token token, String message) {
        Mouth.reportToken(token, "", message);
    }

    private static class ParseError extends RuntimeException {}

    static List<Statement> parse() {
        List<Statement> declarations = new ArrayList<>();
        try {
            while (!isAtEnd()) {
                declarations.add(declaration());
            }
        } catch (ParseError error) {
            return null;
        }
        return declarations;
        //TODO return after adding statements
    }
    private static Statement declaration() {
        try {
            if (match(Types.VARIABLE)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    private static Statement varDeclaration() {
        Token name = consume(Types.IDENTIFIER, "Expect variable name.");
        Expression init = null;
        if (match(Types.EQUAL)) {
            init = expression();
        }
        consume(Types.SEMICOLON, "Expect ':' after variable declaration.");
        return new Statement.Variable(name, init);
    }
    private static Statement statement() {
        if (match(Types.IF)) {
            return ifStatement();
        }
        if (match(Types.PRINT)) {
            return printStatement();
        }
        if (match(Types.REPEAT)) {
            return repeatStatement();
        }
        if (match(Types.L_CURLY)) {
            return new Statement.Block(block());
        }
        return expressionStatement();
    }

    private static Statement repeatStatement() {
        //TODO until needs to work like if break, while (false) {}
        Expression condition = null;

        if (peek().type == Types.UNTIL) {
            consume(Types.UNTIL, "Possible 'until' clause");
            condition = expression();
        }
        consume(Types.L_CURLY, "EXPECT '{' to begin body");
        Statement body = statement();
        consume(Types.R_CURLY, "Expect '}' to end body.");

        return new Statement.Repeat(condition, body);
    }

    private static Statement ifStatement() {
        //TODO remove the need for parens
//        consume(Types.L_PAREN, "Expect '(' after if.");
        Expression condition = expression();
        consume(Types.COLON, "Expect ':' to close condition.");
        Statement ifTrue = statement();
        Statement elseBranch = null;
        if (match(Types.ELSE)) {
            elseBranch = statement();
        }
        return new Statement.If(condition, ifTrue, elseBranch);
    }

    private static List<Statement> block() {
        List<Statement> phrases = new ArrayList<>();

        while (!check(Types.R_CURLY) && !isAtEnd()) {
            phrases.add(declaration());
        }
        consume(Types.R_CURLY, "Expect '}' after block.");
        return phrases;
    }

    private static Statement printStatement() {
        Expression value = expression();
        consume(Types.SEMICOLON, "Expect ':' after value.");
        return new Statement.Print(value);
    }
    private static Statement expressionStatement() {
        Expression expr = expression();
        consume(Types.SEMICOLON, "Expect ';' after expression.");
        return new Statement.ExpState(expr);
    }
    private static void synchronize() {
        //TODO make sure it works
        advance();

        while (!isAtEnd()) {
            if (previous().type == Types.SEMICOLON) {
                return;
            }

            switch (peek().type) {
                case MODEL:
                case CLOSURE:
                case VARIABLE:
                case DATA:
                case IF:
                case REPEAT:
                case PRINT:
                case RETURN: return;
            }

            advance();
        }
    }
}
