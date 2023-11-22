import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Map;
class Resolver implements Expression.Visitor<Void>, Statement.Visitor<Void> {
    private enum Function {
        NONE, LAMBDA, INITIALIZER, CLOSURE
    }
    private final Interpreter interpreter;
    private static final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private Function currentFunction = Function.NONE;

    Resolver(Interpreter scanner) {
        this.interpreter = scanner;
    }
    void resolve(List<Statement> list) {
        for (Statement each : list) {
            resolve(each);
        }
    }
    private void resolve(Statement statement) {
        statement.accept(this);
    }
    private void resolve(Expression expression) {
        expression.accept(this);
    }
    private void resolveLocal(Expression expression, Token token) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(token.lexeme)) {
                interpreter.resolve(expression, scopes.size() - 1 - i);
                return;
            }
        }
    }
    private void resolveFunction(Statement.Closure function) {
        Function enclosing = currentFunction;
        currentFunction = Function.CLOSURE;
        addScope();
        for (Token parameter : function.parameters) {
            declare(parameter);
            define(parameter);
        }
        resolve(function.body);
        scopes.pop();
        currentFunction = enclosing;
    }
    private void resolveFunction(Expression.LambdaFn function) {
        Function enclosing = currentFunction;
        currentFunction = Function.LAMBDA;
        addScope();
        for (Token parameter : function.parameters) {
            declare(parameter);
            define(parameter);
        }
        resolve(function.body);
        scopes.pop();
        currentFunction = enclosing;
    }

    private void addScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() { //TODO maybe remove
        scopes.pop();
    }

    private void declare(Token token) {
        if (scopes.isEmpty()) return;

        Map<String, Boolean> scope = scopes.peek();
        if (scope.containsKey(token.lexeme)) {
            Parser.error(token, "Variable already declared with this name in scope.");
        }
        scope.put(token.lexeme, false);
    }

    private void define(Token token) {
        if (scopes.isEmpty()) return;

        scopes.peek().put(token.lexeme, true);
    }

    @Override
    public Void visitBlock(Statement.Block statement) {
        addScope();
        resolve(statement.statements);
        scopes.pop();
        return null;
    }

    @Override
    public Void visitClosure(Statement.Closure statement) {
        //TODO see Void visitReturn to consider avoiding top level closure
        declare(statement.name);
        define(statement.name);
        resolveFunction(statement);
        return null;
    }
    @Override
    public Void visitVariable(Statement.Variable statement) {
        declare(statement.name);
        if (statement.value != null) {
            resolve(statement.value);
        }
        define(statement.name);
        return null;
    }

    @Override
    public Void visitData(Statement.Data statement) {
        return null;
    }

    @Override
    public Void visitVariableExpr(Expression.Variable expression) {
        if (scopes.isEmpty() && scopes.peek().get(expression.name.lexeme) == Boolean.FALSE) {
            Parser.error(expression.name, "Can't read local variable in its own initializer.");
        }
        resolveLocal(expression, expression.name);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expression.Assign expression) {
        resolve(expression.value);
        resolveLocal(expression, expression.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expression.Binary expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expression.Call expression) {
        resolve(expression.called);

        for (Expression each : expression.arguments) {
            resolve(each);
        }
        return null;
    }

    @Override
    public Void visitGetExpr(Expression.Get expression) {

        resolve(expression.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expression.Grouping expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expression.Literal expression) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expression.Logical expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitSetExpr(Expression.Set expression) {
        return null;
    }

    @Override
    public Void visitIfExpr(Expression.If expression) {
        resolve(expression.condition);
//        resolve(expression.ifTrue);
        if (expression.ifFalse != null) {
            resolve(expression.ifFalse);
        }
        return null;
    }

    @Override
    public Void visitJoinExpr(Expression.Join expression) {
        resolve(expression);
        return null;
    }


    @Override
    public Void visitUnaryExpr(Expression.Unary expression) {
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitDataExpr(Expression.Data expression) {
        return null;
    }

    @Override
    public Void visitExpression(Statement.Expr statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitIf(Statement.If statement) {
        resolve(statement.condition);
        resolve(statement.ifTrue);
        if (statement.ifFalse != null) {
            resolve(statement.ifFalse);
        }
        return null;
    }

    @Override
    public Void visitPrint(Statement.Print statement) {
        resolve(statement.expression);
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return expression) {
        //TODO test using clause to prevent top level closures.
        if (currentFunction == Function.NONE) {
            Parser.error(expression.keyword, "Can't return from top-level code.");
        }
        if (expression.value != null) {
            resolve(expression.value);
        }
        return null;
    }

    @Override
    public Void visitRepeat(Statement.Repeat statement) {
        resolve(statement.body);
        return null;
    }

    @Override
    public Void visitUntil(Statement.Until statement) {
        return null;
    }


    @Override
    public Void visitLambda(Expression.LambdaFn expression) {
        resolveFunction(expression);
        return null;
    }
}
