package mily.structures.dataobjects;

import mily.codegen.*;
import mily.parsing.*;

public record CompilerOutput(EvaluatorTree AST, IRCode outputCode, long lexingDuration, long astBuildDuration,
                             long optimizationDuration, long codeGenerationDuration, long compileDuration) {

}
