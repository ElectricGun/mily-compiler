package src.constants;

import java.util.*;
import static src.constants.Keys.*;

public class Maps {
    protected final static List<String> punctuationKeys = new ArrayList<>(Arrays.asList(
            KEY_BRACKET_OPEN,
            KEY_BRACKET_CLOSE,
            KEY_CURLY_OPEN,
            KEY_CURLY_CLOSE,
            KEY_SEMICOLON,
            KEY_COMMA,
            KEY_NEWLINE,
            KEY_SPACE,
            KEY_TAB
    ));

    protected final static List<String> keywordKeys = new ArrayList<>(Arrays.asList(
            KEY_LET,
            KEY_RETURN
    ));

    protected final static List<String> operatorKeys = new ArrayList<>(Arrays.asList(
            KEY_OP_ASSIGN,
            KEY_OP_MUL,
            KEY_OP_ADD,
            KEY_OP_SUB,
            KEY_OP_DIV,
            KEY_OP_POW,
            KEY_OP_IDIV,
            KEY_OP_MOD,
            KEY_OP_EQUALS,
            KEY_OP_STRICT_EQUALS,
            KEY_OP_NOT_EQUAL,
            KEY_OP_AND,
            KEY_OP_LESS_THAN,
            KEY_OP_LESS_THAN_EQUALS,
            KEY_OP_GREATER_THAN,
            KEY_OP_GREATER_THAN_EQUALS,
            KEY_BOP_SHIFT_LEFT,
            KEY_BOP_SHIFT_RIGHT,
            KEY_BOP_B_AND,
            KEY_BOP_XOR,
            KEY_BOP_OR,
            KEY_OP_NEGATE
    ));

    protected final static List<String> allKeywordKeys = new ArrayList<>();
    static {
        allKeywordKeys.addAll(punctuationKeys);
        allKeywordKeys.addAll(keywordKeys);
        allKeywordKeys.addAll(operatorKeys);
    }


    protected static final HashMap<String, Integer> PEMDAS = new HashMap<>();
    static {
        // negatives are reserved for special characters
        PEMDAS.put(KEY_BRACKET_OPEN, -4);
        PEMDAS.put(KEY_BRACKET_CLOSE, -3);
        // -2 is reserved for unary operators
        PEMDAS.put(KEY_OP_NEGATE, -2);
        // -1 is reserved for constants (default value)
        PEMDAS.put(KEY_OP_POW, 0);
        PEMDAS.put(KEY_OP_MUL, 1);
        PEMDAS.put(KEY_OP_DIV, 1);
        PEMDAS.put(KEY_OP_IDIV, 1);
        PEMDAS.put(KEY_OP_ADD, 2);
        PEMDAS.put(KEY_OP_SUB, 2);
    }
}
