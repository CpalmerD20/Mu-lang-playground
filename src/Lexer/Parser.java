package Lexer;
import Expressions.*;

import java.lang.reflect.Type;
import java.util.List;
public class Parser {
    private static List<Token> tokens;
    private static int current = 0;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private static Expression expression() {
        return equality();
    }
    private static Expression equality() {
        Expression left = comparison();
        while (match(Types.BANG_EQUAL, Types.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            left = new Binary(left, operator, right);
        }
        return left;
    }
    private static Expression comparison() {
        Expression left = term();

        while (match(Types.GREATER, Types.GREATER_EQUAL, Types.LESS, Types.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            left = new Binary(left, operator, right);
        }
        return left;
    }
    private static Expression term() {
        Expression left = factor();

        while (match(Types.MINUS, Types.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Binary(left, operator, right);
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
            left = new Binary(left, operator, right);
        }
        return left;
    }
    private static Expression unary() {
        if (match(Types.BANG, Types.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Unary(operator, right);
        }
        return primary();
    }
    private static Expression primary() {
        if (match(Types.FALSE)) {
            return new Literal(false);
        }
        if (match(Types.VOID)) {
            return new Literal(null);
            //TODO do we want null?
        }
        if (match(Types.FLOAT, Types.STRING)) {
            return new Literal(previous().literal);
        }
        if (match(Types.LEFT_PAREN)) {
            Expression e = expression();
            consume(Types.RIGHT_PAREN, "expect ')' after expression");
            return new Grouping(e);
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

    static Expression parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
        //TODO return after adding statements
    }
}
