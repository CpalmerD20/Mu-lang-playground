package Expressions;

public abstract class Expression {
    interface Visitor<R> {
        R visitAssignExpr(Assign expression);
        R visitBinaryExpr(Binary expression);
        R visitCallExpr(Call expression);
        R visitGetExpr(Get expression);
        R visitGroupingExpr(Grouping expression);
        R visitLiteralExpr(Literal expression);
        R visitLogicalExpr(Logical expression);
        R visitSetExpr(Set expression);
        R visitThisExpr(This expression);
        R visitUnaryExpr(Unary expression);
        R visitDataExpr(Data expression);
    }
    abstract <R> R accept(Visitor<R> visitor);
}
