package src.tokens;

import src.evaluators.FunctionCallEvaluatorNode;

public class FunctionCallToken extends Token {

    private FunctionCallEvaluatorNode functionCallEvaluatorNode;

    public FunctionCallToken(String string, int line, FunctionCallEvaluatorNode functionCallEvaluatorNode) {
        super(string, line);

        this.functionCallEvaluatorNode = functionCallEvaluatorNode;
    }

    public FunctionCallEvaluatorNode getNode() {
        return functionCallEvaluatorNode;
    }

}
