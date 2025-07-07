package src.tokens;

import src.evaluators.*;

public class BracketToken extends TypedToken {

    private final OperationEvaluatorNode operationEvaluatorNode;

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
