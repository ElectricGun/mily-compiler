package src.processing;

import src.constants.*;
import src.tokens.*;
import java.util.*;

import static src.constants.Functions.*;
import static src.constants.Keywords.*;

/**
 * <h1> Class Lexing </h1>
 * Utility for converting text into a list of tokens.
 * @see Keywords
 * @author ElectricGun
 */

public class Lexing {

    private static List<Token> tokens;
    private static String tokenString;

    private static boolean isInitialized;
    private static char previousChar;
    private static int currentLine;
    private static char[] charArray;

    private static boolean commentMode;
    private static boolean commentModeInline;
    private static int commentStartLine;

    private static void init(String input) {
        tokens = new ArrayList<>();
        tokenString = "";
        isInitialized = false;
        previousChar = 0;
        currentLine = 1;
        charArray = input.toCharArray();
        commentMode = false;
        commentModeInline = false;
        commentStartLine = 0;
    }

    private static void tryAddToken() {
        if (!commentMode) {
            if (Functions.equals(KEY_COMMENT_INLINE, tokenString)) {
                commentModeInline = true;
                commentMode = true;
            } else if (Functions.equals(KEY_COMMENT_MULTILINE_START, tokenString)) {
                commentStartLine = currentLine;
                commentMode = true;
            }
            else {
                tokens.add(new Token(tokenString, currentLine));
            }
        } else if (Functions.equals(KEY_COMMENT_MULTILINE_END, tokenString)) {
            commentModeInline = false;
            commentMode = false;
        }
    }

    public static List<Token> tokenize(String input) throws Exception {
        init(input);

        for (int index = 0; index < charArray.length; index++) {
            char c = charArray[index];

            String cs = String.valueOf(c);
            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar));
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            System.out.printf("line: %s   token: \"%s\"   is_partial_operator: %s    comment_mode: %s  inline: %s%n", currentLine, tokenString, isKeywordIncomplete(tokenString),  commentMode, commentModeInline);

            if (isWhiteSpace(cs)) {
                if (Functions.equals(KEY_NEWLINE, cs)) {
                    currentLine++;
                }

                if (!previousIsWhitespace) {
                    tryAddToken();
                }
                tokenString = " ";

            } else if (
                // this else if block prevents
                // malformed compound operators from forming
                    isKeywordIncomplete(tokenString) &&
                    (isOperator(tokenString) || isPunctuation(tokenString)) &&
                    !isKeywordIncomplete(tokenString + c) &&
                    !isOperator(tokenString + c) &&
                    !isPunctuation(tokenString + c)
                    ) {
                tryAddToken();
                tokenString = "" + c;

            } else if (isPunctuation(c) || isOperator("" + c) && !isKeywordIncomplete(tokenString)) {
                tryAddToken();
                tokenString = "" + c;

            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation && !isKeywordIncomplete(tokenString)) {
                tryAddToken();
                tokenString = "" + c;

            } else {
                tokenString += c;
            }

            if (index == charArray.length - 1) {
                tryAddToken();
            }
            // newline detection for inline comments has to be here
            // because tokens are flushed on the next iteration
            if (Functions.equals(KEY_NEWLINE, cs)) {
                if (commentMode && commentModeInline) {
                    commentModeInline = false;
                    commentMode = false;
                }
            }
            isInitialized = true;
            previousChar = c;
        }

        if (commentMode && !commentModeInline) {
            throw new Exception("Unclosed comment from line " + commentStartLine);
        }

        return tokens;
    }
}
