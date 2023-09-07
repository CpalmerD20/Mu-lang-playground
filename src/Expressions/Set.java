package Expressions;

public class Set extends Expression {
    Expression object;
    Token name;
    Expression value;
    public Set(Expression object, Token name, Expression value) {
        this.object = object;
        this.name = name;
        this.value = value;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitSetExpr(this);
    }
}
