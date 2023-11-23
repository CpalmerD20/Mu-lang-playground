import java.util.List;

public class MyLambda implements MyCallable {
    private final Expression.LambdaFn expression;
    MyLambda(Expression.LambdaFn expression) {
        this.expression = expression;
    }
    @Override
    public int arity() {
        return expression.parameters.size();
    }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment scope = new Environment();
        for (int i = 0; i < expression.parameters.size(); i += 1) {
            scope.define(expression.parameters.get(i).lexeme, arguments.get(i));
        }
        interpreter.executeBlock(expression.body, scope);
        return null;
    }
    @Override
    public String toString() {
        return "::Lambda/Function::";
    }
}