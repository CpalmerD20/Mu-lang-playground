import java.util.List;

public abstract class Expression {
    abstract <R> R accept(Visitor<R> guest);
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

    public static class Assign extends Expression {
        final Token name;
        final Expression value;
        public Assign(Token name, Expression value) {
            this.name = name;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> guest) {
            return guest.visitAssignExpr(this);
        }
    }
    public static class Binary extends Expression {
        final Expression left;
        final Expression right;
        final Token operator;
        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitBinaryExpr(this);
        }
    }
    public static class Call extends Expression {
        final Expression called;
        final Token paren;
        final List<Expression> arguments;
        public Call(Expression called, Token paren, List<Expression> arguments) {
            this.called = called;
            this.paren = paren;
            this.arguments = arguments;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitCallExpr(this);
        }
    }
    public static class Data extends Expression {
        Token name;
        public Data(Token name) {
            this.name = name;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitDataExpr(this);
        }
    }
    public static class Get extends Expression {
        Expression object;
        Token name;
        public Get(Expression object, Token name) {
            this.object = object;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitGetExpr(this);
        }
    }
    public static class Unary extends Expression {
        Token operator;
        Expression right;
        public Unary(Token operator, Expression right) {
            this.operator = operator;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitUnaryExpr(this);
        }
    }
    public static class This extends Expression {
        final Token keyword;
        public This(Token keyword) {
            this.keyword = keyword;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitThisExpr(this);
        }
    }
    public static class Set extends Expression {
        Expression object;
        Token name;
        Expression value;
        public Set(Expression object, Token name, Expression value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitSetExpr(this);
        }
    }
    public static class Grouping extends Expression {
        Expression expression;
        public Grouping(Expression expression) {
            this.expression = expression;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitGroupingExpr(this);
        }
    }
    public static class Literal extends Expression {
        final Object value;
        public Literal(Object value) {
            this.value = value;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitLiteralExpr(this);
        }
    }
    public static class Logical extends Expression {
        final Expression left;
        final Expression right;
        final Token operator;
        public Logical(Expression left, Token operator, Expression right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitLogicalExpr(this);
        }

    }
}
