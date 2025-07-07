package src.constants;

import src.tokens.*;
import static src.constants.Maps.*;
import static src.constants.Keys.*;

public class Functions {

    public static boolean equals(String key, String compare) {
        return allKeywordMap.get(key).equals(compare);
    }

    public static boolean equals(String key, Token token) {
        return equals(key, token.string);
    }

    public static boolean equals(String key, char c) {
        return equals(key, String.valueOf(c));
    }

    public static boolean isPunctuation(String compare) {
        for (String s : punctuationMap.values()) {
            if (s.equals(compare)) return true;
        }
        return false;
    }

    public static boolean isPunctuation(char c) {
        return isPunctuation(String.valueOf(c));
    }

    public static boolean isPunctuation(Token t) {
        return isPunctuation(t.string.charAt(0));
    }

    public static boolean isOperator(String c) {
        for (String s : operatorMap.values()) {
            if (c.equals(s)) return true;
        }
        return false;
    }

    public static boolean isOperator(Token t) {
        return isOperator(t.string);
    }

    public static boolean isOperator(char c) {
        return isOperator(String.valueOf(c));
    }

    public static boolean isKeywordIncomplete(String c) {
        if (isWhiteSpace(c) || c.isEmpty())
            return false;
        for (String op : allKeywordMap.values()) {
            boolean partialEquals = op.contains(c) && !op.equals(c);
            if (partialEquals) return true;
        }
        return false;
    }

    public static boolean isKeywordIncomplete(Token t) {
        return isKeywordIncomplete(t.string);
    }

    public static boolean isKeywordIncomplete(char c) {
        return isKeywordIncomplete(String.valueOf(c));
    }


    public static int operationOrder(Token t) {
        return operationOrder(t.string);
    }

    public static boolean isConstant(int order) {
        return order == -1 || order == -4;
    }

    public static int operationOrder(String c) {
        return PEMDAS.getOrDefault(c, -1);
    }

    public static boolean isWhiteSpace(char c) {
        String cs = String.valueOf(c);
        return isWhiteSpace(cs);
    }

    public static boolean isWhiteSpace(Token t) {
        return isWhiteSpace(t.string);
    }

    public static boolean isWhiteSpace(String c) {
        return punctuationMap.get(KEY_SPACE).equals(c) ||
                punctuationMap.get(KEY_TAB).equals(c) ||
                punctuationMap.get(KEY_NEWLINE).equals(c);
    }
}
