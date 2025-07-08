package src.evaluators;

import src.tokens.Token;

public class FunctionCallEvaluatorNode extends EvaluatorNode {
    public FunctionCallEvaluatorNode(Token token, int depth) {
        super(token, depth);
    }
}
