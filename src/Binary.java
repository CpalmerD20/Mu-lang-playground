class Binary extends Expression {
    final Expression left;
    final Expression right;
    final Token operator;
    Binary(Expression left, Expression right, Token operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
