import java.util.List;
public abstract class Statement {
    abstract <R> R accept(Visitor<R> visitor);
    interface Visitor<R> {
        R visitBlock(Block statement);
        R visitExpression(Expr statement);
        R visitClosure(Closure statement);
        R visitIf(If statement);
        R visitPrint(Print statement);
//        R visitReturn(Return statement);
        R visitVariable(Variable statement);
        R visitData(Data statement);
        R visitRepeat(Repeat statement);
        R visitUntil(Until statement);
        R visitReturn(Return statement);

    }

    public static class Block extends Statement {
        final List<Statement> statements;
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlock(this);
        }
    }

    public static class Expr extends Statement {
        final Expression expression;
        Expr(Expression e) {
            this.expression = e;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpression(this);
        }
    }

    public static class Until extends Statement {
        final Expression expression;
        Until(Expression e) { this.expression = e; }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUntil(this);
        }
    }

    public static class Closure extends Statement {
        final Token name;
        final List<Token> parameters;
        final List<Statement> body;

        Closure (Token name, List<Token> parameters, List<Statement> body) {
            this.name = name;
            this.parameters = parameters;
            this.body = body;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClosure(this);
        }
    }
    public static class If extends Statement {
        final Expression condition;
        final Statement ifTrue;
        final Statement ifFalse;

        If(Expression condition, Statement ifTrue, Statement ifFalse) {
            this.condition = condition;
            this.ifTrue = ifTrue;
            this.ifFalse = ifFalse;
        }

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIf(this);
        }
    }
    public static class Print extends Statement {
        //TODO remove when language becomes more feature complete
        final Expression expression;
        Print (Expression e) {
            this.expression = e;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrint(this);
        }
    }

    public static class Return extends Statement {
        final Token keyword;
        final Expression value;
        Return (Token keyword, Expression value) {
            this.keyword = keyword;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturn(this);
        }
    }
    public static class Variable extends Statement {
        final Token name;
        final Expression value;
        Variable (Token name, Expression value) {
            this.name = name;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }
    }

    public static class Data extends Statement {
        final Token name;
        final Expression value;
        Data (Token name, Expression value) {
            this.name = name;
            this.value = value;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitData(this);
        }
    }

    public static class Repeat extends Statement {
        final List<Statement> body;
        Repeat (List<Statement> body) {
            this.body = body;
        }
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeat(this);
        }
    }
}


