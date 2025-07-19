package src.constants;

/**
 * <h1> Class Keywords </h1>
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
        KEY_EMPTY = "",
        KEY_NEWLINE = "\n",
        KEY_SPACE = " ",
        KEY_TAB =   "\t",

    // comment punctuations TODO change these probably
        KEY_COMMENT_MULTILINE_START = "/*",
        KEY_COMMENT_MULTILINE_END = "*/",
        KEY_COMMENT_INLINE = "/-",

    // keywords
        KEY_RETURN = "return",
        KEY_CONDITIONAL_IF = "if",
        KEY_CONDITIONAL_ELSE = "else",
        KEY_LOOPING_WHILE = "while",
        KEY_LOOPING_FOR = "for",
        KEY_BOOLEAN_FALSE = "false",
        KEY_BOOLEAN_TRUE = "true",

    // keywords: datatypes
        KEY_DATA_LET = "let",
        KEY_DATA_INT = "int",
        KEY_DATA_DOUBLE = "double",
        KEY_DATA_STRING = "string",
        KEY_DATA_BOOLEAN = "boolean",
        KEY_DATA_VOID = "void",

    // keywords: other datatypes (not in maps)
        KEY_DATA_UNKNOWN = "key_unknown",

    // operators
        KEY_OP_ASSIGN = "=",
        KEY_OP_MUL = "*",
        KEY_OP_ADD = "+",
        KEY_OP_SUB = "-",
        KEY_OP_DIV = "/",
        KEY_OP_POW = "**",
        KEY_OP_IDIV = "//",
        KEY_OP_MOD = "%",

    // don't bother using these, they are annoying and cause ambiguity
    // just use compound operators instead (like in python)
//        KEY_OP_INCREMENT = "++",
//        KEY_OP_DECREMENT = "--",

    // operators: boolean/comparator
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

    // operator: operation types
        KEY_OP_TYPE_CONSTANT = "key_constant",
        KEY_OP_TYPE_OPERATION = "key_operation",
        KEY_OP_TYPE_GROUP = "key_group",
        KEY_OP_TYPE_CAST = "key_cast";
}
