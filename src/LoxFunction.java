import java.util.List;

public class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;

  LoxFunction(Stmt.Function declaration) {
    this.declaration = declaration;
  }

  public int arity() {
    return declaration.params.size();
  }

  public Object call(Interpreter interpreter, List<Object> arguments) {
    Environment environment = new Environment(interpreter.globals);

    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme, arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }

    return null;
  }
}
