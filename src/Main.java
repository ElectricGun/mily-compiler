package src;

import java.util.*;
import src.constants.*;
import src.evaluators.*;
import src.tokens.Token;
import static src.constants.Functions.*;
import static src.constants.Keys.*;
import static src.structure.Parsing.tokenize;
import static src.structure.Pruning.pruneEmptyOperations;

public class Main {
    public static void main(String[] args) {
        String code = """
               let sus = "among";
               let test_function_1(h, j) {
                  let test = 1;
                  return;
               }
               let test_function_2(h, j) {
                  return (1 - ((h + j) * 4) + 1) == sus && sus > 10;
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

