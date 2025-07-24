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

        long startCompileTime = System.nanoTime();
        List<Token> tokenList = tokenize(code.getCode(), debugMode);
        long lexingDuration = (System.nanoTime() - startCompileTime);

        long startAstBuildDuration = System.nanoTime();
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename(), debugMode);
        evaluatorTree.begin(tokenList);
        long astBuildDuration = (System.nanoTime() - startAstBuildDuration);

        long validationTime = System.nanoTime();

        boolean doAssignTypes = true;

        // check for syntax errors
        if (checkThrowables(evaluatorTree, debugMode))
            return;

        removeEmptyOperations(evaluatorTree, debugMode);

        // todo: unary validation should probably be done in its own method validateUnaries()
        convertUnariesToBinary(evaluatorTree, debugMode);

        validateDeclarations(evaluatorTree, doAssignTypes, debugMode);

        // this step is kinda redundant
//        pruneNestedUnaries(evaluatorTree, debugMode);

        validateTypes(evaluatorTree, debugMode);
        long validationDuration = (System.nanoTime() - validationTime);
        long pruningTime = System.nanoTime();

        solveBinaryExpressions(evaluatorTree, debugMode);

        // check for semantic errors
        if (checkThrowables(evaluatorTree, debugMode))
            return;

        evaluatorTree.printRecursive();

        long endTime = System.nanoTime();
        long pruningDuration = (endTime - pruningTime);
        long compileDuration = (endTime - startCompileTime);
        long totalDuration = (endTime - startTime);

        System.out.println();

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
