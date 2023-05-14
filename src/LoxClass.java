import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
  final String name;
  private final Map<String, LoxFunction> methods;

  LoxClass(String name, Map<String, LoxFunction> methods) {
    this.name = name;
    this.methods = methods;
  }

  public String toString() {
    return name;
  }

  public Object call(Interpreter interpreter, List<Object> arguments) {
    LoxInstance instance = new LoxInstance(this);

    return instance;
  }

  public int arity() {
    return 0;
  }

  public LoxFunction findMethod(String lexeme) {
    if (methods.containsKey(lexeme)) {
      return methods.get(lexeme);
    }

    return null;
  }
}
