package Expressions;

public class Get extends Expression {
    Expression object;
    Token name;
    public Get(Expression object, Token name) {
        this.object = object;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }
}
