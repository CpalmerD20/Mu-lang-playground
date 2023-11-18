public class Token {
    public final Types type;
    public final String lexeme;
    public final Object literal;
    public final int line;
    public Token(Types type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
//        if (type == Types.EOF) {
//            return "";
//        }
        if (literal != null) {
            return type + ": " + "(" + literal + ")\n";
        }
        return type + ": " + lexeme + " " + literal + "\n";
    }
}
