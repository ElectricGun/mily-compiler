package src.tokens;

import src.evaluators.OperationEvaluator;

public class BracketToken extends Token {

    private OperationEvaluator operationEvaluator;

    public BracketToken(String string, int line, OperationEvaluator operationEvaluator) {
        super(string, line);

        this.operationEvaluator = operationEvaluator;
    }

    public OperationEvaluator getOperationEvaluator() {
        return operationEvaluator;
    }

    @Override
    public String toString() {
        return operationEvaluator.toString();
    }
}
