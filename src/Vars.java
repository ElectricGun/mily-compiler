package src;

import java.util.HashMap;

public class Vars {

    public final static char
    // punctuations
    CHAR_BRACKET_OPEN = '(',
    CHAR_BRACKET_CLOSE = ')',
    CHAR_CURLY_OPEN = '{',
    CHAR_CURLY_CLOSE = '}',
    CHAR_SEMICOLON = ';',
    CHAR_COMMA = ',',
    WSP_NEWLINE = '\n',
    WSP_SPACE = ' ',
    WSP_TAB =   '\t'
    ;

    // keywords
    public final static String
    KEYWORD_LET = "let",
    KEYWORD_RETURN = "return",

    // operators
    OP_EQUALS = "=",
    OP_MUL = "*",
    OP_ADD = "+",
    OP_SUB = "-",
    OP_DIV = "/",
    OP_POW = "^",

    // other operation types
    OP_CONSTANT = "constant"
    ;

    public static boolean isPunctuation(char c) {
        char[] puncs = {
                CHAR_BRACKET_CLOSE,
                CHAR_BRACKET_OPEN,
                CHAR_CURLY_CLOSE,
                CHAR_CURLY_OPEN,
                CHAR_SEMICOLON,
                WSP_NEWLINE,
                WSP_SPACE,
                WSP_TAB,
                CHAR_COMMA
        };
        for (char s : puncs) {
            if (c == s) return true;
        }
        return false;
    }

    public static boolean isOperator(String c) {
        String[] ops = {
                OP_MUL,
                OP_ADD,
                OP_DIV,
                OP_POW,
                OP_SUB,
                OP_EQUALS
        };
        for (String s : ops) {
            if (c.equals(s)) return true;
        }
        return false;
    }

    public static int operationOrder(String c) {
        HashMap<String, Integer> pemdas = new HashMap<>();

        pemdas.put(OP_POW, 0);
        pemdas.put(OP_MUL, 1);
        pemdas.put(OP_DIV, 1);
        pemdas.put(OP_ADD, 2);
        pemdas.put(OP_SUB, 2);

        return pemdas.getOrDefault(c, -1);
    }

    public static boolean isWhiteSpace(char c) {
        return c == WSP_SPACE || c == WSP_TAB || c == WSP_NEWLINE;
    }
}
