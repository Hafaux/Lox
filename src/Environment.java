import java.util.HashMap;
import java.util.Map;

public class Environment {
  final Environment enclosing;

  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  private final Map<String, Object> values = new HashMap<>();

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      Object value = values.get(name.lexeme);

      if (value == null)
        throw new RuntimeError(name, "Uninitialized variable '" + name.lexeme + "'.");

      return value;
    }

    // Recursively search the enclosing environment.
    if (enclosing != null)
      return enclosing.get(name);

    throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
  }

  Object getAt(Integer distance, String name) {
    return ancestor(distance).values.get(name);
  }

  Environment ancestor(Integer distance) {
    Environment environment = this;

    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }

    return environment;
  }

  void define(String name, Object value) {
    values.put(name, value);
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);

      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);

      return;
    }

    throw new RuntimeError(null, "Undefined variable '" + name + "'.");
  }

  public void assignAt(Integer distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme, value);
  }
}
