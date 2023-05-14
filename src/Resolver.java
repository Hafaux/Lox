import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

enum FunctionType {
  NONE,
  FUNCTION,
  METHOD,
  INITIALIZER
}

enum ClassType {
  NONE,
  CLASS,
  SUBCLASS
}

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();
  private FunctionType currentFunction = FunctionType.NONE;
  private ClassType currentClass = ClassType.NONE;

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  public void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  private void resolve(Stmt statement) {
    statement.accept(this);
  }

  private void resolve(Expr expression) {
    expression.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<String, Boolean>());
  }

  private void endScope() {
    scopes.pop();
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) {
      return;
    }

    Map<String, Boolean> scope = scopes.peek();

    if (scope.containsKey(name.lexeme)) {
      Lox.error(name, "Variable with this name already declared in this scope.");
    }

    scope.put(name.lexeme, false);
  }

  private void define(Token name) {
    if (scopes.isEmpty()) {
      return;
    }

    scopes.peek().put(name.lexeme, true);
  }

  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        interpreter.resolve(expr, scopes.size() - 1 - i);

        return;
      }
    }
  }

  private void resolveFunction(Stmt.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();

    for (Token param : function.params) {
      declare(param);
      define(param);
    }

    resolve(function.body);

    endScope();

    currentFunction = enclosingFunction;
  }

  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();

    resolve(stmt.statements);

    endScope();

    return null;
  }

  public Void visitVarStmt(Stmt.Var stmt) {
    declare(stmt.name);

    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }

    define(stmt.name);

    return null;
  }

  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Lox.error(expr.name, "Cannot read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name);

    return null;
  }

  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);

    resolveLocal(expr, expr.name);

    return null;
  }

  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);

    return null;
  }

  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);

    return null;
  }

  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);

    if (stmt.elseBranch != null) {
      resolve(stmt.elseBranch);
    }

    return null;
  }

  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Cannot return from top-level code.");
    }

    if (stmt.value != null) {
      if (currentFunction == FunctionType.INITIALIZER) {
        Lox.error(stmt.keyword, "Cannot return a value from an initializer.");
      }

      resolve(stmt.value);
    }

    return null;
  }

  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);

    return null;
  }

  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);

    return null;
  }

  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);

    return null;
  }

  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);

    return null;
  }

  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);

    return null;
  }

  public Void visitClassStmt(Stmt.Class stmt) {
    ClassType enclosingClass = currentClass;
    currentClass = ClassType.CLASS;

    declare(stmt.name);
    define(stmt.name);

    if (stmt.superclass != null) {
      if (stmt.superclass.name.lexeme.equals(stmt.name.lexeme)) {
        Lox.error(stmt.name, "A class cannot inherit from itself.");
      }

      currentClass = ClassType.SUBCLASS;

      resolve(stmt.superclass);

      beginScope();
      scopes.peek().put("super", true);
    }

    beginScope();

    scopes.peek().put("this", true);

    for (Stmt.Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;

      resolveFunction(method, declaration);
    }

    endScope();

    if (stmt.superclass != null) {
      endScope();
    }

    currentClass = enclosingClass;

    return null;
  }

  public Void visitGetExpr(Expr.Get expr) {
    resolve(expr.object);

    return null;
  }

  public Void visitSetExpr(Expr.Set expr) {
    resolve(expr.value);
    resolve(expr.object);

    return null;
  }

  public Void visitThisExpr(Expr.This expr) {
    if (currentClass == ClassType.NONE) {
      Lox.error(expr.keyword, "Cannot use 'this' outside of a class.");
    } else {
      resolveLocal(expr, expr.keyword);
    }

    return null;
  }

  public Void visitSuperExpr(Expr.Super expr) {
    if (currentClass == ClassType.NONE) {
      Lox.error(expr.keyword, "Cannot use 'super' outside of a class.");
    } else if (currentClass != ClassType.SUBCLASS) {
      Lox.error(expr.keyword, "Cannot use 'super' in a class with no superclass.");
    } else {
      resolveLocal(expr, expr.keyword);
    }

    return null;
  }
}
