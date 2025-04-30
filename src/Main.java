package src;
import java.util.ArrayList;
import java.util.List;

import static src.Vars.*;

public class Main {
    public static void main(String[] args) {
        String string = """
                test(skibidi, rizzler, sigma, bruh) {
                    let foo = 10 * 123 / 10;
                    let among = foo * 30;
                    anotherfunction(mmm) {
                        let baz = 10 * mmm;
                    }
                    return 100;
                }
                hehe(rizz) {
                    let sus = 0;
                    let gyatt = 2;
                }
                """;

        String string2 =
                """
                let foo = 1 / 2 + 3 * 4 + 5 ;
                """;

        String string3 =
                """
                euclid() {
                    return 100000;
                }
                
                euler() {
                    return x*y+z+u/v-w;
                }
                """;

        List<String> tokenList = tokenize(string3);
        Evaluator evaluator = new Evaluator();
        Evaluator.EvaluatorNode node = evaluator.begin(tokenList);

        System.out.printf("%n---------------\tInput Code\t%n%n");

        System.out.println(string3);

        System.out.printf("%n---------------\tSyntax Tree (WIP)\t%n%n");

        Evaluator.printRecursive(node);
    }

    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String currentToken = "";
        boolean isInitialized = false;
        char previousChar = 0;
        for (char c : input.toCharArray()) {

            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar) || isOperator("" + previousChar));
            boolean previousIsPunctuation = isInitialized && (isPunctuation(previousChar) || isOperator("" + previousChar));

            if (isWhiteSpace(c)) {
                if (!previousIsWhitespace) {
                    tokens.add(currentToken);
                }
                currentToken = " ";
            }
            else if (isPunctuation(c) || isOperator("" + c)) {
                tokens.add(currentToken);
                currentToken = "" + c;
            } else if (!(isPunctuation(c) || isOperator("" + c)) && previousIsPunctuation) {
                tokens.add(currentToken);
                currentToken = "" + c;
            } else {
                currentToken += c;
            }

            isInitialized = true;
            previousChar = c;
        }
        tokens.add(currentToken);
    return tokens;
    }
}

