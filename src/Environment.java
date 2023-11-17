
import java.util.HashMap;
import java.util.Map;
public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment parent) {
        this.enclosing = parent;
    }

    void assignVariable(String name, Object value) {
        values.put(name, value);

        //TODO lox refers to as 'define'
    }
    void assignData(String name, Object value) {
        if (values.containsKey(name)) {
            throw new RuntimeException("Data point has already been assigned: " + name);
        }
        values.put(name, value);
    }
    void reAssign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if (enclosing != null) {
            enclosing.reAssign(name, value);
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
}
