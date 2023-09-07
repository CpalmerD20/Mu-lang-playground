package Expressions;

public class Literal extends Expression{
    final Object value;
    public Literal(Object value) {
        this.value = value;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }
}
