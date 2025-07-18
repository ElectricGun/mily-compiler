package src;

import java.util.*;
import src.constants.*;
import src.tokens.*;
import src.evaluators.*;

import static src.constants.Functions.*;
import static src.structure.Lexing.*;
import static src.structure.Pruning.*;
import static src.structure.Validation.*;

public class Main {

    public static void main(String[] args) throws Exception {
        CodeFile code = readFile("tests/main.mily");

        System.out.printf("%n---------------\tInput Code\t%n%n");
        System.out.println(code.getCode());

        System.out.printf("%n---------------\tTokenization\t%n%n");
        List<Token> tokenList = tokenize(code.getCode());
        System.out.println(tokenList);

        System.out.printf("%n---------------\tLogs\t%n%n");
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename());
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
