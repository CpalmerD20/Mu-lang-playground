import java.util.ArrayList;
import java.util.List;
public class Parser {
    public static List<Token> tokens;
    private static int current = 0;
    private static int loops = 0; //not sure if needed, attempting to get 'until true' to work
    public Parser(List<Token> tokens) {
        Parser.tokens = tokens;
        current = 0;
    }
    private static Expression expression() {
        return assignment();
    }
    private static Expression assignment() {
        Expression e = or();

        if (match(Types.EQUAL)) {
            Token equals = previous();
            Expression value = assignment();

//            if (e instanceof Expression.Variable) {
//                Token name = ((Expression.Variable)e).name;
//                return new Expression.Assign(name, value);
//            }
            //TODO both let and # are Expression.Data at this point
            if (e instanceof Expression.Data) {
                Token name = ((Expression.Data)e).name;
                return new Expression.Assign(name, value);
            }
            error(equals, "Can only reassign to a declared variable. [ variables are declared with the 'let' keyword. ]");
        }
        return e;
    }
    private static Expression or() {
        Expression exp = and();

        while (match(Types.OR)) {
            Token operator = previous();
            Expression right = and();
            exp = new Expression.Logical(exp, operator, right);
        }
        return exp;
    }
    private static Expression and() {
        Expression exp = equality();

        while (match(Types.AND)) {
            Token operator = previous();
            Expression right = equality();
            exp = new Expression.Logical(exp, operator, right);
        }
        return exp;
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
    private static Expression call() {
        Expression exp = primary();
        while (true) {
            if (match(Types.L_PAREN)) {
                exp = finishCall(exp);
            } else {
                break;
            }
        }
        return exp;
    }

    private static Expression finishCall(Expression called) {
        List<Expression> parameters = new ArrayList<>();
        //TODO make sure only function call needs ()
        if (!check(Types.R_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }
                parameters.add(expression());
            } while (match(Types.COMMA));
        }
        Token p = consume(Types.R_PAREN, "Expect ')' after paramenters.");
        return new Expression.Call(called, p, parameters);
    }

    private static Expression unary() {
        if (match(Types.BANG, Types.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        return call();
    }
    private static Expression primary() {
        if (match(Types.FALSE)) {
            return new Expression.Literal(false);
        }
        if (match(Types.TRUE)) {
            return new Expression.Literal(true);
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
        App.reportToken(token, "", message);
    }

    private static class ParseError extends RuntimeException {}

    public static List<Statement> parse() {
        List<Statement> declarations = new ArrayList<>();
        try {
            while (!isAtEnd()) {
                Statement phrase = declaration();
                declarations.add(phrase);
            }
            return declarations;
        } catch (ParseError error) {
            return null;
        }

    }
    private static Statement declaration() {
        try {
            if (match(Types.VARIABLE)) {
                //TODO hash out mutable
                return varDeclaration();
            }
            if (match(Types.DATA)) {
                return dataDeclaration();
            }
            if (match(Types.CLOSURE)) {
                return closure("closure");
            }
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }
    private static Statement closure(String kind) {
        Token name = consume(Types.IDENTIFIER, "Expect " + kind + " name.");

        //TODO review to match closure spec
        consume(Types.L_PAREN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(Types.R_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
                parameters.add(consume(Types.IDENTIFIER, "Expect parameter name."));
            } while (match(Types.COMMA));
        }
        consume(Types.R_PAREN, "Expect ')' after parameters");
        consume(Types.L_CURLY, "Expect '{' before " + kind + " body");
        List<Statement> body = block(); //assumes left curly has already been matched
        return new Statement.Closure(name, parameters, body);
    }
    private static Statement varDeclaration() {
        Token name = consume(Types.IDENTIFIER, "Expect variable name.");
        Expression value = null;

        if (match(Types.EQUAL)) {
            value = expression();
        }

        consume(Types.SEMICOLON, "Expect ';' at end to declare variable.");
        return new Statement.Variable(name, value);
    }
    private static Statement dataDeclaration() {
        Token name = consume(Types.IDENTIFIER, "Expect data name.");
        Expression value = null;
        if (match(Types.EQUAL)) {
            value = expression();
        }
        consume(Types.SEMICOLON, "Expect ';' at end to declare data.");
        return new Statement.Data(name, value);
    }
    private static Statement statement() {
        if (match(Types.UNTIL)) {
            return untilStatement();
        }
        if (match(Types.IF)) {
            return ifStatement();
        }
        if (match(Types.PRINT)) {
            return printStatement();
        }
        if (match(Types.RETURN)) {
            return returnStatement();
        }
        if (match(Types.REPEAT)) {
            return repeatStatement();
        }
        if (match(Types.L_CURLY)) {
            return new Statement.Block(block());
        }
        return expressionStatement();
    }

    private static Statement untilStatement() {
        if (loops < 1) {
            error(previous(), "Until expression must be inside repeat {}.");
        }

        Expression condition = expression();
        consume(Types.SEMICOLON, "Expect ';' to complete until expression.");
        return new Statement.Until(condition);
    }

    private static Statement returnStatement() {
        Token keyword = previous();
        Expression value = null;
        if (!check(Types.SEMICOLON)) {
            value = expression();
        }
        consume(Types.SEMICOLON, "Expect ';' to end return expression.");
        return new Statement.Return(keyword, value);
    }

    private static Statement repeatStatement() {
        //TODO until needs to work like if break, while (false) {}
        //TODO test with until

        Expression condition = null;
        if (peek().type == Types.UNTIL) {
            consume(Types.UNTIL, "Possible 'until' clause");
            condition = expression();
        }

        try {
            loops += 1;
            consume(Types.L_CURLY, "EXPECT '{' to begin repeat body");
            List<Statement> body = block(); //assumes left curly has already been matched
            return new Statement.Repeat(body);
        } finally {
            loops -= 1;
        }

    }

    private static Statement ifStatement() {
        //TODO enforce { } for else clause?
        Expression condition = expression();
        consume(Types.L_CURLY, "Expect '{' to open then condition.");
        Statement thenBranch = statement();
        Statement elseBranch = null;
        consume(Types.R_CURLY, "Expect '}' to close then condition.");
        if (match(Types.ELSE)) {
            elseBranch = statement();
        }
        return new Statement.If(condition, thenBranch, elseBranch);
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
        consume(Types.SEMICOLON, "Expect ';' after value.");
        return new Statement.Print(value);
    }
    private static Statement expressionStatement() {
        Expression expression = expression();
        consume(Types.SEMICOLON, "Expect ';' after expression.");
        return new Statement.Expr(expression);
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
                case RETURN:
                    return;
            }
            advance();
        }
    }
}
