package src.constants;

import java.io.*;
import java.util.*;

import src.structures.structs.CodeFile;
import src.tokens.*;

import static src.constants.Maps.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class Functions </h1>
 * Basic utilities
 * @author ElectricGun
 */

public class Functions {

    public static CodeFile readFile(String relpath) throws FileNotFoundException {
        File file = new File(relpath);
        String filename = file.getName();

        System.out.println(file.getAbsoluteFile());

        StringBuilder code = new StringBuilder();

        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            code.append(sc.nextLine()).append("\n");
        }
        sc.close();

        return new CodeFile(filename, code.toString());
    }

    public static boolean keyEquals(String key, String compare) {
        return key.equals(compare);
    }

    public static boolean keyEquals(String key, Token token) {
        if (token == null) {
            throw new IllegalArgumentException("token is null");
        }
        return keyEquals(key, token.string);
    }

    public static boolean keyEquals(String key, char c) {
        return keyEquals(key, String.valueOf(c));
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
        return isPunctuation(t.string);
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

    public static boolean isUnaryOperator(String c) {
        for (String s : unaryOperatorKeys) {
            if (c.equals(s)) return true;
        }
        return false;
    }

    public static boolean isUnaryOperator(Token t) {
        return isUnaryOperator(t.string);
    }

    public static boolean isUnaryOperator(char c) {
        return isUnaryOperator(String.valueOf(c));
    }

    public static boolean isKeywordIncomplete(String c) {
        if (isWhiteSpace(c) || c.isEmpty())
            return false;
        for (String op : puncOperatorKeywords) {
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
        return  KEY_SPACE.equals(c) ||
                KEY_TAB.equals(c) ||
                KEY_NEWLINE.equals(c) ||
                KEY_EMPTY.equals(c);
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

    public static boolean isBoolean(String s) {
        return booleanKeys.contains(s);
    }

    public static boolean isBoolean(Token t) {
        return isBoolean(t.string);
    }

    public static boolean isBoolean(char c) {
        return isBoolean(String.valueOf(c));
    }

    public static boolean isDeclarator(String s) {
        return declaratorKeys.contains(s);
    }

    public static boolean isDeclarator(Token t) {
        return isDeclarator(t.string);
    }

    public static boolean isDeclarator(char c) {
        return isDeclarator(String.valueOf(c));
    }

    public static boolean isDeclaratorAmbiguous(String s) {
        return isDeclarator(s) || isVariableName(s);
    }

    public static boolean isDeclaratorAmbiguous(Token t) {
        if (t == null)
            return false;

        return isDeclaratorAmbiguous(t.string);
    }

    public static boolean isDeclaratorAmbiguous(char c) {
        return isDeclaratorAmbiguous(String.valueOf(c));
    }

    public static boolean isVariableName(String s) {
        return !isDeclarator(s) && !isKeyWord(s) && !isOperator(s) && !isPunctuation(s) && !isNumeric(s) && !isBoolean(s) && s != null;
    }

    public static boolean isVariableName(Token t) {
        if (t == null)
            return false;

        return isVariableName(t.string);
    }

    public static boolean isVariableName(char c) {
        return isVariableName(String.valueOf(c));
    }

    public static boolean isReserved(String s) {
        return isKeyWord(s);
    }

    public static boolean isReserved(Token t) {
        return isReserved(t.string);
    }

    public static boolean isReserved(char c) {
        return isReserved(String.valueOf(c));
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
        if (t == null)
            return false;

        return isNumeric(t.string);
    }

    public static boolean isNumeric(char c) {
        return isNumeric(String.valueOf(c));
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);

            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(Token t) {
        if (t == null)
            return false;

        return isInteger(t.string);
    }

    public static boolean isInteger(char c) {
        return isInteger(String.valueOf(c));
    }

    public static String guessValueType(String s) {
        if (s == null)
            return null;

        if (isInteger(s)) {
            return KEY_DATA_INT;

        } else if (isNumeric(s)) {
            return KEY_DATA_DOUBLE;

        } else if (s.startsWith("\"") && s.endsWith("\"")) {
            return KEY_DATA_STRING;

        } else if (s.equals(KEY_BOOLEAN_FALSE) || s.equals(KEY_BOOLEAN_TRUE)) {
            return KEY_DATA_BOOLEAN;
        }
        return KEY_DATA_UNKNOWN;
    }
}
