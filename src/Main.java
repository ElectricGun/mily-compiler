package src;

import java.util.*;
import src.evaluators.*;
import src.tokens.Token;
import static src.structure.Parsing.*;
import static src.structure.Pruning.*;
import static src.constants.Functions.*;

// TODO leading whitespaces causes errors
public class Main {

    public static void main(String[] args) {
        String code = """
           let test_function_1(h, j) {
              return h + j;
           }

           test_function_1(2,4);
           test_function_1(2,3);

           if (test_function_2(4, 2) > 100 + sussy * wussy) {
               let sussy = 1;
           } else if (1) {
               let vent = 5;
           }
           
           while (true) {
               let sus = 2;
           }

           let test_function_2(h, j) {
               return (1 - ((h + j) * 4) + 1) == sus && test_function_1(2, 5) > 10;
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

        System.out.printf("%n---------------\tSyntax Tree\t%n%n");

        Evaluator.printRecursive(node);

        System.out.printf("%n---------------\tSyntax Tree (PRUNED)\t%n%n");

        Evaluator.printRecursive(pruneEmptyOperations(node));
    }
}

