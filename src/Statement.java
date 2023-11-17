import java.util.List;
public abstract class Statement {
    abstract <R> R accept(Visitor<R> visitor);
    interface Visitor<R> {
        R visitBlock(Block statement);
        R visitModel(Model statement);
        R visitExpression(ExpState statement);
        R visitClosure(Closure statement);
        R visitIf(If statement);
        R visitPrint(Print statement);
        R visitReturn(Return statement);
        R visitVariable(Variable statement);
        R visitData(Data statement);
        R visitRepeat(Repeat statement);

    }

    static class Block extends Statement {
        final List<Statement> statements;
        Block(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlock(this);
        }
    }

    static class Model extends Statement {
        final Token name;
        final List<Statement.Variable> properties;
        final List<Statement.Closure> methods;
        Model(Token name, List<Statement.Variable> properties, List<Statement.Closure> methods) {
            this.name = name;
            this.properties = properties;
            this.methods = methods;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitModel(this);
        }
    }

    static class ExpState extends Statement {
        final Expression expression;
        ExpState(Expression e) {
            this.expression = e;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpression(this);
        }
    }

    static class Closure extends Statement {
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
    static class If extends Statement {
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
    static class Print extends Statement {
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

    static class Return extends Statement {
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
    static class Variable extends Statement {
        final Token name;
        final Expression init;
        Variable (Token name, Expression init) {
            this.name = name;
            this.init = init;
        }
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }
    }

    static class Data extends Statement {
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

    static class Repeat extends Statement {
        //TODO future doesn't have condition
        final Expression condition;
        final Statement body;
        Repeat (Expression condition, Statement body) {
            this.condition = condition;
            this.body = body;
        }
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRepeat(this);
        }
    }
}


