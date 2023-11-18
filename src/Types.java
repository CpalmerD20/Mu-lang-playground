
//TODO add in integer types
public enum Types {
    /* Literals. */
    IDENTIFIER, STRING, FLOAT, EOF,

    /* OPERATORS */
    NOTE_IN, NOTE_OUT, L_PAREN, R_PAREN, L_CURLY, R_CURLY, L_BRACE, R_BRACE,
    // &*     *&         ()                {}               []
    BANG, COMMA, COLON, DOT, EQUAL, SEMICOLON, SLASH, STAR, GREATER, LESS,
    // !    ,      :     .      =       ;       /      *       >       <
    MINUS, PLUS, MODULO, BIT_AND, BIT_X0R, BITSHIFT_R, BITSHIFT_L, DATA,
    // -    +      %        &        ^        >>         <<         #

    /* TWO CHARACTERS */
    BANG_EQUAL, EQUAL_EQUAL, EXPONENT, MU, GREATER_EQUAL, LESS_EQUAL,
    // !=           ==          ^*     />      >=           <=

    // Keywords.
    LAMBDA, CLOSURE,
    //each   define
    AND, OR, AS, IS, IF, ELSE, FALSE, TRUE,
    REPEAT, UNTIL, SKIP, STOP, VOID, RETURN,
    JOIN, MATCH, PRINT, VARIABLE, MODEL, SELF,

}
