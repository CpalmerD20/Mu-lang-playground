package Expressions;

public class Data extends Expression {
    Token name;
    public Data(Token name) {
        this.name = name;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitDataExpr(this);
    }
}
