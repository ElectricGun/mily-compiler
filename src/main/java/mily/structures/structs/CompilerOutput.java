package mily.structures.structs;

import mily.codegen.*;
import mily.parsing.*;

public class CompilerOutput {

    protected EvaluatorTree AST;
    protected IRCode outputCode;

    long lexingDuration;
    long astBuildDuration;
    long optimizationDuration;
    long codeGenerationDuration;
    long compileDuration;

    public CompilerOutput(EvaluatorTree ast,
                          IRCode outputCode,
                          long lexingDuration,
                          long astBuildDuration,
                          long optimizationDuration,
                          long codeGenerationDuration,
                          long compileDuration) {
        this.AST = ast;
        this.outputCode = outputCode;
        this.lexingDuration = lexingDuration;
        this.astBuildDuration = astBuildDuration;
        this.optimizationDuration = optimizationDuration;
        this.codeGenerationDuration = codeGenerationDuration;
        this.compileDuration = compileDuration;
    }

    public IRCode getOutputCode() {
        return outputCode;
    }

    public EvaluatorTree getAST() {
        return AST;
    }

    public long getAstBuildDuration() {
        return astBuildDuration;
    }

    public long getCodeGenerationDuration() {
        return codeGenerationDuration;
    }

    public long getCompileDuration() {
        return compileDuration;
    }

    public long getLexingDuration() {
        return lexingDuration;
    }

    public long getOptimizationDuration() {
        return optimizationDuration;
    }
}
