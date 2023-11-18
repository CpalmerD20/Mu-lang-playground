import java.util.List;

public interface FnCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> parameters);
}
