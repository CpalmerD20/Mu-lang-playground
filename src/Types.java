
//TODO add in integer types
public enum Types {
    /* Literals. */
    EOF, IDENTIFIER, STRING, FLOAT, NOTE_IN, NOTE_OUT,
    //                                        note       ~!
    /* OPERATORS */
     L_PAREN, R_PAREN, L_CURLY, R_CURLY, L_BRACE, R_BRACE,
    //        ()                {}               []
    BANG, COMMA, COLON, DOT, EQUAL, SEMICOLON, SLASH, STAR, GREATER, LESS,
    // !    ,      :     .      =       ;       /      *       >       <
    MINUS, PLUS, MODULO, BIT_AND, BIT_X0R, BIT_NOT, BIT_OR,  DATA,
    // -    +      %        &        ^        ~       |        #

    /* TWO CHARACTERS */
    BANG_EQUAL, EQUAL_EQUAL, EXPONENT, GREATER_EQUAL, LESS_EQUAL, BIT_RIGHT, BIT_LEFT,
    // !=           ==          ^*           >=           <=           >>        <<
    RANGE, COLON_EQ, TIMES_EQ, DIVIDE_EQ,
    // ..     :=          *=       /=
    // Keywords.
    LAMBDA, CLOSURE,
    //each   define
    AND, OR, AS, IS, IF, ELSE, FALSE, TRUE, REPEAT, UNTIL, SKIP, STOP, VOID, RETURN, JOIN, MATCH, PRINT, VARIABLE,

}

// YIELD, APPLY