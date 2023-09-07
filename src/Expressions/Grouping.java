package Expressions;

public class Grouping extends Expression{
    Expression expression;
    public Grouping(Expression expression) {
        this.expression = expression;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitGroupingExpr(this);
    }
}
