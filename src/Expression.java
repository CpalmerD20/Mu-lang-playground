import java.util.ArrayList;
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
        R visitIfExpr(If expression);
        R visitJoinExpr(Join expression);
        R visitUnaryExpr(Unary expression);
        R visitDataExpr(Data expression);
        R visitVariableExpr(Variable Expression);
        R visitLambda(LambdaFn expression);
    }

    public static class LambdaFn extends Expression {
        final List<Token> parameters;
        final List<Statement> body;
        public LambdaFn(List<Token> parameters, List<Statement> body) {
            this.parameters = parameters;
            this.body = body;
        }
        public LambdaFn(List<Token> parameters, Statement body) {
            this.parameters = parameters;
            this.body = new ArrayList<Statement>();
            this.body.add(body);
        }
        @Override
        <R> R accept(Visitor<R> guest) {
            return guest.visitLambda(this);
        }

        @Override
        public String toString() {
            return "anonymous";
        }
    }

    public static class If extends Expression {
        final Expression condition;
        final Expression ifTrue; //TESTING IF THEY'RE JUST TOKENS
        final Expression ifFalse;

        If(Expression condition, Expression ifTrue, Expression ifFalse) {
            this.condition = condition;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        @Override
        <R> R accept(Visitor<R> guest) {
            return guest.visitIfExpr(this);
        }
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
            this.name = name;
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
    public static class Join extends Expression {
        final ArrayList<String> targets;
        public Join(ArrayList<String> strings) {
            this.targets = strings;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitJoinExpr(this);
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

    public static class Variable extends Expression {
        Token name;
        public Variable(Token name) {
            this.name = name;
        }
        <R> R accept(Visitor<R> guest) {
            return guest.visitVariableExpr(this);
        }
    }
}
