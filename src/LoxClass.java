import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;

  LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
    this.name = name;
    this.superclass = superclass;
    this.methods = methods;
  }

  public String toString() {
    return name;
  }

  public Object call(Interpreter interpreter, List<Object> arguments) {
    LoxInstance instance = new LoxInstance(this);

    LoxFunction initializer = findMethod("init");

    if (initializer != null) {
      initializer.bind(instance).call(interpreter, arguments);
    }

    return instance;
  }

  public int arity() {
    LoxFunction initializer = findMethod("init");

    if (initializer != null)
      return initializer.arity();

    return 0;
  }

  public LoxFunction findMethod(String lexeme) {
    if (methods.containsKey(lexeme)) {
      return methods.get(lexeme);
    }

    if (superclass != null) {
      return superclass.findMethod(lexeme);
    }

    return null;
  }
}
