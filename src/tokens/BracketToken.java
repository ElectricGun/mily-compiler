package src.tokens;

import src.evaluators.OperationEvaluatorNode;

public class BracketToken extends Token {

    private OperationEvaluatorNode operationEvaluatorNode;

    public BracketToken(String string, int line, OperationEvaluatorNode operationEvaluatorNode) {
        super(string, line);

        this.operationEvaluatorNode = operationEvaluatorNode;
    }

    public OperationEvaluatorNode getOperationEvaluator() {
        return operationEvaluatorNode;
    }

    @Override
    public String toString() {
        return operationEvaluatorNode.toString();
    }
}
