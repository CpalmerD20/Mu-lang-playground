import java.util.List;

public class MyClosure implements MyCallable {
    //environment(parent) ...closure
    //environment() no closure
    private final Statement.Closure declaration; //TODO change to lamda or refactor class to closure
    MyClosure(Statement.Closure declaration) {
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
        return "::Closure/Method named: " + declaration.name.lexeme + "::";
    }
}