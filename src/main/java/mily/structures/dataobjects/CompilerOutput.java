package mily.structures.dataobjects;

import mily.codegen.*;
import mily.parsing.*;

public class CompilerOutput {

    protected final EvaluatorTree AST;
    protected final IRCode outputCode;

    final long lexingDuration;
    final long astBuildDuration;
    final long optimizationDuration;
    final long codeGenerationDuration;
    final long compileDuration;

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
