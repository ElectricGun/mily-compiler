package src.evaluators;

import src.tokens.Token;

import static src.Vars.OP_CONSTANT;

public class ReturnOperationEvaluator extends OperationEvaluator {
    public ReturnOperationEvaluator(Token name, int depth) {
        super(name, depth);
    }

    @Override
    public String toString() {
        return "%s %s".formatted( leftSide == null ? "returns " + constantValue : "returns operator", type.equals(OP_CONSTANT) ? "" : type);
    }
}