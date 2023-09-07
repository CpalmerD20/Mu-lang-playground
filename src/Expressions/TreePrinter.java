package Expressions;

public class TreePrinter implements Expression.Visitor<String> {
    public String print(Expression expression) {
        return expression.accept(this);
    }

    private String parenthesize(String name, Expression... list) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expression expression : list) {
            builder.append(" ");
            builder.append(expression.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    @Override
    public String visitAssignExpr(Assign expression) {
        return null;

    }

    @Override
    public String visitBinaryExpr(Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }

    @Override
    public String visitCallExpr(Call expression) {
        return null;
    }

    @Override
    public String visitGetExpr(Get expression) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Grouping group) {
        return parenthesize("group", group.expression);
    }

    @Override
    public String visitLiteralExpr(Literal expression) {
        if (expression.value == null) return "nil";
        return expression.value.toString();
    }

    @Override
    public String visitLogicalExpr(Logical expression) {
        return null;
    }

    @Override
    public String visitSetExpr(Set expression) {
        return null;
    }

    @Override
    public String visitThisExpr(This expression) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }

    @Override
    public String visitDataExpr(Data expression) {
        return null;
    }
}
