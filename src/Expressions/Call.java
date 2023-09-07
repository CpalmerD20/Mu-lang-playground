package Expressions;
import java.util.*;
public class Call extends Expression{
    Expression called;
    Token paren;
    List<Expression> arguments;
    public Call(Expression called, Token paren, List<Expression> arguments) {
        this.called = called;
        this.paren = paren;
        this.arguments = arguments;
    }
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitCallExpr(this);
    }
}
