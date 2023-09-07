package Lexer;
import Expressions.*;

import java.lang.reflect.Type;
import java.util.List;
public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expression expression() {
        return equality();
    }
    private Expression equality() {
        Expression expression = comparison();
        while (match(Types.BANG_EQUAL, Types.EQUAL_EQUAL)) {
            Token operator = previous();
            Expression right = comparison();
            expression = new Binary(expression, right, operator);
        }
        return expression;
    }
    private Expression comparison() {
        Expression expression = term();

        while (match(Types.GREATER, Types.GREATER_EQUAL, Types.LESS, Types.LESS_EQUAL)) {
            Token operator = previous();
            Expression right = term();
            expression = new Binary(expression, right, operator);
        }
        return expression;
    }
    private Expression term() {
        Expression left = factor();

        while (match(Types.MINUS, Types.PLUS)) {
            Token operator = previous();
            Expression right = factor();
            left = new Binary(left, operator, right);
        }
        return left;
    }
    private boolean match(Types... types) {
        for (Types type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(Types type) {
        if (isAtEnd()) return false;

        return peek().type == type;
    }
    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }
    private boolean isAtEnd() {
        return peek().type == Types.EOF;
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
    private Expression factor() {
        Expression left = unary();

        while (match (Types.SLASH, Types.STAR)) {
            Token operator = previous();
            Expression right = unary();
            left = new Binary(left, operator, right);
        }
        return left;
    }
    private Expression unary() {
        if (match(Types.BANG, Types.MINUS)) {
            Token operator = previous();
            Expression right = unary();
            return new Unary(operator, right);
        }
        return primary();
    }
    private Expression primary() {
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
        return null; //TODO REPLACE FOR LONGTERM
    }
}
