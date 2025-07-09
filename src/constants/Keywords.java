package src.constants;

/**
 * Static lexicon of pre-defined keywords
 * @author ElectricGun
 */

public class Keywords {

    public final static String

    // punctuations
        KEY_BRACKET_OPEN = "(",
        KEY_BRACKET_CLOSE = ")",
        KEY_CURLY_OPEN = "{",
        KEY_CURLY_CLOSE = "}",
        KEY_SEMICOLON = ";",
        KEY_COMMA = ",",
        KEY_NEWLINE = "\n",
        KEY_SPACE = " ",
        KEY_TAB =   "\t",

    // keywords
        KEY_LET = "let",
        KEY_RETURN = "return",
        KEY_CONDITIONAL_IF = "if",
        KEY_CONDITIONAL_ELSE = "else",
        KEY_LOOPING_WHILE = "while",
        KEY_LOOPING_FOR = "for",

    // operators
        KEY_OP_ASSIGN = "=",
        KEY_OP_MUL = "*",
        KEY_OP_ADD = "+",
        KEY_OP_SUB = "-",
        KEY_OP_DIV = "/",
        KEY_OP_POW = "**",
        KEY_OP_IDIV = "//",
        KEY_OP_MOD = "%",

    // boolean/comparator operators
        KEY_OP_EQUALS = "==",
        KEY_OP_NOT_EQUAL = "!=",
        KEY_OP_STRICT_EQUALS = "===",
        KEY_OP_AND = "&&",
        KEY_OP_OR = "||",
        KEY_OP_LESS_THAN = "<",
        KEY_OP_LESS_THAN_EQUALS = "<=",
        KEY_OP_GREATER_THAN = ">",
        KEY_OP_GREATER_THAN_EQUALS = ">=",
        KEY_BOP_SHIFT_LEFT = "<<",
        KEY_BOP_SHIFT_RIGHT = ">>",
        KEY_BOP_B_AND = "&",
        KEY_BOP_XOR = "^",
        KEY_BOP_OR = "|",
        KEY_OP_NEGATE = "!",

    // operator types
        KEY_OP_TYPE_CONSTANT = "key_constant",
        KEY_OP_TYPE_GROUP = "key_group";
}
