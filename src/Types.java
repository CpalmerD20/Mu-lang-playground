enum Types {
    // Literals.
    IDENTIFIER, STRING, FLOAT,
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
    LEFT_NOTE, RIGHT_NOTE,
    LEFT_DO, RIGHT_DO, FUNCTION,
    BIT_AND,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Keywords.
    AND, OR, IF, ELSE, FALSE, TRUE,
    REPEAT, SKIP, STOP, VOID, RETURN,
    PRINT, DATA, MODEL, THIS,

    EOF

}
