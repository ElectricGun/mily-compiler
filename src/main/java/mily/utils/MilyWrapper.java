package mily.utils;

import mily.codegen.*;
import mily.parsing.*;
import mily.preprocessing.*;
import mily.structures.structs.*;
import mily.tokens.*;

import java.io.*;
import java.util.*;

import static mily.codegen.CodeGeneration.*;
import static mily.constants.Ansi.*;
import static mily.preprocessing.Lexing.*;
import static mily.processing.Pruning.*;
import static mily.processing.Validation.*;

public class MilyWrapper {

    public boolean debugMode = false;
    public boolean isQuiet = false;

    public MilyWrapper() {
    }

    public MilyWrapper(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public MilyWrapper(boolean debugMode, boolean isQuiet) {
        this.debugMode = debugMode;
        this.isQuiet = isQuiet;
    }

    public CompilerOutput compile(CodeFile code, String cwd) throws Exception {
        long compileStartTime = System.nanoTime();

        // tokenise
        List<Token> tokenList;
        try {
            tokenList = tokenize(code.getCode(), new File(code.getDirectory(), code.getFilename()).getPath(), debugMode);
            tokenList = Preprocess.processIncludes(tokenList, cwd, debugMode);
        } catch (Exception e) {
            // todo: unhardcode this message
            System.out.println(ANSI_ERROR + "MilyLexingError: " + e.getMessage() + ANSI_RESET);
            return null;
        }

        if (debugMode) {
            System.out.println(tokenList);
        }

        // end lexing -- start building ast
        long startAstBuildDuration = System.nanoTime();
        long lexingDuration = (startAstBuildDuration - compileStartTime);

        EvaluatorTree evaluatorTree = new EvaluatorTree(code.getFilename(), debugMode);
        evaluatorTree.begin(tokenList);

        // end ast building -- start optimisation
        long optimizationStartTime = System.nanoTime();
        long astBuildDuration = (optimizationStartTime - startAstBuildDuration);

        if (debugMode) {
            System.out.println("PRE VALIDATION AST (unassigned reference types)");
            evaluatorTree.printRecursive();
            return null;
        }

        // check for syntax errors
        if (checkThrowables(evaluatorTree)) {
            throw new RuntimeException("Failed to compile: syntax error!");
        }
        try {
            boolean doAssignTypes = true;
            removeEmptyOperations(evaluatorTree);
            convertUnariesToBinary(evaluatorTree, debugMode);
            validateDeclarations(evaluatorTree, doAssignTypes, debugMode);
            validateCallers(evaluatorTree, doAssignTypes, debugMode);
            validateFunctionDeclares(evaluatorTree, debugMode);
            // this step is not needed yet
            //pruneNestedUnaries(evaluatorTree, debugMode);
            validateTypes(evaluatorTree, debugMode);
            validateConditionals(evaluatorTree, debugMode);
            invalidateDynamicDatatype(evaluatorTree, debugMode);
            solveBinaryExpressions(evaluatorTree);

        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return null;
        }

        // check for semantic errors
        if (checkThrowables(evaluatorTree)) {
            throw new RuntimeException("Failed to compile: semantic error!");
        }

        // end optimisation -- start code generation
        long codeGenerationStartTime = System.nanoTime();
        long optimizationDuration = (codeGenerationStartTime - optimizationStartTime);

        IRCode irCode;
        irCode = generateIRCode(evaluatorTree, debugMode);

        long endCompileTime = System.nanoTime();
        long codeGenerationDuration = (endCompileTime - codeGenerationStartTime);
        long compileDuration = (endCompileTime - compileStartTime);

        CompilerOutput output = new CompilerOutput(
                evaluatorTree,
                irCode,
                lexingDuration,
                astBuildDuration,
                optimizationDuration,
                codeGenerationDuration,
                compileDuration
        );

        if (!isQuiet) {
            System.out.println("Compilation successful");
        }

        return output;
    }
}
