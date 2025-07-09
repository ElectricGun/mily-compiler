package src.constants;

import src.tokens.*;
import static src.constants.Maps.*;
import static src.constants.Keywords.*;

/**
 * Basic utilities
 * @author ElectricGun
 */

public class Functions {

    public static boolean equals(String key, String compare) {
        return key.equals(compare);
    }

    public static boolean equals(String key, Token token) {
        return equals(key, token.string);
    }

    public static boolean equals(String key, char c) {
        return equals(key, String.valueOf(c));
    }

    public static boolean isPunctuation(String compare) {
        for (String s : punctuationKeys) {
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
        for (String s : operatorKeys) {
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
        for (String op : allKeywordKeys) {
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

    public static boolean orderIsConstant(int order) {
        return order == -1 || order == -4;
    }

    public static int operationOrder(String c) {
        return PEMDAS.getOrDefault(c, -1);
    }

    public static boolean isWhiteSpace(String c) {
        return KEY_SPACE.equals(c) ||
                KEY_TAB.equals(c) ||
                KEY_NEWLINE.equals(c);
    }

    public static boolean isWhiteSpace(char c) {
        String cs = String.valueOf(c);
        return isWhiteSpace(cs);
    }

    public static boolean isWhiteSpace(Token t) {
        return isWhiteSpace(t.string);
    }

    public static boolean isKeyWord(String s) {
        return keywordKeys.contains(s);
    }

    public static boolean isKeyWord(Token t) {
        return isKeyWord(t.string);
    }

    public static boolean isKeyWord(char c) {
        return isKeyWord(String.valueOf(c));
    }

    public static boolean isVariableName(String s) {
        return !isKeyWord(s) && !isOperator(s) && !isPunctuation(s) && !isNumeric(s) && s != null;
    }

    public static boolean isVariableName(Token t) {
        if (t == null)
            return false;

        return isVariableName(t.string);
    }

    public static boolean isVariableName(char c) {
        return isVariableName(String.valueOf(c));
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);

            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumeric(Token t) {
        return isNumeric(t.string);
    }

    public static boolean isNumeric(char c) {
        return isNumeric(String.valueOf(c));
    }
}
