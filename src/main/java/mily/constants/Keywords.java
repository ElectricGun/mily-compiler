package mily.constants;

/**
 * <h1> Class Keywords </h1>
 * Static lexicon of pre-defined keywords
 *
 * @author ElectricGun
 */

@SuppressWarnings("unused")
public class Keywords {

    public final static String

            // punctuations
            KEY_BRACKET_OPEN = "(",
            KEY_BRACKET_CLOSE = ")",
            KEY_CURLY_OPEN = "{",
            KEY_CURLY_CLOSE = "}",
            KEY_SQUARE_OPEN = "[",
            KEY_SQUARE_CLOSE = "]",
            KEY_COLON = ":",
            KEY_AT = "@",
            KEY_HASH = "#",
            KEY_DOLLAR = "$",
            KEY_SEMICOLON = ";",
            KEY_COMMA = ",",
            KEY_EMPTY = "",
            KEY_NEWLINE = "\n",
            KEY_SPACE = " ",
            KEY_TAB = "\t",

    // comment punctuations
    KEY_COMMENT_MULTILINE_START = "/*",
            KEY_COMMENT_MULTILINE_END = "*/",
            KEY_COMMENT_INLINE = "/-",

    // keywords
    KEY_RETURN = "return",
            KEY_CONDITIONAL_IF = "if",
            KEY_CONDITIONAL_ELSE = "else",
            KEY_BREAK = "break",
            KEY_CONTINUE = "continue",
            KEY_LOOPING_WHILE = "while",
            KEY_LOOPING_FOR = "for",
            KEY_LOOPING_REPEAT = "repeat",
            KEY_SECTION = "section",
            KEY_END_SECTION = "endsection",
            KEY_ENDIF = "endif",

    // keywords: macro
    KEY_RAW = "raw",
            KEY_INCLUDE = "include",

    // keywords: booleans
    KEY_BOOLEAN_FALSE = "false",
            KEY_BOOLEAN_TRUE = "true",

    // keywords: datatypes
//        KEY_DATA_DYNAMIC = "let",
    KEY_DATA_INT = "int",
            KEY_DATA_DOUBLE = "double",
            KEY_DATA_STRING = "string",
            KEY_DATA_BOOLEAN = "boolean",
            KEY_DATA_VOID = "void",

    // keywords: variable data
    KEY_MEM = "mem",
            KEY_LOCAL = "local",

    // keywords: other datatypes (not in maps)
    KEY_DATA_UNKNOWN = "unknown_type",

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
            KEY_OP_NEGATE = "!",

    // operators: bitwise
    KEY_BOP_SHIFT_LEFT = "<<",
            KEY_BOP_SHIFT_RIGHT = ">>",
            KEY_BOP_B_AND = "&",
            KEY_BOP_XOR = "^",
            KEY_BOP_OR = "|",

    // operator: operation types
    KEY_OP_TYPE_CONSTANT = "key_constant",
            KEY_OP_TYPE_OPERATION = "key_operation",
            KEY_OP_TYPE_GROUP = "key_group",
            KEY_OP_CAST_EXPLICIT = "key_cast_explicit",
            KEY_OP_CAST_IMPLICIT = "key_op_type_cast_implicit";
}
