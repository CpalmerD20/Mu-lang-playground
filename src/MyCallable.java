import java.util.List;

public interface MyCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> parameters);
}
