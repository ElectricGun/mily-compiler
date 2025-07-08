package src.structure;

import src.constants.*;
import src.tokens.*;
import java.util.*;
import static src.constants.Functions.*;
import static src.constants.Keywords.*;

public class Parsing {
    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        String tokenString = "";
        boolean isInitialized = false;
        char previousChar = 0;
        int currentLine = 1;

        for (char c : input.toCharArray()) {
            String cs = String.valueOf(c);
            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar));
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            System.out.printf("line: %s   token: %s   is_partial_operator: %s%n", currentLine, tokenString, isKeywordIncomplete(tokenString));

            if (isWhiteSpace(cs)) {
                if (Functions.equals(KEY_NEWLINE, cs)) {
                    currentLine ++;
                }
                if (!previousIsWhitespace) {
                    tokens.add(new Token(tokenString, currentLine));
                }
                tokenString = " ";
            }
            else if (isPunctuation(c) || isOperator("" + c) && !isKeywordIncomplete(tokenString)) {

                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;
            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation && !isKeywordIncomplete(tokenString)) {
                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;
            } else {
                tokenString += c;
            }

            isInitialized = true;
            previousChar = c;
        }
        tokens.add(new Token(tokenString, currentLine));

        return tokens;
    }
}
