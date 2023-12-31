
import java.util.HashMap;
import java.util.Map;
public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }
    Environment(Environment closure) {
        this.enclosing = closure;
    }

    Environment ancestor(int n) {
        Environment environment = this;
        for (int i = 0; i < n; i += 1) environment = environment.enclosing;
        return environment;
    }
    void define(String name, Object value) {
        values.put(name, value);
    }
    void reassign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.reassign(name, value);
            return;
        }
        throw new InterpreterError(name, "Undefined identifier: " + name.lexeme);
    }
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new InterpreterError(name, "Undefined variable: " + name);
    }
    Object getAt(int depth, String name) {
        return ancestor(depth).values.get(name);
    }
    void assignAt(int depth, Token name, Object value) {
        ancestor(depth).values.put(name.lexeme, value);
    }
}
