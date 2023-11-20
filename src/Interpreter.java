import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
//    private final Environment globals = new Environment();
    private Environment environment = new Environment();
    private final Map<Expression, Integer> locals = new HashMap<>();
    //TODO find out if both are needed
    Interpreter() {
        //TODO rename clock(), change define to defineData();
        environment.define("clock", new MyCallable() {
            @Override
            public int arity() {
                return 0;
            }
            @Override
            public Object call(Interpreter interpreter, List<Object> parameters) {
                return (double)System.currentTimeMillis() / 1000;
            }
            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }
    private Object evaluate(Expression e) {
        return e.accept(this);
    }

    public void interpret(List<Statement> phrases) {
        System.out.println("...interpreting...");
        try {
            for (Statement each: phrases) {
                execute(each);
            }
        } catch (InterpreterError error) {
            report(error);
        }
    }
    private void execute(Statement statement) {
        statement.accept(this);
    }

    static void report(InterpreterError error) {
        System.err.println(
            error.getMessage() + "\n[line : " + error.token.line + "]"
        );
        App.hadInterpreterError = true;
    }
    private String stringify(Object subject) {
        if (subject == null) {
            return "void";
        }
        if (subject instanceof Double) {
            String text = subject.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return subject.toString();
    }

    private boolean isEqual(Object ob1, Object ob2) {
        if (ob1 == null && ob2 == null) {
            return true;
        }
        if (ob1 == null || ob2 == null) {
            return false;
        }
        return ob1.equals(ob2);
    }
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double || operand instanceof Long) {
            return;
        }
        throw new InterpreterError(operator, "operand must be a number");
    }
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        if (left instanceof Long && right instanceof Long) {
            return;
        }
        if (left instanceof Double && right instanceof Long) {
            return;
        }
        if (left instanceof Long && right instanceof Double) {
            return;
        }
        throw new InterpreterError(operator, "operand must be a number");
    }

    //TODO add in join expressions
    /*
        if (left instanceof String || right instanceof String)
        return "" + left + right;
    */
    private boolean isTruthy(Object object) {
        //TODO revisit empty sequences like Python? if object == 0 ?
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean) {
            return (boolean)object;
        }
        return true;
    }

    @Override
    public Object visitGroupingExpr(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLiteralExpr(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpr(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case BANG : {
                return !isTruthy(right);
            }
            case MINUS : {
                return -(double)right;
            }
        }
        return null;
    }
    @Override
    public Object visitBinaryExpr(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        if (expression.operator.type == Types.BANG_EQUAL) {
            return !isEqual(left, right);
        }
        if (expression.operator.type == Types.EQUAL_EQUAL) {
            return isEqual(left, right);
        }


        //TODO test if instanceof clauses work

        if (left instanceof Double || right instanceof Double) {
            switch (expression.operator.type) {
                case EXPONENT:
                    checkNumberOperands(expression.operator, left, right);
                    return Math.pow((double) left, (double) right);
                case SLASH:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left / (double) right;
                case STAR:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left * (double) right;
                case MINUS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left - (double) right;
                case PLUS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left + (double) right;
                case GREATER:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left > (double) right;
                case GREATER_EQUAL:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left >= (double) right;
                case LESS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left < (double) right;
                case LESS_EQUAL:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left <= (double) right;
            }
        }
        if (left instanceof Long || right instanceof Long) {
            switch (expression.operator.type) {
                case EXPONENT :
                    checkNumberOperands(expression.operator, left, right);
                    return Math.pow((long) left, (long) right);
                case SLASH :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left / (long) right;
                case STAR :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left * (long) right;
                case MINUS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left - (long) right;
                case PLUS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left + (long) right;
                case GREATER :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left > (long) right;
                case GREATER_EQUAL :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left >= (long) right;
                case LESS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left < (long) right;
                case LESS_EQUAL :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left <= (long) right;
                default : throw new RuntimeException("Arithmetic Error");
            }
        }
        return null;
    }
    @Override
    public Object visitLogicalExpr(Expression.Logical expression) {
        //TODO test
        Object left = evaluate(expression.left);

        if (expression.operator.type == Types.OR) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }
        return evaluate(expression.right);
    }
    @Override
    public Object visitAssignExpr(Expression.Assign expression) {
        Object value = evaluate(expression.value);
        environment.reassign((expression.name), value);
        return value;

        //TODO revisit, for mutable | immutable
    }

    @Override
    public Object visitCallExpr(Expression.Call expression) {
        Object called = evaluate(expression.called);
        if (!(called instanceof MyCallable)) {
            throw new InterpreterError(expression.paren, "Can only call functions, closures, and models.");
        }
        List<Object> parameters = new ArrayList<>();
        for (Expression argument : expression.arguments) {
            parameters.add(evaluate(argument));
        }
        MyCallable function = (MyCallable)called;

        if (parameters.size() != function.arity()) {
            throw new InterpreterError(expression.paren, "Expected " + function.arity() + " parameters but got " + parameters.size());
        }

        return function.call(this, parameters);
    }

    @Override
    public Object visitGetExpr(Expression.Get expression) {
        return null;
    }





    @Override
    public Object visitSetExpr(Expression.Set expression) {
        return null;
    }

    @Override
    public Object visitSelfExpr(Expression.Self expression) {
        return null;
    }

    @Override
    public Object visitDataExpr(Expression.Data expression) {
        return environment.get(expression.name);
    }

    @Override
    public Object visitVariableExpr(Expression.Variable expression) {
        return findVariable(expression.name, expression);
    }

    private Object findVariable(Token name, Expression expression) {
//        Integer distance = locals.get(expression);
//        if (distance != null) {
//            return environment.ancestor(distance, name.lexeme);
//        } else {
//            return environment.get(name);
//        }
        //TODO implement
        return null;
    }

    @Override
    public Void visitBlock(Statement.Block statement) {
        executeBlock(statement.statements, new Environment(environment));
        return null;
    }

    void executeBlock(List<Statement> statements, Environment parent) {
        Environment prior = this.environment;

        try {
            this.environment = parent;
            for (Statement each : statements) {
                execute(each);
            }
        } finally {
            this.environment = prior; //restores environment even if exception occurs
        }
    }

    @Override
    public Void visitModel(Statement.Model statement) {
        return null;
        //TODO implement
    }

    @Override
    public Void visitExpression(Statement.Expr statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitLambda(Statement.LambdaIn statement) {
        MyLambda lambda = new MyLambda(statement);
        environment.define(statement.name.lexeme, lambda);
        return null;
        //TODO should be assignData()
        //TODO test and implement anonymous
    }

    @Override
    public Void visitClosure(Statement.Closure statement) {
        MyClosure closure = new MyClosure(statement, environment);
        environment.define(statement.name.lexeme, closure);
        return null;
    }

    @Override
    public Void visitIf(Statement.If statement) {
        if (isTruthy(evaluate(statement.condition))) {
            execute(statement.ifTrue);
        } else if (statement.ifFalse != null) {
            execute(statement.ifFalse);
        }
        return null;
    }

    @Override
    public Void visitPrint(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return statement) {
        Object value = null;
        if (statement.value != null) {
            value = evaluate(statement.value);
        }

        throw new Return(value);
    }

    @Override
    public Void visitVariable(Statement.Variable statement) {
        Object value = null;
        if (statement.value != null) {
            value = evaluate(statement.value);
        }
        environment.define(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitData(Statement.Data statement) {
        Object value = null;
        if (statement.value != null) {
            value = evaluate(statement.value);
        }
        //TODO revisit immutability
//        environment.assignData(statement.name.lexeme, value);
        environment.define(statement.name.lexeme, value);
        //TODO fix once you get mutability figured out;
        return null;
    }

    @Override
    public Void visitRepeat(Statement.Repeat statement) {
        while (true) for (Statement each : statement.body) {
            if (each instanceof Statement.Until) {
                if (isTruthy(evaluate(((Statement.Until) each).expression))) {
                    return null;
                }
            }
            execute(each);
        }
    }

    @Override
    public Void visitUntil(Statement.Until statement) {
        return null;
    }

    public void resolve(Expression expression, int i) {
    }
}
