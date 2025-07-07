package src.constants;

import java.util.*;
import static src.constants.Keys.*;

public class Maps {
    protected final static Map<String, String> punctuationMap = new HashMap<>();
    static {
        punctuationMap.put(KEY_BRACKET_OPEN, "(");
        punctuationMap.put(KEY_BRACKET_CLOSE, ")");
        punctuationMap.put(KEY_CURLY_OPEN, "{");
        punctuationMap.put(KEY_CURLY_CLOSE, "}");
        punctuationMap.put(KEY_SEMICOLON, ";");
        punctuationMap.put(KEY_COMMA, ",");
        punctuationMap.put(KEY_NEWLINE, "\n");
        punctuationMap.put(KEY_SPACE, " ");
        punctuationMap.put(KEY_TAB, "\t");
    }

    protected final static Map<String, String> keywordMap = new HashMap<>();
    static {
        keywordMap.put(KEY_LET, "let");
        keywordMap.put(KEY_RETURN, "return");
    }

    protected final static Map<String, String> operatorMap = new HashMap<>();
    static {
        operatorMap.put(KEY_OP_ASSIGN, "=");
        operatorMap.put(KEY_OP_MUL, "*");
        operatorMap.put(KEY_OP_ADD, "+");
        operatorMap.put(KEY_OP_SUB, "-");
        operatorMap.put(KEY_OP_DIV, "/");
        operatorMap.put(KEY_OP_POW, "^");
        operatorMap.put(KEY_OP_IDIV, "//");
        operatorMap.put(KEY_OP_MOD, "%");

        operatorMap.put(KEY_OP_EQUALS, "==");
        operatorMap.put(KEY_OP_STRICT_EQUALS, "===");
        operatorMap.put(KEY_OP_NOT_EQUAL, "!=");
        operatorMap.put(KEY_OP_AND, "&&");
        operatorMap.put(KEY_OP_LESS_THAN, "<");
        operatorMap.put(KEY_OP_LESS_THAN_EQUALS, "<=");
        operatorMap.put(KEY_OP_GREATER_THAN, ">");
        operatorMap.put(KEY_OP_GREATER_THAN_EQUALS, ">=");
        operatorMap.put(KEY_BOP_SHIFT_LEFT, "<<");
        operatorMap.put(KEY_BOP_SHIFT_RIGHT, ">>");
        operatorMap.put(KEY_BOP_B_AND, "&");
        operatorMap.put(KEY_BOP_XOR, "^");
        operatorMap.put(KEY_BOP_OR, "|");
        operatorMap.put(KEY_OP_NEGATE, "!");
    }

    protected final static Map<String, String> allKeywordMap = new HashMap<>();
    static {
        allKeywordMap.putAll(punctuationMap);
        allKeywordMap.putAll(keywordMap);
        allKeywordMap.putAll(operatorMap);
    }

    protected static final HashMap<String, Integer> PEMDAS = new HashMap<>();
    static {
        // negatives are reserved for special characters
        PEMDAS.put(punctuationMap.get(KEY_BRACKET_OPEN), -4);
        PEMDAS.put(punctuationMap.get(KEY_BRACKET_CLOSE), -3);
        // -2 is reserved for unary operators
        PEMDAS.put(operatorMap.get(KEY_OP_NEGATE), -2);
        // -1 is reserved for constants (default value)
        PEMDAS.put(operatorMap.get(KEY_OP_POW), 0);
        PEMDAS.put(operatorMap.get(KEY_OP_MUL), 1);
        PEMDAS.put(operatorMap.get(KEY_OP_DIV), 1);
        PEMDAS.put(operatorMap.get(KEY_OP_IDIV), 1);
        PEMDAS.put(operatorMap.get(KEY_OP_ADD), 2);
        PEMDAS.put(operatorMap.get(KEY_OP_SUB), 2);
    }
}
