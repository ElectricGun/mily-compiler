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
        final boolean debugMode = false;

        long startTime = System.nanoTime();
        CodeFile code = readFile("tests/main.mily");

//        System.out.printf("%n---------------\tInput Code\t%n%n");
//        System.out.println(code.getCode());

        long startCompileTime = System.nanoTime();
//        System.out.printf("%n---------------\tTokenization\t%n%n");
        List<Token> tokenList = tokenize(code.getCode(), debugMode);
        long lexingDuration = (System.nanoTime() - startCompileTime);

//        System.out.println(tokenList);

//        System.out.printf("%n---------------\tLogs\t%n%n");

        long startAstBuildDuration = System.nanoTime();
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename(), debugMode);
        evaluatorTree.begin(tokenList);
        long astBuildDuration = (System.nanoTime() - startAstBuildDuration);

        long validationTime = System.nanoTime();
        boolean errored = validateThrowables(evaluatorTree, debugMode);

        if (!errored) {
            boolean doAssignTypes = true;
            validateDeclarations(evaluatorTree, doAssignTypes, debugMode);
            convertUnariesToBinary(evaluatorTree, debugMode);
            validateTypes(evaluatorTree, debugMode);
            long validationDuration = (System.nanoTime() - validationTime);

            long pruningTime = System.nanoTime();
            pruneEmptyOperations(evaluatorTree, debugMode);
            simplifyUnaries(evaluatorTree, debugMode);
            simplifyBinaryExpressions(evaluatorTree, debugMode);
            evaluatorTree.printRecursive();

            long endTime = System.nanoTime();
            long pruningDuration = (endTime - pruningTime);
            long compileDuration = (endTime - startCompileTime);
            long totalDuration = (endTime - startTime);

            System.out.printf(
                    "Lexing time: %sms%n" +
                            "AST building time: %sms%n" +
                            "Semantic validation time: %sms%n" +
                            "Pruning time: %sms%n" +
                            "Total compile time: %sms%n" +
                            "Total run time: %sms%n",
                    lexingDuration / 1000000,
                    astBuildDuration / 1000000,
                    validationDuration / 1000000,
                    pruningDuration / 1000000,
                    compileDuration / 1000000,
                    totalDuration / 1000000
            );
        }
    }
}
