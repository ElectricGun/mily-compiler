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

        // read code
        CodeFile code = readFile("tests/main.mily");

        long startCompileTime = System.nanoTime();

        // tokenise
        List<Token> tokenList = tokenize(code.getCode(), debugMode);
        long lexingDuration = (System.nanoTime() - startCompileTime);

        // build ast
        long startAstBuildDuration = System.nanoTime();
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename(), debugMode);
        evaluatorTree.begin(tokenList);
        long astBuildDuration = (System.nanoTime() - startAstBuildDuration);

        long optimizationTime = System.nanoTime();

        // check for syntax errors
        if (checkThrowables(evaluatorTree, debugMode))
            return;

//        evaluatorTree.printRecursive();

        removeEmptyOperations(evaluatorTree, debugMode);

        convertUnariesToBinary(evaluatorTree, debugMode);

        evaluatorTree.printRecursive();

        boolean doAssignTypes = true;
        validateDeclarations(evaluatorTree, doAssignTypes, debugMode);

//        evaluatorTree.printRecursive();
        // this step is kinda redundant
//        pruneNestedUnaries(evaluatorTree, debugMode);

        validateFunctions(evaluatorTree, debugMode);

        validateTypes(evaluatorTree, debugMode);

        solveBinaryExpressions(evaluatorTree, debugMode);

        // check for semantic errors
        if (checkThrowables(evaluatorTree, debugMode))
            return;

        long optimizationDuration = (System.nanoTime() - optimizationTime);

        long endTime = System.nanoTime();
        long compileDuration = (endTime - startCompileTime);
        long totalDuration = (endTime - startTime);

//        evaluatorTree.printRecursive();

        System.out.println();

        System.out.printf(
            "Lexing time: %sms%n" +
            "AST building time: %sms%n" +
            "Optimisation time: %sms%n" +
            "Total compile time: %sms%n" +
            "Total run time: %sms%n",
            lexingDuration / 1000000,
            astBuildDuration / 1000000,
            optimizationDuration / 1000000,
            compileDuration / 1000000,
            totalDuration / 1000000
        );
    }
}
