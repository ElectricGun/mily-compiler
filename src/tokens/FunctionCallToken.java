package src.tokens;

import src.evaluators.*;

/**
 * A token used to store function calls within expressions
 * @author ElectricGun
 */

public class FunctionCallToken extends Token {

    private FunctionCallEvaluatorNode functionCallEvaluatorNode;

    public FunctionCallToken(String string, int line, FunctionCallEvaluatorNode functionCallEvaluatorNode) {
        super(string, line);

        this.functionCallEvaluatorNode = functionCallEvaluatorNode;
    }

    public FunctionCallEvaluatorNode getNode() {
        return functionCallEvaluatorNode;
    }

    @Override
    public String toString() {
        return functionCallEvaluatorNode.toString();
    }
}
