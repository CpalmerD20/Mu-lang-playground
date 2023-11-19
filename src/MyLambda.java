import java.util.List;

public class MyLambda implements MyCallable {
    private final Statement.LambdaIn declaration;
    MyLambda(Statement.LambdaIn declaration) {
        this.declaration = declaration;
    }
    @Override
    public int arity() {
        return declaration.parameters.size();
    }
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment scope = new Environment();
//          should be able to have it's own environment, not globals
        for (int i = 0; i < declaration.parameters.size(); i += 1) {
            scope.define(declaration.parameters.get(i).lexeme, arguments.get(i));
        }
        interpreter.executeBlock(declaration.body, scope);
        return null;
    }
    @Override
    public String toString() {
        return "::Lambda/Function named: " + declaration.name.lexeme + "::";
    }
}