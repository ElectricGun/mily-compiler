package mily;

import java.io.File;
import java.util.*;

import mily.codegen.*;
import mily.preprocessing.*;
import mily.structures.structs.*;
import mily.tokens.*;
import mily.parsing.*;
import mily.utils.*;

import static mily.codegen.CodeGeneration.*;
import static mily.constants.Ansi.*;
import static mily.constants.Functions.*;
import static mily.preprocessing.Lexing.*;
import static mily.processing.Pruning.*;
import static mily.processing.Validation.*;

public class Main {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

        ArgParser argParser = new ArgParser("-");
        argParser.addFlag("--debug", ArgParser.ArgTypes.BOOLEAN);
        argParser.addFlag("--print-ast", ArgParser.ArgTypes.BOOLEAN);
        argParser.addFlag("--benchmark", ArgParser.ArgTypes.BOOLEAN);
        argParser.processFlags(args);

        final boolean debugMode = argParser.getBoolean("--debug");
        final boolean printAst = argParser.getBoolean("--print-ast");
        final boolean printBenchmark = argParser.getBoolean("--benchmark");
        File toRead = new File(argParser.getPositionalArgument());

        // read code
        String fileName = toRead.getName();
        String cwd = toRead.getParent();

        CodeFile code = readFile(cwd, fileName);

        long startCompileTime = System.nanoTime();

        // tokenise
        List<Token> tokenList;
        try {
            tokenList = tokenize(code.getCode(), new File(code.getDirectory(), code.getFilename()).getPath(), debugMode);
            tokenList = Preprocess.processIncludes(tokenList, cwd, debugMode);
        } catch (Exception e) {
            // todo: unhardcode this message
            System.out.println(ANSI_ERROR + "MilyLexingError: " + e.getMessage() + ANSI_RESET);
            return;
        }
        long lexingDuration = (System.nanoTime() - startCompileTime);

        // build ast
        long startAstBuildDuration = System.nanoTime();
        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename(), debugMode);
        evaluatorTree.begin(tokenList);

        long astBuildDuration = (System.nanoTime() - startAstBuildDuration);
        long optimizationTime = System.nanoTime();

        // check for syntax errors
        if (checkThrowables(evaluatorTree)) {
            System.out.println("Failed to compile!");
            return;
        }
//        evaluatorTree.printRecursive();
        removeEmptyOperations(evaluatorTree);
        convertUnariesToBinary(evaluatorTree, debugMode);
        boolean doAssignTypes = true;
        validateDeclarations(evaluatorTree, doAssignTypes, debugMode);
        validateCallers(evaluatorTree, doAssignTypes, debugMode);
        validateFunctionDeclares(evaluatorTree, debugMode);
        // this step is not needed yet
        //pruneNestedUnaries(evaluatorTree, debugMode);
        validateTypes(evaluatorTree, debugMode);
        validateConditionals(evaluatorTree, debugMode);
        solveBinaryExpressions(evaluatorTree);

        // check for semantic errors
        if (checkThrowables(evaluatorTree)) {
            System.out.println("Failed to compile!");
            return;
        }
        long optimizationDuration = (System.nanoTime() - optimizationTime);

        long codeGenerationTime = System.nanoTime();

        IRCode irCode = null;
        try {
            irCode = generateIRCode(evaluatorTree, debugMode);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (printAst) {
                evaluatorTree.printRecursive();
                System.out.println();
            }
        }
        long codeGenerationDuration = (System.nanoTime() - codeGenerationTime);

        long endTime = System.nanoTime();
        long compileDuration = (endTime - startCompileTime);
        long totalDuration = (endTime - startTime);


        System.out.println("Compilation successful");
        System.out.println("Output:");
        System.out.println();
        irCode.printMlog();

        if (printBenchmark) {
            System.out.println();
            System.out.printf(
                    "Lexing time: %sms%n" +
                            "AST building time: %sms%n" +
                            "Optimisation time: %sms%n" +
                            "Code generation time: %sms%n" +
                            "Total compile time: %sms%n" +
                            "Total run time: %sms%n",
                    lexingDuration / 1000000,
                    astBuildDuration / 1000000,
                    optimizationDuration / 1000000,
                    codeGenerationDuration / 1000000,
                    compileDuration / 1000000,
                    totalDuration / 1000000
            );
        }
    }
}
