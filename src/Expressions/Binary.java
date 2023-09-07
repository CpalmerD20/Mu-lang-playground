package Expressions;

public class Binary extends Expression {
    final Expression left;
    final Expression right;
    final Token operator;
    public Binary(Expression left, Token operator, Expression right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }
}
