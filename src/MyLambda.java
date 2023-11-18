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
        Environment environment = new Environment(interpreter.globals);

        for (int i = 0; i < declaration.parameters.size(); i += 1) {
            environment.defineVariable(declaration.parameters.get(i).lexeme, arguments.get(i));
        }
        interpreter.executeBlock(declaration.body, environment);
        return null;
    }

    @Override
    public String toString() {
        return "::Lmabda/Function named: " + declaration.name.lexeme + "::";
    }
}