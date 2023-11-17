import java.util.List;
public class TreePrinter implements Expression.Visitor<String> {
    public String print(List<Expression> expressions) {
        for (Expression phrase : expressions) {
            return phrase.accept(this);
        }
        return "no expressions";
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
    public String visitAssignExpr(Expression.Assign expression) {
        return null;

    }
    @Override
    public String visitBinaryExpr(Expression.Binary expression) {
        return parenthesize(expression.operator.lexeme, expression.left, expression.right);
    }
    @Override
    public String visitCallExpr(Expression.Call expression) {
        return null;
    }
    @Override
    public String visitGetExpr(Expression.Get expression) {
        return null;
    }
    @Override
    public String visitGroupingExpr(Expression.Grouping group) {
        return parenthesize("group", group.expression);
    }
    @Override
    public String visitLiteralExpr(Expression.Literal expression) {
        if (expression.value == null) return "nil";
        return expression.value.toString();
    }
    @Override
    public String visitLogicalExpr(Expression.Logical expression) {
        return null;
    }
    @Override
    public String visitSetExpr(Expression.Set expression) {
        return null;
    }
    @Override
    public String visitThisExpr(Expression.This expression) {
        return null;
    }
    @Override
    public String visitUnaryExpr(Expression.Unary expression) {
        return parenthesize(expression.operator.lexeme, expression.right);
    }
    @Override
    public String visitDataExpr(Expression.Data expression) {
        return null;
    }
}
