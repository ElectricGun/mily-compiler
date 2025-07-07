package src;

import java.util.*;
import src.constants.*;
import src.evaluators.*;
import src.tokens.Token;
import static src.constants.Functions.*;
import static src.constants.Keys.*;

public class Main {
    public static void main(String[] args) {
        String code = """
               let test_function_1(h, j) {
                  return;
               }
                  
               let test_function_2(h, j) {
                  return (h + j) * 4;
               }
               """;

        System.out.printf("%n---------------\tInput Code\t%n%n");

        System.out.println(code);

        System.out.printf("%n---------------\tTokenization\t%n%n");

        List<Token> tokenList = tokenize(code);
        System.out.println(tokenList);

        System.out.printf("%n---------------\tLogs\t%n%n");

        Evaluator evaluator = new Evaluator();
        EvaluatorNode node = evaluator.begin(tokenList);

        System.out.printf("%n---------------\tSyntax Tree (WIP)\t%n%n");

        Evaluator.printRecursive(node);
    }

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

//            tokenString = tokenString.strip();

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

