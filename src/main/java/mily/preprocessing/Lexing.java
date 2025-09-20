package mily.preprocessing;

import mily.constants.*;
import mily.tokens.*;

import java.util.*;

import static mily.constants.Functions.*;
import static mily.constants.Keywords.*;

// TODO this needs a rewrite

/**
 * <h1> Class Lexing </h1>
 * Utility for converting text into a list of tokens.
 *
 * @author ElectricGun
 * @see Keywords
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
    private static String commentBuffer;

    private static String _source;

    private static void init(String input, String source) {
        tokens = new ArrayList<>();
        tokenString = "";
        isInitialized = false;
        previousChar = 0;
        currentLine = 1;
        charArray = input.toCharArray();
        commentMode = false;
        commentModeInline = false;
        commentStartLine = 0;
        commentBuffer = "";
        _source = source;
    }

    private static void tryAddToken(boolean debugMode) {
        if (!commentMode) {
            if (keyEquals(KEY_COMMENT_INLINE, tokenString)) {
                commentModeInline = true;
                commentMode = true;
            } else if (keyEquals(KEY_COMMENT_MULTILINE_START, tokenString)) {
                commentStartLine = currentLine;
                commentMode = true;
            } else {
                if (debugMode)
                    System.out.println("Line " + currentLine + " added: " + tokenString);
                tokens.add(new Token(tokenString, _source, currentLine));
            }
        } else if (keyEquals(KEY_COMMENT_MULTILINE_END, tokenString)) {
            commentModeInline = false;
            commentMode = false;
        }
    }

    public static List<Token> tokenize(String input, String source, boolean debugMode) throws Exception {
        init(input, source);

        for (int index = 0; index < charArray.length; index++) {
            char c = charArray[index];

            String cs = String.valueOf(c);
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            if (debugMode) {
                System.out.printf("line: %s   token: \"%s\"   is_partial_operator: %s    comment_mode: %s  inline: %s%n", currentLine, tokenString, isKeywordIncomplete(tokenString), commentMode, commentModeInline);
            }

            if (keyEquals(KEY_NEWLINE, cs)) {
                currentLine++;
            }

            if (commentMode && !commentModeInline) {
                commentBuffer += c;

                if (!KEY_COMMENT_MULTILINE_END.startsWith(commentBuffer)) {
                    commentBuffer = "" + c;

                } else if (KEY_COMMENT_MULTILINE_END.equals(commentBuffer)) {
                    commentBuffer = "";
                    commentMode = false;
                }

                tokenString = "";

            } else if (isWhiteSpace(cs)) {
                tryAddToken(debugMode);
                tokenString = "" + c;

            } else if (
                // this else if block prevents
                // malformed compound operators from forming
                    isKeywordIncomplete(tokenString) && (
                            isOperator(tokenString) || isPunctuation(tokenString) ||
                                    isOperator(c) || isPunctuation(c)
                    ) &&
                            !isKeywordIncomplete(tokenString + c) &&
                            !isOperator(tokenString + c) &&
                            !isPunctuation(tokenString + c)
            ) {
                tryAddToken(debugMode);
                tokenString = "" + c;

            } else if ((isPunctuation(c) || isOperator("" + c)) && !isKeywordIncomplete(tokenString)) {
                tryAddToken(debugMode);
                tokenString = "" + c;

            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation && !isKeywordIncomplete(tokenString)) {
                tryAddToken(debugMode);
                tokenString = "" + c;

            } else {
                tokenString += c;
            }

            if (index == charArray.length - 1) {
                tryAddToken(debugMode);
            }
            // newline detection for inline comments has to be here
            // because tokens are flushed on the next iteration
            if (keyEquals(KEY_NEWLINE, cs)) {
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
