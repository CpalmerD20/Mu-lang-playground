package Expressions;

public class This extends Expression {
    final Token keyword;
    public This(Token keyword) {
        this.keyword = keyword;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitThisExpr(this);
    }
}
