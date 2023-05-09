import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private Environment environment = new Environment();

  public void interpret(List<Stmt> expression) {
    try {
      for (Stmt statement : expression) {
        execute(statement);
      }
    } catch (RuntimeError error) {
      Lox.runtimeError(error);
    }
  }

  private void execute(Stmt statement) {
    statement.accept(this);
  }

  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  public Object visitUnaryExpr(Expr.Unary expr) {
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);

        return -(double) right;

      default:
        break;
    }

    // Unreachable.
    return null;
  }

  public Object visitBinaryExpr(Expr.Binary expr) {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type) {
      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left < (double) right;
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return (double) left > (double) right;
      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left >= (double) right;
      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left <= (double) right;
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double)
          return (double) left + (double) right;

        if (left instanceof String && right instanceof String)
          return (String) left + (String) right;

        throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double) left * (double) right;
      case BANG_EQUAL:
        return !isEqual(left, right);
      case EQUAL_EQUAL:
        return isEqual(left, right);

      default:
        break;
    }

    // Unreachable.
    return null;
  }

  private void checkNumberOperand(Token operator, Object operand) throws RuntimeError {
    if (operand instanceof Double)
      return;

    throw new RuntimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) throws RuntimeError {
    if (left instanceof Double && right instanceof Double)
      return;

    throw new RuntimeError(operator, "Operands must be numbers.");
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null)
      return true;
    if (a == null)
      return false;

    return a.equals(b);
  }

  private String stringify(Object object) {
    if (object == null)
      return "nil";

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }

      return text;
    }

    return object.toString();
  }

  private boolean isTruthy(Object object) {
    if (object == null)
      return false;
    if (object instanceof Boolean)
      return (boolean) object;

    return true;
  }

  // TODO: Implement the rest of the methods.
  public Object visitSuperExpr(Expr.Super expr) {
    return null;
  }

  public Object visitThisExpr(Expr.This expr) {
    return null;
  }

  public Object visitSetExpr(Expr.Set expr) {
    return null;
  }

  public Object visitGetExpr(Expr.Get expr) {
    return null;
  }

  public Object visitCallExpr(Expr.Call expr) {
    return null;
  }

  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.get(expr.name);
  }

  public Object visitLogicalExpr(Expr.Logical expr) {
    return null;
  }

  public Object visitAssignExpr(Expr.Assign expr) {
    return null;
  }

  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);

    return null;
  }

  public Void visitPrintStmt(Stmt.Print stmt) {
    Object value = evaluate(stmt.expression);

    System.out.println(stringify(value));

    return null;
  }

  public Void visitVarStmt(Stmt.Var stmt) {
    Object value = null;

    if (stmt.initializer != null)
      value = evaluate(stmt.initializer);

    environment.define(stmt.name.lexeme, value);

    return null;
  }

  public Void visitBlockStmt(Stmt.Block stmt) {
    return null;
  }

  public Void visitIfStmt(Stmt.If stmt) {
    return null;
  }

  public Void visitWhileStmt(Stmt.While stmt) {
    return null;
  }

  public Void visitFunctionStmt(Stmt.Function stmt) {
    return null;
  }

  public Void visitReturnStmt(Stmt.Return stmt) {
    return null;
  }

  public Void visitClassStmt(Stmt.Class stmt) {
    return null;
  }

}