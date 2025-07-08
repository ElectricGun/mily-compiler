package src;

import java.util.*;

import src.evaluators.*;
import src.tokens.Token;

import static src.structure.Parsing.tokenize;
import static src.structure.Pruning.pruneEmptyOperations;

public class Main {
    public static void main(String[] args) {
        String code = """
               let test_function_1(h, j) {
                  return h + j;
               }
               
               sus(); suss(2,3);

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

