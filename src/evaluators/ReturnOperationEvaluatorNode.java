package src.evaluators;

import src.tokens.*;
import static src.constants.Keys.*;

public class ReturnOperationEvaluatorNode extends OperationEvaluatorNode {
    public ReturnOperationEvaluatorNode(Token name, int depth) {
        super(name, depth);
    }

    @Override
    public String toString() {
        return "%s %s".formatted( leftSide == null ? "returns " + constantValue : "returns operator", type.equals(KEY_OP_CONSTANT) ? "" : type);
    }
}