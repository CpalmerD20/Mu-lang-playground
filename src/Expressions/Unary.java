package Expressions;

public class Unary extends Expression {
    Token operator;
    Expression right;
    public Unary(Token operator, Expression right) {
        this.operator = operator;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitUnaryExpr(this);
    }
}
