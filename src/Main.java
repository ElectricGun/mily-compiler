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
                let global_var = 103;
                
                let global_operation = global_var * 2;

                euclid() {
                    return 100000;
                }
                euler() {
                    return x*y+z+u/v-w;
                }
                turing() {return 100;}
                lovelace() {
                    let sdfds = 13123;
                    return a * sdfds;
                }
                """;

        System.out.printf("%n---------------\tInput Code\t%n%n");

        System.out.println(string3);

        System.out.printf("%n---------------\tTokenization\t%n%n");

        List<String> tokenList = tokenize(string3);
        System.out.println(tokenList);

        System.out.printf("%n---------------\tLogs\t%n%n");

        Evaluator evaluator = new Evaluator();
        Evaluator.EvaluatorNode node = evaluator.begin(tokenList);

        System.out.printf("%n---------------\tSyntax Tree (WIP)\t%n%n");

        Evaluator.printRecursive(node);
    }

    public static List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        String currentToken = "";
        boolean isInitialized = false;
        char previousChar = 0;
        for (char c : input.toCharArray()) {

            boolean previousIsWhitespace = isInitialized && (isWhiteSpace(previousChar));
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

            System.out.printf("current: %s%n", currentToken);

            isInitialized = true;
            previousChar = c;
        }
        tokens.add(currentToken);
    return tokens;
    }
}

