package src.structure;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * Parsing utilities
 * @author ElectricGun
 */

public class Lexing {

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String tokenString = "";

        boolean isInitialized = false;
        char previousChar = 0;
        int currentLine = 1;
        char[] charArray = input.toCharArray();

        for (int index = 0; index < charArray.length; index++) {
            char c = charArray[index];

            String cs = String.valueOf(c);
            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar));
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            System.out.printf("line: %s   token: \"%s\"   is_partial_operator: %s%n", currentLine, tokenString, isKeywordIncomplete(tokenString));

            if (isWhiteSpace(cs)) {
                if (Functions.equals(KEY_NEWLINE, cs)) {
                    currentLine++;
                }

                if (!previousIsWhitespace) {
                    tokens.add(new Token(tokenString.trim(), currentLine));
                }
                tokenString = " ";

            } else if (
                    // prevents malformed compound operators from forming
                    isKeywordIncomplete(tokenString) &&
                    (isOperator(tokenString) || isPunctuation(tokenString)) &&
                    !isKeywordIncomplete(tokenString + c) &&
                    !isOperator(tokenString + c)
                    ) {
                tokens.add(new Token(tokenString , currentLine));
                tokenString = "" + c;

            } else if (isPunctuation(c) || isOperator("" + c) && !isKeywordIncomplete(tokenString)) {
                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;

            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation && !isKeywordIncomplete(tokenString)) {
                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;

            } else {
                tokenString += c;
            }

            if (index == charArray.length - 1) {
                tokens.add(new Token(tokenString, currentLine));
            }
            isInitialized = true;
            previousChar = c;
        }
        return tokens;
    }
}
