package Expressions;

public class Logical extends Expression{
    Expression left;
    Expression right;
    Token operator;
    public Logical(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitLogicalExpr(this);
    }

}
