package src;

import src.evaluators.*;
import src.tokens.Token;

import java.util.*;

import static src.Vars.*;

public class Main {
    public static void main(String[] args) {
        String code = """
                  test_function(x, y) {
                    let amonge = sus * (among + 1);
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
            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar));
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            if (isWhiteSpace(c)) {
                if (WSP_NEWLINE == c) {
                    currentLine ++;
                }
                if (!previousIsWhitespace) {
                    tokens.add(new Token(tokenString, currentLine));
                }
                tokenString = " ";
            }
            else if (isPunctuation(c) || isOperator("" + c)) {
                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;
            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation) {
                tokens.add(new Token(tokenString, currentLine));
                tokenString = "" + c;
            } else {
                tokenString += c;
            }

            System.out.printf("line: %s   token: %s%n",currentLine, tokenString);

            isInitialized = true;
            previousChar = c;
        }
        tokens.add(new Token(tokenString, currentLine));
    return tokens;
    }
}

