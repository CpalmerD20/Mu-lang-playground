class InterpreterError extends RuntimeException {
    final Environment globals = new Environment();
    final Token token;
    InterpreterError(Token token, String message) {
        super(message);
        this.token = token;
    }
}