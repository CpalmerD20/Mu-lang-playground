import java.util.List;

class InterpreterError extends RuntimeException {
    final Token token;
    InterpreterError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    private Environment environment = new Environment();
    private Object evaluate(Expression e) {
        return e.accept(this);
    }

    public void interpret(List<Statement> phrases) {
        try {
            for (Statement phrase: phrases) {
                execute(phrase);
            }
        } catch (InterpreterError error) {
            report(error);
        }
    }
    private void execute(Statement phrase) {
        phrase.accept(this);
    }

    static void report(InterpreterError error) {
        System.err.println(
            error.getMessage() + "\n[line : " + error.token.line + "]"
        );
//        hadInterpreterError = true;
        //TODO
    }
    private String stringify(Object subject) {
        String text = "";
        if (subject == null) {
            return "void";
        }
        if (subject instanceof Double) {
            text = subject.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }
        return subject.toString();
    }

    private boolean isEqual(Object ob1, Object ob2) {
        if (ob1 == null && ob2 == null) {
            return true;
        }
        if (ob1 == null || ob2 == null) {
            return false;
        }
        return ob1.equals(ob2);
    }
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double || operand instanceof Long) {
            return;
        }
        throw new InterpreterError(operator, "operand must be a number");
    }
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }
        if (left instanceof Long && right instanceof Long) {
            return;
        }
        if (left instanceof Double && right instanceof Long) {
            return;
        }
        if (left instanceof Long && right instanceof Double) {
            return;
        }
        throw new InterpreterError(operator, "operand must be a number");
    }

    //TODO add in join expressions
    /*
        if (left instanceof String || right instanceof String)
        return "" + left + right;
    */
    private boolean isTruthy(Object o) {
        //TODO revisit empty sequences like Python?
        // if o == 0 ?
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (boolean)o;
        }
        return true;
    }

    @Override
    public Object visitGroupingExpr(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLiteralExpr(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpr(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.operator.type) {
            case BANG : {
                return !isTruthy(right);
            }
            case MINUS : {
                return -(double)right;
            }
        }
        return null;
    }
    @Override
    public Object visitBinaryExpr(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        if (expression.operator.type == Types.BANG_EQUAL) {
            return !isEqual(left, right);
        }
        if (expression.operator.type == Types.EQUAL_EQUAL) {
            return isEqual(left, right);
        }


        //TODO test if instanceof clauses work

        if (left instanceof Double || right instanceof Double) {
            switch (expression.operator.type) {
                case EXPONENT:
                    checkNumberOperands(expression.operator, left, right);
                    return Math.pow((double) left, (double) right);
                case SLASH:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left / (double) right;
                case STAR:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left * (double) right;
                case MINUS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left - (double) right;
                case PLUS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left + (double) right;
                case GREATER:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left > (double) right;
                case GREATER_EQUAL:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left >= (double) right;
                case LESS:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left < (double) right;
                case LESS_EQUAL:
                    checkNumberOperands(expression.operator, left, right);
                    return (double) left <= (double) right;
            }
        }
        if (left instanceof Long || right instanceof Long) {
            switch (expression.operator.type) {
                case EXPONENT :
                    checkNumberOperands(expression.operator, left, right);
                    return Math.pow((long) left, (long) right);
                case SLASH :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left / (long) right;
                case STAR :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left * (long) right;
                case MINUS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left - (long) right;
                case PLUS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left + (long) right;
                case GREATER :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left > (long) right;
                case GREATER_EQUAL :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left >= (long) right;
                case LESS :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left < (long) right;
                case LESS_EQUAL :
                    checkNumberOperands(expression.operator, left, right);
                    return (long) left <= (long) right;
                default : throw new RuntimeException("Arithmetic Error");
            }
        }
        return null;
    }
    @Override
    public Object visitLogicalExpr(Expression.Logical expression) {
        return null;
    }
    @Override
    public Object visitAssignExpr(Expression.Assign expression) {
        return null;
    }

    @Override
    public Object visitCallExpr(Expression.Call expression) {
        return null;
    }

    @Override
    public Object visitGetExpr(Expression.Get expression) {
        return null;
    }





    @Override
    public Object visitSetExpr(Expression.Set expression) {
        return null;
    }

    @Override
    public Object visitThisExpr(Expression.This expression) {
        return null;
    }



    @Override
    public Object visitDataExpr(Expression.Data expression) {
        return environment.get(expression.name);
    }

    @Override
    public Void visitBlock(Statement.Block statement) {
        return null;
        //TODO
    }

    @Override
    public Void visitModel(Statement.Model statement) {
        return null;
        //TODO
    }

    @Override
    public Void visitExpression(Statement.ExpState statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void visitClosure(Statement.Closure statement) {
        return null;
        //TODO
    }

    @Override
    public Void visitIf(Statement.If statement) {
        return null;
        //TODO
    }

    @Override
    public Void visitPrint(Statement.Print statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return statement) {
        return null;
        //TODO
    }

    @Override
    public Void visitVariable(Statement.Variable statement) {
        Object value = null;
        if (statement.init != null) {
            value = evaluate(statement.init);
        }
        environment.assignVariable(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitData(Statement.Data statement) {
        Object value = null;
        if (statement.value != null) {
            value = evaluate(statement.value);
        }
        environment.assignData(statement.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitRepeat(Statement.Repeat statement) {
        return null;
        //TODO
    }
}
