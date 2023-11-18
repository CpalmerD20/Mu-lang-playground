import java.util.List;

public class MyClosure implements MyCallable {
    //environment(parent) ...closure
    private final Statement.Closure declaration; //TODO change to lamda or refactor class to closure
/*
    private final boolean isInitializer;
    private final Environment parent;
    MyClosure(Statement.Closure declaration, Environment parent, boolean isInitializer) {
        this.declaration = declaration;
        this.parent = parent;
        this.isInitializer = isInitializer;
    }
*/
    MyClosure(Statement.Closure declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
//        Environment environment = new Environment(parent);
        Environment environment = new Environment(interpreter.globals);

        for (int i = 0; i < declaration.parameters.size(); i += 1) {
            environment.defineVariable(declaration.parameters.get(i).lexeme, arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return object) {
            return object.value;
        }
//        if (isInitializer) return parent.getAt(0, "this");
        return null;
    }

    @Override
    public String toString() {
        return "::Closure/Method named: " + declaration.name.lexeme + "::";
    }
}
