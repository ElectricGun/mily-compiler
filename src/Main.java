package src;

import java.util.*;
import src.evaluators.*;
import src.tokens.Token;
import static src.structure.Lexing.*;
import static src.structure.Pruning.*;
import static src.structure.Validation.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String code = """
           let numeric = -(((60 + (1 + -3 + 2 + -5 - 3) // -6 / -30 * (92 + 20) - -100 ** -2 / 3)));

           let test_function_1(h, j) {
              return h + j;
           }

           test_function_1(2,4);

           for (let i = 0; i < 100; i = i + 1) {
            print(i);
           }

           let variable_1 = 10;
           let variable_2 = 60 + 10;
           let variable_3 = 2;

           if (test_function_1(4, 2) > 100 + variable_1 * variable_2) {
               variable_1 = 1;

           } else if (true) {
               variable_2 = 5;
           }

           while (true) {
               print(variable_3);
               variable_3 = variable_3 + 1;
           }
                       
           let test_function_2(h, j) {
               return (1 - ((h + j) * 4) + 1) == test_function_2(h, 4) && test_function_1(j, 5) > 10;
           }
           """;

        System.out.printf("%n---------------\tInput Code\t%n%n");
        System.out.println(code);

        System.out.printf("%n---------------\tTokenization\t%n%n");
        List<Token> tokenList = tokenize(code);
        System.out.println(tokenList);

        System.out.printf("%n---------------\tLogs\t%n%n");
        EvaluatorTree evaluatorTree = new EvaluatorTree();
        evaluatorTree.begin(tokenList);

        System.out.printf("%n---------------\tSyntax Tree\t%n%n");
        evaluatorTree.printRecursive();

        System.out.printf("%n---------------\tValidating Tree\t%n%n");
        validateDeclarations(evaluatorTree);

        System.out.printf("%n---------------\tSyntax Tree (CLEANED)\t%n%n");
        EvaluatorTree pruned = pruneEmptyOperations(evaluatorTree);
        pruned.printRecursive();
        simplifyNestedUnaries(pruned);
        convertUnariesToBinary(pruned);
        simplifyBinaryExpressions(pruned);

        System.out.printf("%n---------------\tSyntax Tree (PRUNED)\t%n%n");

        pruned.printRecursive();


    }
}
