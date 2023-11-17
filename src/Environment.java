
import java.util.HashMap;
import java.util.Map;
public class Environment {
    private static final Map<String, Object> values = new HashMap<>();

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
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        throw new InterpreterError(name, "Undefined variable: " + name);
    }
}
