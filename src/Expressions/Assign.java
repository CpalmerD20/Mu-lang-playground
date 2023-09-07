package Expressions;

public class Assign extends Expression{
    final Token name;
    final Expression value;
    public Assign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }
    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }
}
