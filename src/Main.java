package src;

import java.util.*;

import src.structures.*;
import src.tokens.*;
import src.evaluators.*;

import static src.constants.Functions.*;
import static src.processing.Lexing.*;
import static src.processing.Pruning.*;
import static src.processing.Validation.*;

public class Main {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

        CodeFile code = readFile("tests/main.mily");

        System.out.printf("%n---------------\tInput Code\t%n%n");
        System.out.println(code.getCode());

        long startCompileTime = System.nanoTime();
        System.out.printf("%n---------------\tTokenization\t%n%n");
        List<Token> tokenList = tokenize(code.getCode());
        System.out.println(tokenList);

        System.out.printf("%n---------------\tLogs\t%n%n");
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename());
        evaluatorTree.begin(tokenList);

        System.out.printf("%n---------------\tSyntax Tree\t%n%n");
        evaluatorTree.printRecursive();

        System.out.printf("%n---------------\tValidating Tree\t%n%n");
        boolean doAssignTypes = true;
        validateDeclarations(evaluatorTree, doAssignTypes);

        convertUnariesToBinary(evaluatorTree);

        validateTypes(evaluatorTree);

        System.out.printf("%n---------------\tSyntax Tree (CLEANED)\t%n%n");
        EvaluatorTree pruned = pruneEmptyOperations(evaluatorTree);
        simplifyUnaries(pruned);
        pruned.printRecursive();


        System.out.printf("%n---------------\tSyntax Tree (BINARY)\t%n%n");
        pruned.printRecursive();

        simplifyBinaryExpressions(pruned);

        System.out.printf("%n---------------\tSyntax Tree (PRUNED)\t%n%n");

        pruned.printRecursive();

        long endTime = System.nanoTime();

        long compileDuration = (endTime - startCompileTime);
        long totalDuration = (endTime - startTime);

        System.out.printf("%nCompile time: %sms%nTotal time: %sms%n", compileDuration / 1000000, totalDuration / 1000000);
    }
}
