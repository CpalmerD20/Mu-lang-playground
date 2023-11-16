package Expressions;

import Lexer.Types;
public class Interpreter implements Expression.Visitor<Object> {

    private Object evaluate(Expression e) {
        return e.accept(this);
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
    public Object visitGroupingExpr(Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expression) {
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
    public Object visitBinaryExpr(Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        if (expression.operator.type == Types.BANG_EQUAL) {
            return !isEqual(left, right);
        }
        if (expression.operator.type == Types.EQUAL_EQUAL) {
            return isEqual(left, right);
        }

        if (left instanceof String || right instanceof String) {
            throw new RuntimeException("Cannot perform arithmetic on Strings!");
        }

        //TODO test if instanceof clauses work

        if (left instanceof Double || right instanceof Double) {
            switch (expression.operator.type) {
                case EXPONENT:
                    return Math.pow((double) left, (double) right);
                case SLASH:
                    return (double) left / (double) right;
                case STAR:
                    return (double) left * (double) right;
                case MINUS:
                    return (double) left - (double) right;
                case PLUS:
                    return (double) left + (double) right;
                case GREATER:
                    return (double) left > (double) right;
                case GREATER_EQUAL:
                    return (double) left >= (double) right;
                case LESS:
                    return (double) left < (double) right;
                case LESS_EQUAL:
                    return (double) left <= (double) right;
            }
        } else {
            return switch (expression.operator.type) {
                case EXPONENT -> Math.pow((long) left, (long) right);
                case SLASH -> (long) left / (long) right;
                case STAR -> (long) left * (long) right;
                case MINUS -> (long) left - (long) right;
                case PLUS -> (long) left + (long) right;
                case GREATER -> (long) left > (long) right;
                case GREATER_EQUAL -> (long) left >= (long) right;
                case LESS -> (long) left < (long) right;
                case LESS_EQUAL -> (long) left <= (long) right;
//                default -> throw new RuntimeException("Arithmetic Error");
            };
        }
        return null;
    }
    @Override
    public Object visitLogicalExpr(Logical expression) {
        return null;
    }
    @Override
    public Object visitAssignExpr(Assign expression) {
        return null;
    }

    @Override
    public Object visitCallExpr(Call expression) {
        return null;
    }

    @Override
    public Object visitGetExpr(Get expression) {
        return null;
    }





    @Override
    public Object visitSetExpr(Set expression) {
        return null;
    }

    @Override
    public Object visitThisExpr(This expression) {
        return null;
    }



    @Override
    public Object visitDataExpr(Data expression) {
        return null;
    }

}
